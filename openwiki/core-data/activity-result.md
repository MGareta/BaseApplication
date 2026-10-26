---
type: package reference
title: Activity results
description: How com.core.data.activityResult turns AndroidX ActivityResultContracts into suspend functions callable from any layer, including the registry, SavedStateRegistry and recreation mechanics and every convenience extension.
tags: [core-data, activity-result, coroutines, android, hilt]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-d2e6a80001c9a08605ad669c
    resource: repo://core/data/src/main/java/com/core/data/activityResult/di/ActivityResultModule.kt
  - id: openwiki-source-adb835f40633da33b1530a6d
    resource: repo://core/data/src/main/java/com/core/data/activityResult/manager/ActivityResultManagerImpl.kt
  - id: openwiki-source-9c8dca96896b728201b9fe4a
    resource: repo://core/data/src/main/java/com/core/data/activityResult/utils/Extensions.kt
  - id: openwiki-source-f657300c43b408062c1260b1
    resource: repo://core/data/src/main/java/com/core/data/utils/ActivityProvider.kt
  - id: openwiki-source-abb1f279258670e2248cdcce
    resource: repo://core/data/src/test/java/com/core/data/activityResult/ActivityResultTest.kt
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Activity results (`com.core.data.activityResult`)

## Purpose and when to use it

`IActivityResultManager` lets non-UI code (use cases, repositories, other managers) launch any
`androidx.activity.result.contract.ActivityResultContract` and **suspend until the result is delivered**,
without holding a reference to an Activity or registering launchers in `onCreate`.
The manager finds the foreground activity through [`ActivityProvider`](utils.md) and registers a
one-shot launcher on that activity's `ActivityResultRegistry`.

Use it for:
- runtime permissions (the [`permission`](permission.md) package is built on top of it),
- camera/video capture, contact picking, document/content pickers, SAF trees,
- `IntentSender` flows (for example in-app updates, Google sign-in) and arbitrary `startActivityForResult` intents.

Do **not** use it:
- when there is no `FragmentActivity` in the foreground (it returns `null` immediately, see below);
- from code that cannot suspend (all APIs are `suspend`);
- for concurrent requests of the same kind (see *Gotchas*).

## Package layout

```
com/core/data/activityResult/
├── di/
│   └── ActivityResultModule.kt        # Hilt @Module providing IActivityResultManager
├── manager/
│   ├── IActivityResultManager.kt      # public contract
│   └── ActivityResultManagerImpl.kt   # registry + saved-state implementation
└── utils/
    └── Extensions.kt                  # 15 typed suspend extensions over common contracts
```

## Dependency injection

| Module | Component | Binding | Scope |
|---|---|---|---|
| `com.core.data.activityResult.di.ActivityResultModule` (`object`, `@Module`) | `SingletonComponent` | `providesActivityResultManager(): IActivityResultManager = ActivityResultManagerImpl()` | `@Singleton` |

`ActivityResultManagerImpl` has no constructor dependencies. It reads `ActivityProvider` (a Kotlin `object`)
statically, so `ActivityProvider.init(application)` must have been called (done by `CoreApp` in
`core/presentation`).

Injection in a feature:

```kotlin
class PickAvatarUseCase @Inject constructor(
    private val activityResultManager: IActivityResultManager,
) {
    suspend operator fun invoke(): Uri? = activityResultManager.getContent(mimeType = "image/*")
}
```

Other core managers that consume it: `PermissionManagerImpl` (via `PermissionModule`).

## Type reference

### `IActivityResultManager`

`com.core.data.activityResult.manager.IActivityResultManager` – public interface.

```kotlin
suspend fun <I, O, C : ActivityResultContract<I, O>> requestResult(
    contract: C,
    input: I
): O?
```

| Parameter | Meaning |
|---|---|
| `contract` | Any `ActivityResultContract<I, O>` instance (built-in `ActivityResultContracts.*` or a custom one). Its `javaClass.simpleName` is used as the identity for state restoration. |
| `input` | The contract input passed to `launcher.launch(input)`. |
| **returns** | The contract output `O`, or `null` when no activity was available at call time. |

Suspends until the registry callback fires. It is cancellable: cancelling the calling coroutine unregisters the launcher
(the `finally` block calls `launcher?.unregister()`). It doesn't throw for "no activity". Exceptions thrown by
`launch` (for example `ActivityNotFoundException` for an intent nobody handles) propagate to the caller.

### `ActivityResultManagerImpl`

