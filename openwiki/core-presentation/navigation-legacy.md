---
type: package reference
title: Legacy Navigation Compose stack
description: Reference for com.core.presentation.navigation, the older Navigation Compose based stack. Covers graphs, typed entries for screens, dialogs and bottom sheets, the NavigationManager/NavigationIntent direction channel, argument helpers, NavGraphBuilder registration, stability wrappers and the custom Material3 bottom-sheet navigator. Nothing in the app currently uses this stack; BaseActivity runs on navigation3.
tags: [core-presentation, navigation, navigation-compose, legacy, bottom-sheet, hilt]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
  - id: openwiki-source-fa3f21287e473ab9ea0b5af1
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/screen/BaseFlow.kt
  - id: openwiki-source-89701c6bba4493fb1de0687e
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/di/NavigationModule.kt
  - id: openwiki-source-0accb637bcc6ec9ef73f4f00
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/manager/NavigationManagerImpl.kt
  - id: openwiki-source-d4ce43f8a166c100e0c0d1d8
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/arguments/NavGraphEntryArguments.kt
  - id: openwiki-source-34b9ef3d57b2f31c3c54ccdd
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/arguments/ViewModelArguments.kt
  - id: openwiki-source-60d6ef93390fb45a130b2c84
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/directions/NavigationIntent.kt
  - id: openwiki-source-730f75e1ea33225aec34ed87
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/utils/NavigationGraphBuilderExtensions.kt
  - id: openwiki-source-0cf78a6d6374c91a5eb8c322
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/utils/navigator/bottomSheet/BottomSheetHost.kt
  - id: openwiki-source-080875d54994ba50ebf4724e
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/utils/navigator/bottomSheet/BottomSheetNavigator.kt
  - id: openwiki-source-d502552da26384e7c0195b9e
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation/model/utils/StabilityWrappers.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Legacy Navigation Compose stack (`com.core.presentation.navigation`)

## Status: not used

`BaseActivity` renders the **Navigation3** stack ([navigation3](navigation3.md)). Nothing in `app/`, `demo/` or `core/presentation`
references this package except `base/screen/BaseFlow.kt` and the internal `coroutines/Utils.kt` helper used by `BottomSheetHost`. No `NavHost` is set up anywhere.
**New features must use Navigation3.** This page exists so agents can recognise the legacy types (several share simple names with Navigation3
types, for example `INavigationDestination`, `INavigationGraph`, `INavigationGraphFactory`, `NavigationModule`) and avoid importing the wrong package.

| Concept | Legacy (`navigation.*`) | Active (`navigation3.*`) |
|---|---|---|
| Destination | `interface INavigationDestination` (`@Serializable` route class) | `abstract class INavigationDestination : NavKey` |
| Graph | `interface INavigationGraph` (with `startingDestination` and transitions) | `abstract class INavigationGraph` |
| Launcher marker | `interface INavigationGraphLauncher` | `abstract class INavigationGraphLauncher : INavigationGraph` |
| Entry | `INavigationGraphEntry<D, VM : BaseViewModel>` + Screen/Dialog/BottomSheet sub-interfaces | `INavigationEntry<D, VM : ViewModel>` + Dialog/BottomSheet subclasses |
| Navigation API | `INavigationManager` (emits `NavigationIntent`s) | `INavigationGraphManager` (mutates the back stack) |
| Hilt component | `SingletonComponent` | `ActivityRetainedComponent` |

## Package layout

```
navigation/
├── di/NavigationModule.kt
├── factory/{INavigationGraphFactory.kt, NavigationGraphFactoryImpl.kt}
├── manager/{INavigationManager.kt, NavigationManagerImpl.kt}
└── model/
    ├── arguments/{NavGraphEntryArguments.kt, ViewModelArguments.kt}
    ├── destination/INavigationDestination.kt
    ├── directions/{INavigationDirections.kt, NavigationIntent.kt}
    ├── entry/{INavigationGraphEntry.kt, type/INavigationGraphScreenEntry.kt, type/INavigationGraphDialogEntry.kt, type/INavigationGraphBottomSheetEntry.kt}
    ├── graph/{INavigationGraph.kt, INavigationGraphBottomNavBar.kt, INavigationGraphLauncher.kt}
    └── utils/{NavigationGraphBuilderExtensions.kt, StabilityWrappers.kt,
               navigator/bottomSheet/{BottomSheetHost.kt, BottomSheetNavigator.kt, BottomSheetNavigatorDestinationBuilder.kt}}
```

## Dependency injection: `navigation/di/NavigationModule`

