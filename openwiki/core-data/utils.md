---
type: package reference
title: ActivityProvider
description: Reference for com.core.data.utils.ActivityProvider. Covers the process-wide object that registers ActivityLifecycleCallbacks, exposes the application context, and tracks the most recent FragmentActivity as currentActivity and activityFlow. Includes its init contract, lifecycle filtering, and the managers that depend on it.
tags: [core-data, activity, lifecycle, utils, context]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T12:11:14.549Z
sources:
  - id: openwiki-source-03fcc4056003dc2f567db8f7
    resource: repo://app/src/main/java/com/baseapplication/App.kt
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-f657300c43b408062c1260b1
    resource: repo://core/data/src/main/java/com/core/data/utils/ActivityProvider.kt
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
  - id: openwiki-source-7a8a9bf9712d2c5d9bff4f29
    resource: repo://core/presentation/src/main/java/com/core/presentation/CoreApp.kt
generated: { by: "claude-code", at: "2026-10-26T12:11:14.549Z" }
---

# ActivityProvider (`com.core.data.utils`)

## Purpose and when to use it

`ActivityProvider` is a Kotlin `object` (not Hilt-injected) that lets data-layer code reach:

- the **application `Context`** (`applicationContext`), used by `PermissionManagerImpl.hasPermission`;
- the **current foreground `FragmentActivity`** (`currentActivity`), used to launch activity results and to check permission rationale;
- a **`Flow` of activities** (`activityFlow`), used by `ActivityResultManagerImpl` to re-register launchers after recreation.

Feature code rarely needs it directly. Prefer `IActivityResultManager` / `IPermissionManager`. In Hilt-managed code, inject
`@ApplicationContext Context` instead of using `ActivityProvider.applicationContext`.

## Package layout

```
com/core/data/utils/
└── ActivityProvider.kt
```

There are no DI bindings. It's accessed statically.

## Initialization contract

`fun init(application: Application)` stores the application as `_applicationContext` and calls
`application.registerActivityLifecycleCallbacks(this)`.

It must be called once, early in `Application.onCreate()`. In this repo, `core/presentation`'s `open class CoreApp : Application()`
does it, and `app`'s `App : CoreApp()` inherits that. In tests, `BaseTest.setup()` calls it with the `HiltTestApplication`.
If it isn't called:
- `applicationContext` throws `UninitializedPropertyAccessException` (it's a `lateinit` backing field);
- `currentActivity` is always `null`, and `activityFlow` never emits.

## Type reference

`object ActivityProvider : Application.ActivityLifecycleCallbacks`

| Member | Type | Semantics |
|---|---|---|
| `applicationContext` | `Context` | The `Application` passed to `init`. |
| `activityFlow` | `Flow<FragmentActivity>` | `_activityFlow.asStateFlow().distinctUntilChanged { old, new -> old.get() === new.get() }.mapNotNull { it.get() }.filter { it.lifecycle.currentState.isAtLeast(INITIALIZED) }`. It's hot-backed: a new collector immediately receives the latest tracked activity if it's still alive and not destroyed, then each **different** activity instance. |
| `currentActivity` | `FragmentActivity?` | The latest tracked activity if it's still referenced and its lifecycle is at least `INITIALIZED` (that is, not `DESTROYED`). Otherwise `null`. |
| `init(application)` | `Unit` | See above. |

Internal state: `_activityFlow = MutableStateFlow(WeakReference<FragmentActivity>(null))`. The `WeakReference` avoids leaking activities.

Lifecycle callbacks:

| Callback | Behaviour |
|---|---|
| `onActivityCreated`, `onActivityStarted`, `onActivityResumed` | If the activity `is FragmentActivity`, set `_activityFlow.value = WeakReference(it)`. Non-`FragmentActivity` activities are **ignored**. |
| `onActivityPaused`, `onActivityStopped`, `onActivitySaveInstanceState`, `onActivityDestroyed` | No-op. A destroyed activity is hidden by the lifecycle filter rather than cleared. |

So the tracked activity is the **most recent** `FragmentActivity` to be created, started or resumed. When navigating back from B to A, A's
`onStart`/`onResume` makes A current again. A stopped (backgrounded) activity remains "current" until it's destroyed.

## Data flow

<!-- openwiki: mermaid parse failed and this diagram was converted to a text fence so it does not break rendering. Fix the diagram source and restore the mermaid fence. Parser error: Heuristic: an unescaped angle bracket inside a label breaks rendering; rephrase the label. -->
```text
flowchart LR
    App[Application.onCreate -> CoreApp] -->|init| AP[ActivityProvider]
    A[FragmentActivity lifecycle] -->|created/started/resumed| AP
    AP -->|currentActivity| PM[PermissionManagerImpl rationale]
    AP -->|applicationContext| PM2[PermissionManagerImpl.hasPermission]
    AP -->|currentActivity + activityFlow| AR[ActivityResultManagerImpl]
```

## Dependents in core/data

- `ActivityResultManagerImpl`: `currentActivity` (it returns `null` if absent) and `activityFlow` (`mapLatest` re-registration). See [activity-result](activity-result.md).
- `PermissionManagerImpl`: `applicationContext` for `checkSelfPermission`, and `currentActivity` for `shouldShowRequestPermissionRationale`. See [permission](permission.md).
- Indirectly, everything built on those: `FileManagerImpl`, `NotificationsManagerImpl` and `DownloadManagerImpl`.

## Usage examples

```kotlin
// Application
@HiltAndroidApp
class App : CoreApp()            // CoreApp.onCreate() calls ActivityProvider.init(this)

// Rare direct use (prefer the managers)
val activity: FragmentActivity? = ActivityProvider.currentActivity
ActivityProvider.activityFlow.first()   // suspends until a live FragmentActivity exists
```

## Gotchas and constraints

- **Only `FragmentActivity` is tracked.** In this repository `MainActivity : BaseActivity` and `BaseActivity : ComponentActivity`
  (core/presentation), which is **not** a `FragmentActivity`. As a result, in the shipped app `currentActivity` is `null`,
  `IActivityResultManager.requestResult` returns `null`, and permission requests resolve to `Denied` (or fall back to current grants).
  To use the activity-based managers, make the host activity extend `FragmentActivity`/`AppCompatActivity`, or widen `ActivityProvider`
  to `ComponentActivity` (`ActivityResultManagerImpl` only needs `ComponentActivity` APIs; `PermissionManagerImpl` uses `ActivityCompat`).
- `activityFlow` waits indefinitely when no live activity exists (for example while the app is in the background after all activities are destroyed).
- `init` registers callbacks every time it's called. Call it once per `Application`.
- Not thread-confined: `StateFlow` updates happen on the main thread (lifecycle callbacks), and reads are safe from any thread, but the
  returned activity must only be used on the main thread.

## Testing

`ActivityProviderTest` (`@HiltAndroidTest`, `BaseTest`) checks that `init` stores the context, that a Robolectric `FragmentActivity` becomes
`currentActivity` and the first `activityFlow` value and is reported `null` after `destroy()`, and that a plain `Activity` is ignored.

## Related pages

- [Activity results](activity-result.md) · [Permissions](permission.md) · [Overview](overview.md) · [Recipes](recipes.md)
