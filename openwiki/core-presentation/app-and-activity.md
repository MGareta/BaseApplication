---
type: component reference
title: CoreApp and BaseActivity
description: Reference for core/presentation's application and activity base classes. CoreApp initializes ActivityProvider. BaseActivity is the single-activity host that sets edge-to-edge and night mode, applies Theme, renders the Navigation3 NavDisplay with bottom-sheet/dialog scenes and graph-scoped ViewModels, shows the floating bottom nav bar, and bridges session commands.
tags: [core-presentation, activity, application, navigation3, session, edge-to-edge, hilt]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
  - id: openwiki-source-7a8a9bf9712d2c5d9bff4f29
    resource: repo://core/presentation/src/main/java/com/core/presentation/CoreApp.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# CoreApp and BaseActivity

## Purpose and when to use it

The app is a **single-activity Compose app**:

- `com.core.presentation.CoreApp` is the `Application` base class. The app's `App : CoreApp()` (`app/.../App.kt`).
- `com.core.presentation.base.activity.BaseActivity` is the abstract host activity. The app's `MainActivity : BaseActivity()` (`app/.../MainActivity.kt`)
  is the only activity in the app manifest (`launchMode="singleTop"`, `screenOrientation="portrait"`, `windowSoftInputMode="adjustResize"`).

Features never create activities. They contribute navigation graphs and entries (see [navigation3](navigation3.md)) that `BaseActivity` renders.

## Package layout

```
com/core/presentation/
├── CoreApp.kt
└── base/activity/BaseActivity.kt
```

## `CoreApp`

```kotlin
open class CoreApp : Application() {
    override fun onCreate() { super.onCreate(); ActivityProvider.init(this) }
}
```
It initializes core/data's [`ActivityProvider`](../core-data/utils.md). The app subclass must also be annotated `@HiltAndroidApp` (done in `App`).

## `BaseActivity`

`@AndroidEntryPoint abstract class BaseActivity : ComponentActivity()`

### Injected members (all `dagger.Lazy`, field-injected)

| Field | Type | Provided by |
|---|---|---|
| `navigationGraphFactory` | `dagger.Lazy<navigation3.factory.INavigationGraphFactory>` | `navigation3.di.NavigationModule` (`ActivityRetainedComponent`, `@ActivityRetainedScoped`) |
| `navigationGraphManager` | `dagger.Lazy<navigation3.manager.INavigationGraphManager>` | same |
| `sessionManager` | `dagger.Lazy<ISessionManager>` | core/data `SessionModule` (`@Singleton`) |

These are `lateinit var` and public, so subclasses can use them (for example `navigationGraphManager.get().goToDestination(...)`).

### `onCreate(savedInstanceState)`, in order

1. `AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)`.
2. `enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT), navigationBarStyle = SystemBarStyle.light(TRANSPARENT, TRANSPARENT))`.
   Both bars are transparent and use the **light** style (dark icons) regardless of theme.
3. API 29+: `window.isNavigationBarContrastEnforced = false`.
4. `super.onCreate(...)` (Hilt injection happens here).
5. `setContent { ... }`:
   - `remember { DialogSceneStrategy<INavigationDestination>() }` and `remember { BottomSheetSceneStrategy<INavigationDestination>() }`.
   - `LaunchedEffect(sessionManager)` collects `sessionManager.get().commands.distinctUntilChanged().collectLatest { ... }` and dispatches
     `SessionCommand.Warning` → `onSessionWarning()` and `SessionCommand.TimeOut` → `onSessionTimeout()`. This collector sits **outside** `Theme`.
   - `Theme { ... }` (see [theme](theme.md)):
     - `val scrollConnection = rememberBottomNavBarScrollConnection()` (defaults: not inline, 48 dp threshold, `OnScrollDown`).
     - `val bottomNavGraphs = remember { navigationGraphFactory.get().navigationGraphBottomNavBar }`.
     - `CompositionLocalProvider(LocalBottomNavBarScrollConnection provides scrollConnection)` wraps a full-size `Box` containing:
       1. **`NavDisplay`** (Navigation3) with
          - `backStack = navigationGraphManager.get().backStack` (a `SnapshotStateList<INavigationDestination>`),
          - `sceneStrategies = listOf(bottomSheetStrategy, dialogStrategy)`. When neither matches, the default single-pane scene is used.
          - `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberSharedViewModelStoreNavEntryDecorator(navigationGraphManager.get(), navigationGraphFactory.get().navigationGraphsWithDestinations))`,
          - `entryProvider = entryProvider { navigationGraphFactory.get().register(scope = this) }`.
          No `onBack` is passed, so back handling relies on `NavDisplay`'s default behaviour for a mutable back stack. Verify this against the Navigation3 version in use.
       2. **Bottom nav bar**: `AnimatedContent` aligned `BottomCenter`, padded by `WindowInsets.navigationBars` and 16 dp horizontally,
          with `targetState = navigationGraphFactory.get().getNavigationGraphFromDestination(backStack.last())` and a vertical slide transition.
          If the current destination's graph `is INavigationGraphBottomNavBar`, it renders
          `BottomNavBarComponent(modifier = Modifier, bottomNavGraphs, selectedNavGraph = it, scrollConnection, onTabSelected = { })`.

