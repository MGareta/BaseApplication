---
type: package reference
title: Navigation 3 (active navigation stack)
description: Reference for com.core.presentation.navigation3, the Navigation3-based stack BaseActivity uses. Covers destinations (NavKey), entries with Render plus dialog and bottom-sheet variants, graphs (launcher, bottom-nav), feature registration through Hilt @ClassKey multibinding in ActivityRetainedComponent, the factory and back-stack manager, graph-scoped ViewModel stores, the bottom-sheet scene strategy, and known pitfalls.
tags: [core-presentation, navigation3, navigation, hilt, multibinding, viewmodel, bottom-sheet, dialog]
sources:
  - id: openwiki-source-bf3f24298a1cbf4d07ff2872
    resource: repo://.claude/skills/wiki-context/SKILL.md
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
  - id: openwiki-source-5f31cbb6e624fb9b0eb9aadc
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/di/NavigationModule.kt
  - id: openwiki-source-f96123c650a78c26f62af83d
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/factory/NavigationGraphFactoryImpl.kt
  - id: openwiki-source-691c0708e5faef6c7d032df6
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/manager/NavigationGraphManagerImpl.kt
  - id: openwiki-source-e8b13a3752a96095685bba51
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/model/entry/decorators/viewModel/NavigationGraphViewModelStoreOwner.kt
  - id: openwiki-source-c612bea63c86a3798eafddc8
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/model/entry/decorators/viewModel/SharedViewModelStoreNavEntryDecorator.kt
  - id: openwiki-source-ad838c034f33134312606ac4
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/model/entry/INavigationEntry.kt
  - id: openwiki-source-ea052543c1f7bb3f0c11e875
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/model/entry/scenes/BottomSheetSceneStrategy.kt
  - id: openwiki-source-fa1d9c4027b1b13f8543e591
    resource: repo://login/presentation/src/main/java/com/login/presentation/di/LoginNavigationModule.kt
  - id: openwiki-source-0a99751a4aaebc09c898f153
    resource: repo://common/navigation/src/main/java/com/common/navigation/destination/login/LoginScreenNavDestination.kt
  - id: openwiki-source-3d81ea9a2f39ace049ef5bdb
    resource: repo://login/presentation/src/main/java/com/login/presentation/navigation/LoginEntry.kt
  - id: openwiki-source-a26ab395010ddf1d5412cc1f
    resource: repo://login/presentation/src/main/java/com/login/presentation/navigation/LoginNavGraph.kt