`object NavigationModule`, `@InstallIn(SingletonComponent::class)`. All providers are `@Singleton`:
- `providesNavigationGraphFactory(destinations: Map<Class<*>, Set<INavigationGraphEntry<*, *>>>, graphs: Map<Class<*>, INavigationGraph>): INavigationGraphFactory = NavigationGraphFactoryImpl(...)`
- `providesNavigationManagerImpl(): INavigationManager = NavigationManagerImpl()`
- `providesNavigationDirections(navigationManager): INavigationDirections = navigationManager as INavigationDirections`

Features contributed `@IntoMap @ClassKey(Graph::class)` bindings for both maps. Nobody contributes them today, so injecting the legacy factory
would fail to resolve the maps unless they're declared (`@Multibinds`) or contributed.

## Type reference

### Destinations, graphs and entries
- `@Immutable interface INavigationDestination`: marker. Implementations are `@Serializable` type-safe routes.
- `@Immutable interface INavigationGraph { val startingDestination: KClass<out INavigationDestination>; val enterTransition/exitTransition/popEnterTransition/popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition/ExitTransition)? }`.
  The graph **object's class** is used as the nested graph route.
- `@Immutable interface INavigationGraphLauncher`: a marker for the single start graph.
- `@Immutable interface INavigationGraphBottomNavBar { order; @DrawableRes icon; label; notificationCount: StateFlow<Int>; isFab }`, identical in shape to the Navigation3 one.
- `@Immutable interface INavigationGraphEntry<DESTINATION : INavigationDestination, VIEWMODEL : BaseViewModel<*, *, *>> { val params: Map<KType, NavType<*>> }`. `params` is the type map for non-primitive route arguments.
- `INavigationGraphScreenEntry<D, VM>`: `enterTransition`, `exitTransition`, `popEnterTransition`, `popExitTransition`, `sizeTransform` (all nullable lambdas), `onComposableStarted(viewModel)`, `onComposableStopped(viewModel)`, `@Composable Render(controller: StableHolder<NavHostController>, viewModel: VM)`.
- `INavigationGraphDialogEntry<D, VM>`: `dialogProperties: DialogProperties`, the two lifecycle callbacks, and `Render(...)`.
- `INavigationGraphBottomSheetEntry<D, VM>`: `secureFlagPolicy: SecureFlagPolicy`, `skipPartiallyExpanded: Boolean`, the lifecycle callbacks, and `Render(...)`.

### Factory: `INavigationGraphFactory` / `NavigationGraphFactoryImpl`
- `navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>` joins both maps by class key. It's a computed `get()` that rebuilds on every access.
- `getLauncherNavGraph(): INavigationGraph`. Exactly one graph implementing `INavigationGraphLauncher` must exist, otherwise it throws `IllegalStateException`.
- `getBottomNavBarNavigationGraphs()` filters graphs implementing `INavigationGraphBottomNavBar`.
- `isBottomNavBarDestination(destination: String): Boolean` reflectively checks whether any bottom-nav graph entry's supertype arguments stringify to `destination`.

### Manager and directions
- `@Immutable interface INavigationManager { navigate(destination, builder: NavOptionsBuilder.() -> Unit = {}); navigateUp(); popCurrentBackStack(); popBackStackUntil(destination, inclusive, saveState = false) }`.
- `@Immutable interface INavigationDirections { val directions: Flow<NavigationIntent> }`.
- `@Singleton class NavigationManagerImpl : INavigationManager, INavigationDirections` sends intents into `Channel<NavigationIntent>(BUFFERED)` with `trySend`, and `directions = receiveAsFlow()`.
  A host composable was expected to collect `directions` and apply them to the `NavHostController`. That collector doesn't exist in the codebase.
- `@Stable sealed interface NavigationIntent`: `Navigate(destination, builder)` (`toString` prints only the destination), `data object PopCurrentBackStack`,
  `PopBackStackUntil(route, inclusive, saveState = false)`, `data object NavigateUp`.

### Arguments
- `inline fun <reified T : Any> navGraphEntryArgument(isNullableAllowed: Boolean = false, json: Json = Json): NavType<T>` is a JSON-string `NavType` for custom
  `@Serializable` argument types (stored in the bundle as a string). Use it in `INavigationGraphEntry.params` as `mapOf(typeOf<MyArg>() to navGraphEntryArgument<MyArg>())`.
- `@Immutable interface IViewModelNavigationArguments<DESTINATION> { val savedStateHandle: SavedStateHandle }` and
  `inline fun <reified T : INavigationDestination> IViewModelNavigationArguments<T>.params(typeMap: Map<KType, NavType<*>>): T` decode the route object from
  the ViewModel's `SavedStateHandle` using restricted Navigation APIs (`generateNavArguments`, `decodeArguments`) and `@InternalSerializationApi`.