### Session hooks

| Member | Kind | Default |
|---|---|---|
| `override fun onUserInteraction()` | override | `lifecycleScope.launch { sessionManager.get().restartSessionTimeout() }` on every touch or key. |
| `abstract fun onSessionWarning()` | abstract | Must be implemented (for example show a "session about to expire" dialog). |
| `open fun onSessionTimeout()` | open | `lifecycleScope.launch { sessionManager.get().invalidateSession() }`. Call `super` if you override it. |

See [core-data session](../core-data/session.md) for timer semantics (5 s timeout, single-consumer channel).

## UI composition diagram

<!-- openwiki: mermaid parse failed and this diagram was converted to a text fence so it does not break rendering. Fix the diagram source and restore the mermaid fence. Parser error: Heuristic: an unescaped angle bracket inside a label breaks rendering; rephrase the label. -->
```text
flowchart TD
    A[BaseActivity.setContent] --> L[LaunchedEffect: session commands]
    A --> T[Theme]
    T --> P[CompositionLocalProvider LocalBottomNavBarScrollConnection]
    P --> B[Box fillMaxSize]
    B --> N[NavDisplay backStack + scenes + decorators + entryProvider]
    N --> F[INavigationGraphFactory.register -> INavigationEntry.Render]
    B --> C[AnimatedContent graph of backStack.last]
    C -->|graph is INavigationGraphBottomNavBar| BN[BottomNavBarComponent]
```

## Usage example (app module)

```kotlin
@HiltAndroidApp
class App : CoreApp()

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onSessionWarning() {
        navigationGraphManager.get().goToDestination(SessionWarningDestination)   // e.g. an INavigationEntryDialog
    }

    override fun onSessionTimeout() {
        super.onSessionTimeout()                                                  // invalidates the session
        navigationGraphManager.get().goToDestination(LoginDestination)
    }
}
```

The current `MainActivity` implements both hooks with `TODO(...)`. Because a session warning arrives about 3 s after `startSession`,
**any app that starts a session will crash with `NotImplementedError`** until these are implemented.

## Extension guide

- **Wire tab selection:** replace the empty `onTabSelected = { }` with navigation to the tab graph's start destination, for example
  `navigationGraphManager.get().goToDestination(startDestinationOf(tab))`. There's currently no API mapping an `INavigationGraphBottomNavBar` to its
  start destination. Use `navigationGraphFactory.get().navigationGraphsWithDestinations[tab as INavigationGraph]`.
- **Extra overlays (snackbars, global loaders):** add them inside the `Box` after `NavDisplay`.
- **Additional scene strategies** (for example a list-detail pane): add them to `sceneStrategies` before the dialog strategy.

## Gotchas and constraints

- **`BaseActivity` is a `ComponentActivity`, not a `FragmentActivity`.** core/data's `ActivityProvider` only tracks `FragmentActivity`, so
  `IActivityResultManager` and `IPermissionManager` requests can't reach this activity: they return `null`/`Denied`
  (see [activity-result](../core-data/activity-result.md), [permission](../core-data/permission.md)).
- **Bottom-nav tabs don't navigate:** `onTabSelected = { }`. Tapping a tab only re-expands the bar (the component calls `scrollConnection.expand()`).
- **Unregistered destinations crash the activity:** `getNavigationGraphFromDestination` throws `IllegalArgumentException("Invalid destination")`
  when `backStack.last()` doesn't belong to any registered graph. That includes destinations whose graph has no `@ClassKey`-matched entry set.
- **Status/navigation bar icons are always dark** (`SystemBarStyle.light`), which may be unreadable in dark theme.
- Screens must handle insets themselves (edge-to-edge). The demo uses `windowInsetsPadding(NavigationBarDefaults.windowInsets)` plus
  `Modifier.bottomNavBarPadding()` and `nestedScroll(LocalBottomNavBarScrollConnection.current)`.
- `distinctUntilChanged()` on session commands suppresses consecutive identical commands (see core-data session).
- The legacy `navigation/` package isn't used here (see [navigation-legacy](navigation-legacy.md)).

## Related pages

- [Navigation 3](navigation3.md)
- [Bottom nav bar and toolbars](components-navigation.md)
- [Theme](theme.md)
- [core-data session](../core-data/session.md)
- [core-data ActivityProvider](../core-data/utils.md)
- [Overview](overview.md)