generated: { by: "claude-code", at: "2026-11-16T11:21:00.506Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-16T11:21:00.506Z
---

# Navigation 3 (`com.core.presentation.navigation3`)

## Purpose and when to use it

This is the **active** navigation system. `BaseActivity` renders `androidx.navigation3.ui.NavDisplay` over a back stack owned by
`INavigationGraphManager` (see [app-and-activity](app-and-activity.md)). Features plug in by contributing **graphs** and **entries** through Hilt.
Navigation is done by pushing destinations onto the back stack.

Don't use the legacy `com.core.presentation.navigation` package ([navigation-legacy](navigation-legacy.md)). Several type names are identical, so check your imports.

## Package layout

```
navigation3/
├── di/NavigationModule.kt
├── factory/{INavigationGraphFactory.kt, NavigationGraphFactoryImpl.kt (internal)}
├── listener/INavigationGraphListener.kt                (internal)
├── manager/{INavigationGraphManager.kt, NavigationGraphManagerImpl.kt (internal)}
└── model/
    ├── destination/INavigationDestination.kt
    ├── entry/
    │   ├── INavigationEntry.kt
    │   ├── type/{INavigationEntryDialog.kt, INavigationEntryBottomSheet.kt}
    │   ├── scenes/BottomSheetSceneStrategy.kt           (internal BottomSheetScene + BottomSheetSceneStrategy)
    │   └── decorators/viewModel/{NavigationGraphViewModelStoreOwner.kt (internal), SharedViewModelStoreNavEntryDecorator.kt (internal)}
    └── graph/
        ├── INavigationGraph.kt
        └── type/{INavigationGraphLauncher.kt, INavigationGraphBottomNavBar.kt}
```

## Core model

| Type | Kind | Contract |
|---|---|---|
| `INavigationDestination` | `abstract class : NavKey` | A route. Subclass it as a `@Serializable data object` (no args) or `@Serializable data class` (args are its fields). |
| `INavigationEntry<out DESTINATION : INavigationDestination, out VM : ViewModel>` | `abstract class` | Binds a destination type to a ViewModel type and UI. Requires `abstract val navigator: dagger.Lazy<INavigationGraphManager>` and `@Composable abstract fun Render(params: DESTINATION, viewModel: VM)`. |
| `INavigationEntryDialog<D, VM>` | `abstract class : INavigationEntry` | Adds `abstract val dialogProperties: DialogProperties`. Rendered by Navigation3's `DialogSceneStrategy` in a `Dialog`. |
| `INavigationEntryBottomSheet<D, VM>` | `abstract class : INavigationEntry` | Adds `abstract val modalBottomSheetProperties: ModalBottomSheetProperties` (`@ExperimentalMaterial3Api`). Rendered in a Material3 `ModalBottomSheet`. |
| `INavigationGraph` | `abstract class` | A group of entries (feature flow). It's the key for ViewModel sharing. |
| `INavigationGraphLauncher` | `abstract class : INavigationGraph` | Marks the **single** start graph. |
| `INavigationGraphBottomNavBar` | `@Immutable interface` | `order: Int`, `@DrawableRes icon: Int`, `label: String`, `notificationCount: StateFlow<Int>` (unused by the UI), `isFab: Boolean`. A graph implementing it gets a bottom-nav item, and the bar shows while one of its destinations is on top. |

`INavigationEntry` resolves its type arguments reflectively: `destination: KClass<out DESTINATION>` and `viewModel: KClass<out VM>` (both `by lazy`). It walks the
superclass chain to the first parameterized `INavigationEntry` supertype, so intermediate base classes work. It throws `NoSuchElementException("Generic type not found")` if
the arguments can't be resolved (for example when they're generic type parameters rather than concrete classes).

## Dependency injection

### Core module: `navigation3/di/NavigationModule`
`object NavigationModule`, `@InstallIn(ActivityRetainedComponent::class)`. All providers are `@ActivityRetainedScoped` (they survive rotation, one per activity "instance"):

| Provider | Returns |
|---|---|
| `providesNavigationGraphFactory(destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationEntry<INavigationDestination, ViewModel>>>, graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>)` | `INavigationGraphFactory` (`NavigationGraphFactoryImpl`) |
| `provideNavigationScope()` | `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`, an **unqualified** `CoroutineScope` binding in this component |
| `providesNavigationGraphManager(scope, navigationGraphFactory)` | `INavigationGraphManager` (`NavigationGraphManagerImpl`) |

### Feature registration contract
The core factory consumes a graph map and an entry-set map keyed by graph class, then joins only matching pairs. Each participating feature graph therefore needs both contributions under the same key: the graph instance and the **set** of its entries. The illustrative Hilt module below shows one way to provide them; its names and layout are not a feature template.

```kotlin
@Module
@InstallIn(ActivityRetainedComponent::class)
internal class OrdersNavigationModule {

    @Provides @IntoMap @ClassKey(OrdersNavGraph::class)
    fun providesOrdersGraph(graph: OrdersNavGraph): INavigationGraph = graph

    @Provides @IntoMap @ClassKey(OrdersNavGraph::class)
    fun providesOrdersEntries(
        list: OrdersListEntry,
        detail: OrderDetailEntry,
        filters: OrderFiltersSheetEntry,
    ): Set<INavigationEntry<INavigationDestination, ViewModel>> = setOf(list, detail, filters)
}
```
- The map value types must match **exactly** (`INavigationGraph` from `navigation3.model.graph`, and `Set<INavigationEntry<INavigationDestination, ViewModel>>`). Declaration-site `out`
  variance makes `setOf(entryA, entryB)` assignable.