### Registration: `NavGraphBuilder.addGraphs(navController: StableHolder<NavHostController>, navigationGraphs)`
For each graph it creates a nested `navigation(startDestination = graph.startingDestination, route = graph::class, transitions...)`. For each entry it adds:
- **Screen**: `ComposeNavigatorDestinationBuilder(route = destinationClass, typeMap = entry.params)` with the entry transitions.
- **Dialog**: `DialogNavigatorDestinationBuilder(..., dialogProperties)`.
- **Bottom sheet**: `BottomSheetNavigatorDestinationBuilder(...)` with `securePolicy` and `skipPartiallyExpanded`.

Each content block runs [`BaseFlow`](base-architecture.md#basescreenbaseflow-legacy-navigation-compose-only) (a graph-scoped Hilt ViewModel, plus `loadInitData()` on first creation),
then `ComposableLifecycle(onComposableStarted, onComposableStopped)` (see [ui-utils](ui-utils.md)), then `entry.Render(navController, viewModel)`.
The ViewModel and destination classes are resolved reflectively from `entry::class.supertypes[0].arguments[1]`/`[0]`. The entry must **directly** implement the
typed interface as its first supertype, and the ViewModel's **direct** superclass must be `BaseViewModel`, otherwise it throws `IllegalStateException`.

### Stability wrappers (`StabilityWrappers.kt`)
`@Stable class StableHolder<T>(val item: T)` and `@Immutable class ImmutableHolder<T>(val item: T)` (both with `component1()`), plus extensions
`T.asStable()`, `T.asStable` (property), `T.asImmutable()` and `T.asImmutable`. They wrap unstable objects (such as `NavHostController`) to avoid recompositions.
They're generic and could be reused anywhere.

### Material3 bottom-sheet navigator
- `@Navigator.Name("M3BottomSheetNavigator") class BottomSheetNavigator : Navigator<BottomSheetNavigator.Destination>()` pushes and pops with transitions on the
  navigator state. `Destination(navigator, content)` is a `NavDestination` + `FloatingWindow` with internal `securePolicy` (default `Inherit`) and `skipPartiallyExpanded` (default `true`).
- `@NavDestinationDsl class BottomSheetNavigatorDestinationBuilder` has constructors `(navigator, route: String, content)` and
  `(navigator, route: KClass<*>, typeMap, content)`, plus `var securePolicy` and `var skipPartiallyExpanded` applied in `build()`.
- `@Composable fun BottomSheetHost(navigator: BottomSheetNavigator, modifier = Modifier, sheetMaxWidth = BottomSheetDefaults.SheetMaxWidth, shape = ExpandedShape, containerColor = ContainerColor, contentColor = contentColorFor(containerColor), tonalElevation = 0.dp, scrimColor = ScrimColor, dragHandle = { BottomSheetDefaults.DragHandle() }, contentWindowInsets = { BottomSheetDefaults.windowInsets })`
  renders one `ModalBottomSheet` per back-stack entry. It uses `zipWithPreviousCount()` to keep a sheet composed while it animates out, hides the old sheet before showing the
  new one, provides the entry's `LocalOwnersProvider`, dismisses through `navigator.dismiss(entry)`, and marks transitions complete. It must be placed next to the `NavHost`
  with the navigator added to the `NavHostController`'s navigator provider.

## Migration notes (legacy to Navigation3)

| Legacy | Navigation3 replacement |
|---|---|
| `INavigationGraphScreenEntry.Render(controller, vm)` | `INavigationEntry.Render(params, viewModel)` with `navigator: Lazy<INavigationGraphManager>` |
| `INavigationManager.navigate(dest)` | `INavigationGraphManager.goToDestination(dest)` |
| `navigateUp()` / `popCurrentBackStack()` | `goBack()` |
| `popBackStackUntil(...)` | No equivalent. Manipulate `backStack` (a `SnapshotStateList`) directly. |
| `onComposableStarted/Stopped` | `ComposableLifecycle(...)` inside `Render` |
| `BaseFlow` graph-scoped VM + `loadInitData()` | Graph `ViewModelStore` via the decorator, and call `loadInitData()` yourself |
| `navGraphEntryArgument` / `params(typeMap)` | Route args are fields of the `@Serializable` destination, received as `params` in `Render` |

## Related pages

- [Navigation 3](navigation3.md)
- [Base architecture (`BaseFlow`)](base-architecture.md)
- [Coroutines (`zipWithPreviousCount`)](coroutines.md)
- [UI utilities (`ComposableLifecycle`)](ui-utils.md)