`com.core.data.activityResult.manager.ActivityResultManagerImpl` – public class, implements `IActivityResultManager`. No constructor parameters, no annotations (it's constructed manually in the Hilt module).

State:
- `keyIncrement: AtomicInteger(0)` – monotonically increasing counter used to build registry keys `"result_<n>"`.
- `pendingResult: String?` – simple class name of the contract currently in flight. It's `null` when idle.

File-private constants:

| Constant | Value | Use |
|---|---|---|
| `SAVED_STATE_REGISTRY_KEY` | `"activityresult_saved_state"` | Key of the `SavedStateProvider` registered on the activity. |
| `PENDING_RESULT_KEY` | `"pending"` | Bundle entry with the pending contract's simple name. |
| `LAST_INCREMENT_KEY` | `"key_increment"` | Bundle entry with the last issued key increment. |

#### `requestResult` algorithm

1. `ActivityProvider.currentActivity?.calculateKey(contract)`. If there's no current activity (or it's below `Lifecycle.State.INITIALIZED`), it **returns `null` immediately**.
2. `calculateKey` consumes the restored bundle for `SAVED_STATE_REGISTRY_KEY` from the activity's `SavedStateRegistry`:
   - If a bundle exists **and** its `"pending"` value equals `contract.javaClass.simpleName`, it returns `(isLaunched = true, key = "result_<saved increment>")`. This is a request that was already launched before the activity was recreated.
   - Otherwise it returns `(isLaunched = false, key = "result_<keyIncrement.getAndIncrement()>")`.
3. It sets `pendingResult = contract.javaClass.simpleName`.
4. It collects `ActivityProvider.activityFlow` with `mapLatest` and takes `.first()`. For each emitted activity:
   - If it hasn't launched yet, `prepareSavedData()` registers a `SavedStateProvider` that writes
     `bundleOf("pending" to pendingResult, "key_increment" to keyIncrement.get() - 1)`. If a provider is already
     registered under the key it does nothing. If registration throws `IllegalArgumentException`, it clears and retries.
   - It registers `activityResultRegistry.register(key, contract) { result -> ... }`. This is the overload **without** a
     `LifecycleOwner`, so it can be registered at any lifecycle state and must be unregistered manually.
   - It launches only if `!isLaunched`, then sets `isLaunched = true`.
   - The callback clears `pendingResult`, unregisters the saved-state provider (`clearSavedStateData()`), and resumes the continuation with the result.
   - `finally { launcher?.unregister() }` always unregisters the launcher, both on result and on cancellation.
5. Because of `mapLatest`, if `activityFlow` emits a **different** activity instance while waiting (for example after a configuration change), the
   in-flight block is cancelled (unregistering the old launcher), and the block re-runs on the new activity. It registers
   the **same key** without relaunching (`isLaunched` is already `true`). `ActivityResultRegistry` then delivers the pending result to the new registration.

Private helpers (all `ComponentActivity` extensions): `calculateKey`, `prepareSavedData`, `registerSavedData`, `clearSavedStateData`.
`clearSavedStateData` unregisters the provider and consumes any restored state under the key.

### `utils/Extensions.kt` (top-level suspend extensions on `IActivityResultManager`)

All are thin wrappers that pick an `ActivityResultContracts.*` contract and map `null` to a default:

| Function | Contract | Input passed | Return (default when `null`) |
|---|---|---|---|
| `requestPermission(permission: String): Boolean` | `RequestPermission()` | `permission` | `Boolean` (`false`) |
| `requestPermissions(vararg permission: String): Map<String, Boolean>?` | `RequestMultiplePermissions()` | `arrayOf(*permission)` | nullable map (no default) |
| `takePicturePreview(): Bitmap?` | `TakePicturePreview()` | `null` | `Bitmap?` |
| `takePicture(destination: Uri): Boolean` | `TakePicture()` | `destination` | `Boolean` (`false`) |
| `captureVideo(destination: Uri): Boolean` | `CaptureVideo()` | `destination` | `Boolean` (`false`) |
| `pickContact(): Uri?` | `PickContact()` | `null` | `Uri?` |
| `getContent(mimeType: String): Uri?` | `GetContent()` | `mimeType` | `Uri?` |
| `getMultipleContents(mimeType: String): List<Uri>` | `GetMultipleContents()` | `mimeType` | `List<Uri>` (`emptyList()`) |
| `openDocument(mimeType: String): Uri?` | `OpenDocument()` | `arrayOf(mimeType)` | `Uri?` |
| `openMultipleDocuments(mimeTypes: Array<String>): List<Uri>` | `OpenMultipleDocuments()` | `mimeTypes` | `List<Uri>` (`emptyList()`) |
| `openDocumentTree(startingLocation: Uri? = null): Uri?` | `OpenDocumentTree()` | `startingLocation` | `Uri?` |
| `createDocument(fileName: String, mimeType: String): Uri?` | `CreateDocument(mimeType = mimeType)` | `fileName` | `Uri?` |
| `startIntentSender(intentSenderRequest: IntentSenderRequest): ActivityResult?` | `StartIntentSenderForResult()` | `intentSenderRequest` | `ActivityResult?` |
| `startActivityForResult(intent: Intent): ActivityResult?` | `StartActivityForResult()` | `intent` | `ActivityResult?` |