- A graph without an entry set (or the reverse) is **silently dropped** by the factory join.
- One `@ClassKey` per map, so put all entries of a graph in one set provider (two providers with the same key fail with a Dagger duplicate-key error).
- Entries and graphs are normal `@Inject constructor` classes. Entries receive `override val navigator: dagger.Lazy<INavigationGraphManager>`.
- **Feature navigation types should be `internal`, except destinations.** Entries, the graph, the navigation module and screens in a feature presentation module (for example `LoginEntry`, `LoginNavGraph`, `LoginNavigationModule`) are `internal`; the examples on this page use it too. Destinations are the exception: they are public and live in `:common:navigation` (see [Where destinations live](#where-destinations-live)). This is safe because `:app` never references feature entries or graphs: it only receives the `@IntoMap` bindings, whose value types are the public `INavigationGraph` and `Set<INavigationEntry<INavigationDestination, ViewModel>>`, and `INavigationEntry` resolves its destination and ViewModel classes by reflection over generic type arguments, which works on `internal` classes because Kotlin `internal` is public in bytecode. Never widen a feature entry, graph or screen to public just to make it compile.

### Where destinations live

Every feature's navigation destination lives in the shared module `:common:navigation` (namespace `com.common.navigation`, plugin `feature.presentation`, depends only on `:core:presentation`), not in the feature:

```
common/navigation/src/main/java/com/common/navigation/destination/<feature>/<Screen>ScreenNavDestination.kt
package com.common.navigation.destination.<feature>
e.g. .../destination/login/LoginScreenNavDestination.kt, .../destination/demo/DemoScreenNavDestination.kt
```

- The class is named `<Screen>ScreenNavDestination` and is a **public** `@Serializable data object` (no args) or `data class` (args as fields) extending `INavigationDestination()`.
- Feature presentation modules have no `model/destination/` package (`model/` holds only `ui/`) and get `implementation(project(":common:navigation"))` from the `feature.presentation` plugin (they do not declare it in their own build file). Their entries, graph, navigation module and screens stay `internal` and import the destination from `com.common.navigation.destination.<feature>`.
- To navigate to another feature, import that feature's `<Screen>ScreenNavDestination` from the shared module and call `navigator.get().goToDestination(...)`; nothing else of the other feature is imported. `:app` can reference destinations the same way because `AppPlugin` adds `:common:navigation` to it, for example `LoginScreenNavDestination` on session timeout.
- Because `:common:navigation` cannot depend on features, destination arguments must be primitives/IDs or types from `core` or `:common:navigation` itself.
- **Why:** one place knows every route, so features navigate to each other without depending on each other, and `core` stays generic (it only has `INavigationDestination`).
- **Trade-offs:** every screen's route becomes public and reachable; adding a screen edits two modules (the destination here, the entry in the feature); the module must stay pure (destination classes only, no logic).

## Factory: `INavigationGraphFactory`

| Member | Semantics |
|---|---|
| `navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationEntry<...>>>` | `by lazy` join of `graphs` and `destinations` by class key, keeping only complete pairs. |
| `navigationGraphLauncher: Map.Entry<INavigationGraph, Set<...>>` | The pair whose graph `is INavigationGraphLauncher`. It throws `IllegalStateException` if there are none ("Launcher navigation graph is not set...") or more than one. |
| `navigationGraphBottomNavBar: Set<INavigationGraphBottomNavBar>` | Graphs implementing the bottom-nav interface. |
| `fun register(scope: EntryProviderScope<INavigationDestination>)` | For every entry: `scope.addEntryProvider(clazz = entry.destination, clazzContentKey = { it::class.qualifiedName.toString() }, metadata = ...)`. Metadata is `BottomSheetSceneStrategy.bottomSheet(entry.modalBottomSheetProperties)` for bottom-sheet entries, `DialogSceneStrategy.dialog(entry.dialogProperties)` for dialog entries, else empty. Content requires a `LocalViewModelStoreOwner` implementing `HasDefaultViewModelProviderFactory`, then calls `viewModel()` with a `HiltViewModelFactory` built from `LocalContext`, the owner's default factory and creation extras, before calling `entry.Render(params = destination, viewModel = viewModel)`. |
| `fun getNavigationGraphFromDestination(destination): INavigationGraph` | The first graph whose entries include `destination::class`. It throws `IllegalArgumentException("Invalid destination")` otherwise. |

## Manager: `INavigationGraphManager`

| Member | Semantics |
|---|---|
| `val backStack: SnapshotStateList<INavigationDestination>` | `by lazy`: starts with `navigationGraphLauncher.value.first().destination.objectInstance`. The **first entry of the launcher graph's set must be a Kotlin `object` destination**, otherwise it throws `IllegalStateException("Destination X must be a Kotlin 'object'...")`. On creation it launches `snapshotFlow { it.toList() }.onEach { onBackStackChange() }` in the navigation scope. |
| `fun goToDestination(destination)` | `backStack.add(destination)`. |
| `fun goBack()` | `backStack.removeLastOrNull()` (it can empty the stack). |
| `fun getViewModelStore(graph): ViewModelStore` | `getOrPut(graph::class.qualifiedName!!) { ViewModelStore() }`. |

`NavigationGraphManagerImpl` also implements internal `INavigationGraphListener { fun onPop() }`. `onPop()` and every back-stack change run `onBackStackChange()`, which
computes the graphs still represented in the back stack and `clear()`s and removes the `ViewModelStore` of every other graph, destroying their ViewModels.

All operations mutate Compose snapshot state. Call them from the main thread (for example from `Render`, a `LaunchedEffect`, or click handlers).

## Graph-scoped ViewModels (entry decorator)

`BaseActivity` adds `rememberSharedViewModelStoreNavEntryDecorator(navigationGraphManager, graphs, defaultViewModelProviderFactory, defaultViewModelCreationExtras)` (internal, remembered by the graph registry and factory inputs). For each `NavEntry`:
1. It finds the graph containing an entry whose `destination.qualifiedName == entry.contentKey`.
2. If found, it provides `LocalViewModelStoreOwner = NavigationGraphViewModelStoreOwner(graphStore, defaultFactory, creationExtras)`. This owner implements `HasDefaultViewModelProviderFactory`; otherwise the entry renders unchanged.
3. Its `onPop` forwards to `INavigationGraphListener.onPop()`.

Consequences:
- **ViewModels are shared per graph and per class.** `viewModel(modelClass)` without a key means every entry in the same graph that declares the same VM type gets the **same instance**
  (useful for multi-step flows), and so does every back-stack instance of the same destination.
- A ViewModel lives until **no destination of its graph** remains in the back stack.

## Bottom-sheet scenes

`internal class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T>`: if the **last** back-stack entry's metadata has key `"bottomsheet"` (`BOTTOM_SHEET_KEY`) with
`ModalBottomSheetProperties`, it creates an `internal class BottomSheetScene` (`OverlayScene`) whose content is
`ModalBottomSheet(onDismissRequest = onBack, properties) { entry.Content() }`, overlaying all previous entries. Otherwise it returns `null` (the next strategy applies).
`companion fun bottomSheet(modalBottomSheetProperties = ModalBottomSheetProperties()): Map<String, Any>` builds the metadata, and the factory calls it for `INavigationEntryBottomSheet`.

## Data flow

```mermaid
sequenceDiagram
    participant E as Entry.Render (screen)
    participant M as INavigationGraphManager
    participant ND as NavDisplay (BaseActivity)
    participant F as INavigationGraphFactory
    participant D as SharedViewModelStore decorator
    E->>M: goToDestination(OrderDetail(id))
    M->>M: backStack.add (SnapshotStateList)
    M->>M: snapshotFlow -> onBackStackChange (clear unused graph stores)
    ND->>F: entryProvider(OrderDetail) (registered via register())
    ND->>ND: sceneStrategies: bottomSheet? dialog? single pane
    ND->>D: decorate entry -> LocalViewModelStoreOwner = graph store
    D->>F: content: viewModel(OrderDetailViewModel::class)
    F->>E: Render(params = OrderDetail(id), viewModel)
```

## Illustrative Navigation3 composition

The Orders-named classes below illustrate how destinations, a graph, and entries fit together. They are not a required feature structure; each feature adds only the elements its approved design needs.

```kotlin
// Destinations
// common/navigation/.../destination/orders/ (package com.common.navigation.destination.orders), public
@Serializable data object OrdersListScreenNavDestination : INavigationDestination()
@Serializable data class OrderDetailScreenNavDestination(val orderId: String) : INavigationDestination()
@Serializable data object OrderFiltersScreenNavDestination : INavigationDestination()

// Below: feature code in :orders:presentation, which imports them
// import com.common.navigation.destination.orders.OrdersListScreenNavDestination (and the other two)

// Graph (launcher + bottom-nav tab in this example)
internal class OrdersNavGraph @Inject constructor() : INavigationGraphLauncher(), INavigationGraphBottomNavBar {
    override val order = 0
    override val icon = R.drawable.ic_orders
    override val label = "Orders"
    override val notificationCount: StateFlow<Int> = MutableStateFlow(0)   // must be a real StateFlow
    override val isFab = false
}

// Screen entry
internal class OrdersListEntry @Inject constructor(
    override val navigator: Lazy<INavigationGraphManager>,        // dagger.Lazy
) : INavigationEntry<OrdersListScreenNavDestination, OrdersViewModel>() {
    @Composable
    override fun Render(params: OrdersListScreenNavDestination, viewModel: OrdersViewModel) {
        val state by viewModel.viewState.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) { viewModel.loadInitData() }
        OrdersListScreen(
            state = state,
            onOrderClick = { navigator.get().goToDestination(OrderDetailScreenNavDestination(it)) },
            onFilters = { navigator.get().goToDestination(OrderFiltersScreenNavDestination) },
        )
    }
}

// Bottom sheet entry sharing the same ViewModel (same graph, same VM class -> same instance)
internal class OrderFiltersSheetEntry @Inject constructor(
    override val navigator: Lazy<INavigationGraphManager>,
) : INavigationEntryBottomSheet<OrderFiltersScreenNavDestination, OrdersViewModel>() {
    @OptIn(ExperimentalMaterial3Api::class)
    override val modalBottomSheetProperties = ModalBottomSheetProperties()

    @Composable
    override fun Render(params: OrderFiltersScreenNavDestination, viewModel: OrdersViewModel) {
        FiltersSheet(onApply = { viewModel.handleUiEvents(OrdersContract.Event.ApplyFilters(it)); navigator.get().goBack() })
    }
}
```
Register the graph and its entry set under matching keys as described in *Feature registration contract*. Put the launcher's initial entry **first** in its set, because the start destination is the set's first entry and must be an `object`.

## Gotchas and constraints

- **Hilt ViewModels with constructor dependencies:** `NavigationGraphViewModelStoreOwner` implements `HasDefaultViewModelProviderFactory` and carries the activity's default factory and creation extras. `register()` builds a `HiltViewModelFactory` from the current context and that owner, then supplies the factory and extras to `viewModel()`, so graph-scoped `@HiltViewModel` entries can resolve injected constructor dependencies through Hilt.
- **`loadInitData()` isn't called** for Navigation3 entries. Call it yourself.
- **Content keys are class names.** `clazzContentKey = qualifiedName`, so two back-stack instances of the same destination class (for example two `OrderDetailScreenNavDestination`s with different ids)
  share saveable state and the ViewModel. Avoid stacking the same destination twice, or include the args in a distinct class.
- **Start destination:** the launcher graph's first set entry must be an `object` destination, with exactly one launcher graph.
- **Unregistered destination on top crashes** `BaseActivity` (`getNavigationGraphFromDestination` throws).
- **Back stack isn't persisted:** it lives in an `@ActivityRetainedScoped` object (it survives rotation, not process death), and destinations' `@Serializable` isn't used for restoration here.
- `goBack()` on a single-entry stack empties it. Guard it if needed.
- The navigation `CoroutineScope` is never cancelled.
- `notificationCount` must be a real `StateFlow`. `flowOf(0) as StateFlow<Int>` (as in the demo and component preview) throws `ClassCastException` if it's ever read.
- Bottom-nav tab taps aren't wired in `BaseActivity` (`onTabSelected = { }`).

## Extension guide

- **New screen:** destination + entry + add it to its graph's set provider.
- **New graph:** a graph class (optionally `INavigationGraphBottomNavBar`) + a `@ClassKey` module with both providers.
- **Dialog:** extend `INavigationEntryDialog` and provide `dialogProperties`. **Bottom sheet:** extend `INavigationEntryBottomSheet`.
- **New scene type:** implement `SceneStrategy<INavigationDestination>` and add it to `sceneStrategies` in `BaseActivity`. Attach metadata in `register()` based on a new entry subtype.

## Related pages

- [CoreApp and BaseActivity](app-and-activity.md)
- [Base architecture](base-architecture.md)
- [Recipes](recipes.md)
- [Legacy navigation](navigation-legacy.md)
- [Bottom nav bar](components-navigation.md)
