---
type: package reference
title: Session management
description: Reference for com.core.data.session. Covers ISessionManager, which holds the logged-in user/token in the memory cache, tracks Active/Inactive state, and runs an inactivity CountDownTimer that emits Warning and TimeOut SessionCommands through a conflated channel, plus how core/presentation's BaseActivity consumes them.
tags: [core-data, session, authentication, timeout, coroutines, hilt]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T12:11:14.549Z
sources:
  - id: openwiki-source-18cc41ac67eeca6a221490bb
    resource: repo://core/data/src/main/java/com/core/data/session/di/SessionModule.kt
  - id: openwiki-source-56c08b42e976bdf84a8338f8
    resource: repo://core/data/src/main/java/com/core/data/session/manager/SessionManagerImpl.kt
  - id: openwiki-source-96e649b3e8d794f0bd618089
    resource: repo://core/data/src/main/java/com/core/data/session/utils/Constants.kt
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
generated: { by: "claude-code", at: "2026-10-26T12:11:14.549Z" }
---

# Session (`com.core.data.session`)

## Purpose and when to use it

`ISessionManager` represents the authenticated session:

- `startSession(user, token)` marks the session `Active`, stores `SessionData` in the **memory cache**, and starts an inactivity timer.
- The timer emits `SessionCommand.Warning` shortly before expiry and `SessionCommand.TimeOut` at expiry on `commands: Flow<SessionCommand>`.
- `restartSessionTimeout()` resets the timer (called on user interaction). `invalidateSession()` logs out.
- `getCurrentSession()` returns the stored `SessionData` (for example to add the token to requests).

It's in-memory only: the session doesn't survive process death. Persist refresh tokens yourself (for example AES-encrypted in the disk cache)
if you need auto-login.

## Package layout

```
com/core/data/session/
├── di/SessionModule.kt
├── manager/
│   ├── ISessionManager.kt
│   └── SessionManagerImpl.kt
├── model/
│   ├── SessionCommand.kt     # Warning, TimeOut
│   ├── SessionData.kt        # @Serializable(user, token)
│   └── SessionState.kt       # Active, Inactive
└── utils/Constants.kt        # SESSION_CACHE_KEY
```

## Dependency injection

`SessionModule` (`object`, `@InstallIn(SingletonComponent::class)`):
`@Singleton @Provides fun providesSessionManager(cacheManager: ICacheManager): ISessionManager = SessionManagerImpl(cacheManager)`.

`BaseActivity` (core/presentation) injects it as `dagger.Lazy<ISessionManager>`. Features usually inject `ISessionManager` directly:

```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: ISessionManager,
)
```

## Type reference

### `model/SessionState` (sealed interface)
`data object Active`, `data object Inactive`.

### `model/SessionCommand` (sealed interface)
| Subtype | Emitted when |
|---|---|
| `data object Warning` | On a timer tick where `millisUntilFinished <= 3000` (`COUNTDOWN_WARNING`). With a 5 s total and 3 s interval, that's the tick at about 3 s, roughly 2 s before timeout. |
| `data object TimeOut` | When the timer finishes (`COUNTDOWN_TIMEOUT` = 5 s after the last start/restart). |

### `model/SessionData`
`@InternalSerializationApi @Serializable data class SessionData(val user: String, val token: String)`. Opt in to `InternalSerializationApi` to use it.

### `utils/Constants`
`object Constants { val SESSION_CACHE_KEY = "SESSION" + Uuid.random().toHexString() }` gives a random key per process, so the cache entry can't be
guessed or collide across processes. It's only meaningful with `MEMORY_CACHE`.

### `manager/ISessionManager`
```kotlin
var sessionState: SessionState
val commands: Flow<SessionCommand>
suspend fun startSession(user: String, token: String)
suspend fun invalidateSession()
suspend fun restartSessionTimeout()
suspend fun getCurrentSession(): ApiResult<SessionData>
```
The interface is annotated `@OptIn(InternalSerializationApi::class)`.

### `manager/SessionManagerImpl`
Constructor: `(cacheManager: ICacheManager)`. File constants: `COUNTDOWN_TIMEOUT = 5 * 1000L`, `COUNTDOWN_WARNING = 3 * 1000L`.

- `_commands = Channel<SessionCommand>(capacity = Channel.CONFLATED)`. `commands` returns `_commands.receiveAsFlow()`, which is a **hot, single-consumer**
  flow. Each element goes to one collector, and only the latest unconsumed command is kept.