Note that `requestPermission` returns `false` both when the user denies **and** when no activity was available.
`requestPermissions` returns `null` in the no-activity case, and `PermissionManagerImpl` falls back to `hasPermission` checks in that case.

## Data flow

```mermaid
sequenceDiagram
    participant UC as Caller (use case)
    participant M as ActivityResultManagerImpl
    participant AP as ActivityProvider
    participant A as FragmentActivity
    participant R as ActivityResultRegistry
    UC->>M: requestResult(contract, input)
    M->>AP: currentActivity
    alt no activity
        M-->>UC: null
    else activity present
        M->>A: calculateKey (consume saved state)
        M->>AP: activityFlow.mapLatest
        AP-->>M: activity
        M->>A: prepareSavedData (SavedStateProvider)
        M->>R: register(key, contract, callback)
        M->>R: launcher.launch(input)
        Note over A,R: user interacts; config change re-emits activity -> re-register same key, no relaunch
        R-->>M: callback(result)
        M->>A: clearSavedStateData
        M->>R: unregister
        M-->>UC: result
    end
```

## Usage examples

```kotlin
// Generic contract
val output: ActivityResult? = activityResultManager.requestResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    input = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)),
)

// Typed helpers
val granted: Boolean = activityResultManager.requestPermission(Manifest.permission.CAMERA)
val photoSaved: Boolean = activityResultManager.takePicture(destination = fileProviderUri)
val docs: List<Uri> = activityResultManager.openMultipleDocuments(arrayOf("application/pdf"))
val created: Uri? = activityResultManager.createDocument(fileName = "report.pdf", mimeType = "application/pdf")
```

## Extension guide

- **New typed helper:** add a top-level `suspend fun IActivityResultManager.xxx(...)` in `utils/Extensions.kt` that calls
  `requestResult(contract = ..., input = ...)` and maps `null` to a sensible default (`false`, `emptyList()`) when the
  output type is non-nullable. Add a test in `ActivityResultTest` that mocks `requestResult` with `coEvery` and asserts the contract type and input.
- **Custom contract:** implement `ActivityResultContract<I, O>` and pass it to `requestResult`. Give it a unique class
  name, because restoration matching uses `javaClass.simpleName`. Anonymous objects have empty or ambiguous simple names.

## Gotchas and constraints

- **`ActivityProvider.init` is mandatory.** Without it `currentActivity` is `null` and every call returns `null`. Only `FragmentActivity` instances are tracked.
- **Doesn't work in the app as currently wired.** `MainActivity` extends `core/presentation`'s `BaseActivity`, which extends
  `ComponentActivity`, not `FragmentActivity`. `ActivityProvider` ignores it, so `currentActivity` stays `null` and
  every `requestResult` call (and every helper extension) returns `null` or its default without showing any UI. Fix it by making the host activity
  a `FragmentActivity`/`AppCompatActivity`, or by widening `ActivityProvider` to track `ComponentActivity`. See
  [ActivityProvider](utils.md) and [CoreApp and BaseActivity](../core-presentation/app-and-activity.md).
- **Single in-flight request.** `pendingResult` and the saved-state provider are single shared slots on a singleton.
  Overlapping requests overwrite `pendingResult`, so state restoration for the earlier request is lost. Serialize requests.
- **Process death.** The suspended coroutine doesn't survive process death. The saved bundle allows a *new* call with
  the same contract class after recreation to re-attach to the original registry key (`isLaunched = true`) and receive the
  pending result instead of launching again.
- **Main thread.** Registry registration and `launch` touch activity APIs. Call from the main dispatcher (for example `viewModelScope`).
- **Test coverage gap:** `ActivityResultTest` covers only the extension functions against a mocked
  `IActivityResultManager`. `ActivityResultManagerImpl` itself (key restoration, `mapLatest` re-registration) has no unit test.

## Related pages

- [ActivityProvider](utils.md): source of `currentActivity` / `activityFlow`.
- [Permissions](permission.md): built on `requestPermission(s)`.
- [Recipes](recipes.md): launching for results in features.
- [Testing](testing.md)
- [CoreApp and BaseActivity](../core-presentation/app-and-activity.md): the host activity type.