- `countDownTimer` is a lazy `android.os.CountDownTimer(5000, 3000)`. `onTick` sends `Warning` if `millisUntilFinished <= 3000`, and `onFinish` sends `TimeOut` (via `trySend`).
- `sessionState` starts as `SessionState.Inactive`. It's a public `var`, so callers can also set it.
- `startSession(user, token)`: `countDownTimer.start()`, sets `Active`, then `cacheManager.cacheData(MEMORY_CACHE, ImmortalCacheConfig(SESSION_CACHE_KEY), SessionData(user, token))`.
- `invalidateSession()`: `countDownTimer.cancel()`, sets `Inactive`, then `cacheManager.deleteData(MEMORY_CACHE, SESSION_CACHE_KEY)`.
- `restartSessionTimeout()`: no-op when `Inactive`. Otherwise `cancel()` + `start()`.
- `getCurrentSession()`: `cacheManager.getData(MEMORY_CACHE, SESSION_CACHE_KEY, SessionData::class)`. That's `Success(SessionData)` or `NoContent`.

## Data flow and presentation integration

<!-- openwiki: mermaid parse failed and this diagram was converted to a text fence so it does not break rendering. Fix the diagram source and restore the mermaid fence. Parser error: Heuristic: an unescaped angle bracket inside a label breaks rendering; rephrase the label. -->
```text
sequenceDiagram
    participant UI as BaseActivity
    participant SM as SessionManagerImpl
    participant T as CountDownTimer
    participant C as ICacheManager (MEMORY)
    UI->>SM: startSession(user, token) (via login use case)
    SM->>T: start (5s)
    SM->>C: cacheData(SESSION_CACHE_KEY, SessionData)
    UI->>SM: restartSessionTimeout() on every onUserInteraction
    T-->>SM: onTick(<=3s left)
    SM-->>UI: commands: Warning -> onSessionWarning()
    T-->>SM: onFinish
    SM-->>UI: commands: TimeOut -> onSessionTimeout()
    UI->>SM: invalidateSession() (default onSessionTimeout)
    SM->>C: deleteData(SESSION_CACHE_KEY)
```

`BaseActivity` (`core/presentation/.../base/activity/BaseActivity.kt`):
- In `setContent`, `LaunchedEffect(sessionManager) { sessionManager.get().commands.distinctUntilChanged().collectLatest { ... } }`
  dispatches `Warning` → `abstract fun onSessionWarning()` and `TimeOut` → `open fun onSessionTimeout()` (default: `lifecycleScope.launch { invalidateSession() }`).
- `onUserInteraction()` → `lifecycleScope.launch { restartSessionTimeout() }`.

## Usage examples

```kotlin
// Login
authRepository.login(email, password).onSuccess { dto ->
    sessionManager.startSession(user = dto.userId, token = dto.accessToken)
}

// Auth header (OkHttp interceptor runs off the main thread; runBlocking is acceptable for an in-memory read)
class AuthInterceptor @Inject constructor(private val sessionManager: ISessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { sessionManager.getCurrentSession().getOrNull()?.token }
        val request = chain.request().newBuilder().apply {
            token?.let { header("Authorization", "Bearer $it") }
        }.build()
        return chain.proceed(request)
    }
}

// Guard
if (sessionManager.sessionState == SessionState.Inactive) navigateToLogin()
```

## Extension guide

- **Configurable timeouts:** the constants are file-private. To make them configurable, add constructor parameters with
  defaults and pass them from `SessionModule`. Update `SessionManagerTest`, which advances the main looper by the hard-coded durations.
- **Extra session fields:** extend `SessionData` (it's `@Serializable`) and `startSession`'s signature.
- **More commands** (for example `Refresh`): add a subtype to `SessionCommand`, emit it with `_commands.trySend`, and handle it in `BaseActivity`'s `when`.

## Gotchas and constraints

- **The timeout is 5 seconds** (warning about 2 s before). That's almost certainly a development placeholder: adjust it before production.
- **Single consumer:** don't collect `commands` in more than one place. A second collector steals commands from `BaseActivity`.
- **`BaseActivity` applies `distinctUntilChanged()`** to the collected flow, so a second `Warning` that directly follows a previous `Warning`
  (the user interacted after the first warning and the timer ran down again) is filtered out and `onSessionWarning()` isn't called again.
  `TimeOut` after a `Warning` still goes through.
- **Main looper required:** `CountDownTimer` creates its `Handler` from the thread that first touches the lazy timer. Call `startSession` from the
  main thread (for example from `viewModelScope`) the first time.
- **Not persisted:** process death loses `sessionState` and `SessionData`. The cache key is random per process.
- `sessionState` is a public mutable `var`, so prefer the methods over setting it directly.

## Testing

`SessionManagerTest` (`@HiltAndroidTest`, `BaseTest`) covers: initial `Inactive` with no cached session, `startSession` activating and caching data,
`invalidateSession` deactivating and removing data, the timer emitting `Warning` then `TimeOut` (using `shadowOf(getMainLooper()).idleFor(...)`), and
`restartSessionTimeout` being a no-op when inactive and resetting an active timer.

## Related pages

- [Cache](cache.md) (`MEMORY_CACHE`) · [Recipes](recipes.md) · [Overview](overview.md) (presentation integration) · [Testing](testing.md)
