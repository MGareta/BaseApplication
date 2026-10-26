---
type: how-to guide
title: Recipes for feature screens
description: Step-by-step Kotlin and Compose recipes for building features on core/presentation. Covers a complete Navigation3 screen, dialog and bottom-sheet destinations, bottom-nav tabs, validated forms, loading data into UiResult, paged lists, collapsing toolbars, session-warning handling, and adding design tokens or component styles, with the workarounds the current code requires.
tags: [core-presentation, recipes, how-to, navigation3, forms, paging, toolbar, design-system]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-dc2f4215d060ab5729f6cb69
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/di/DemoScreenModule.kt
  - id: openwiki-source-36d466834629170a4b4a5f3e
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/navigation/DemoScreenNavEntry2.kt
  - id: openwiki-source-04e9feca9dd458f02c42ea03
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/navigation/DemoScreenNavGraph.kt
  - id: openwiki-source-3917a7c895954ef50b72f64c
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/navigation/DemoScreenNavGraphEntry.kt
  - id: openwiki-source-90b8a1933f23cdd6d93ec1c8
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/view/DemoScreen.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Recipes for feature screens

Assumptions: a feature presentation module using the `feature.presentation` plugin (it gets core/presentation, Compose, Hilt and KSP). If the screen needs
core/data types (`ApiResult`, managers), also depend on the feature's data module (which `api`s core/data). See [overview](overview.md#how-other-modules-consume-it).

> **Read first:** current limitations that affect almost every recipe.
> 1. Navigation3 creates ViewModels with `viewModel(modelClass)` and **no Hilt factory**, so ViewModels with injected constructor parameters may fail to instantiate
>    ([navigation3](navigation3.md#gotchas-and-constraints)). Fix the decorator first, or keep ViewModels dependency-free.
> 2. Navigation3 doesn't call `loadInitData()`. Call it from `Render`.
> 3. `MainActivity.onSessionWarning()`/`onSessionTimeout()` are `TODO()`. Implement them before starting a session.

---

## 1. New feature screen (Navigation3)

1. **Destination** (route + args):
   ```kotlin
   @Serializable data object CatalogDestination : INavigationDestination()
   @Serializable data class ProductDestination(val productId: String) : INavigationDestination()
   ```
2. **Contract**:
   ```kotlin
   class CatalogContract {
       data class UiState(val isLoading: Boolean = false, val products: List<ProductUi> = emptyList()) : IBaseContract.ViewState
       sealed interface Event : IBaseContract.ViewEvent { data class ProductClicked(val id: String) : Event; data object Retry : Event }
       sealed interface Effect : IBaseContract.ViewEffect { data class OpenProduct(val id: String) : Effect }
   }
   ```
3. **ViewModel** (`BaseViewModel`, see [base-architecture](base-architecture.md)):
   ```kotlin
   @HiltViewModel
   class CatalogViewModel @Inject constructor(/* see limitation 1 */) :
       BaseViewModel<CatalogContract.UiState, CatalogContract.Event, CatalogContract.Effect>() {
       override fun setInitialState() = CatalogContract.UiState()
       override fun loadInitData() { /* withUseCaseScope { ... } */ }
       override fun handleUiEvents(event: CatalogContract.Event) = when (event) {
           is CatalogContract.Event.ProductClicked -> setEffect { CatalogContract.Effect.OpenProduct(event.id) }
           CatalogContract.Event.Retry -> loadInitData()
       }
   }
   ```
4. **Stateless screen composable** (previewable, theme tokens only):
   ```kotlin
   @Composable
   fun CatalogScreen(state: CatalogContract.UiState, onEvent: (CatalogContract.Event) -> Unit) {
       Column(
           Modifier
               .fillMaxSize()
               .background(LocalPalette.current.backgroundPalette.base)
               .nestedScroll(LocalBottomNavBarScrollConnection.current)   // only if the graph shows the bottom nav
               .verticalScroll(rememberScrollState())
               .windowInsetsPadding(WindowInsets.statusBars)
               .bottomNavBarPadding(),
       ) { /* ... */ }
   }
   ```
5. **Entry** (binds destination, ViewModel and UI):
   ```kotlin
   class CatalogEntry @Inject constructor(
       override val navigator: dagger.Lazy<INavigationGraphManager>,
   ) : INavigationEntry<CatalogDestination, CatalogViewModel>() {
       @Composable
       override fun Render(params: CatalogDestination, viewModel: CatalogViewModel) {
           val state by viewModel.viewState.collectAsStateWithLifecycle()
           LaunchedEffect(Unit) { viewModel.loadInitData() }
           LaunchedEffect(Unit) {
               viewModel.effect.collect {
                   when (it) { is CatalogContract.Effect.OpenProduct -> navigator.get().goToDestination(ProductDestination(it.id)) }
               }
           }
           CatalogScreen(state = state, onEvent = viewModel::handleUiEvents)
       }
   }
   ```
6. **Graph and Hilt module** (both under the same `@ClassKey`, in `ActivityRetainedComponent`):
   ```kotlin
   class CatalogNavGraph @Inject constructor() : INavigationGraph()

   @Module @InstallIn(ActivityRetainedComponent::class)
   class CatalogNavigationModule {
       @Provides @IntoMap @ClassKey(CatalogNavGraph::class)
       fun graph(graph: CatalogNavGraph): INavigationGraph = graph

       @Provides @IntoMap @ClassKey(CatalogNavGraph::class)
       fun entries(catalog: CatalogEntry, product: ProductEntry): Set<INavigationEntry<INavigationDestination, ViewModel>> = setOf(catalog, product)
   }
   ```
7. **Navigate** from anywhere with access to the manager: `navigator.get().goToDestination(CatalogDestination)`, and `goBack()`.

Checklist: imports come from `navigation3` (not `navigation`), the destination class is used by exactly one entry, and the entry is in its graph's set.

## 2. Make a graph the app's start graph

Extend `INavigationGraphLauncher` instead of `INavigationGraph` (exactly one graph app-wide), and put an entry whose destination is a Kotlin **`object`** **first** in the set.
The demo's `DemoScreenNavGraph` is currently the launcher, so remove that role from it.

## 3. Dialog and bottom-sheet destinations

```kotlin
class ConfirmDeleteEntry @Inject constructor(override val navigator: dagger.Lazy<INavigationGraphManager>) :
    INavigationEntryDialog<ConfirmDeleteDestination, CatalogViewModel>() {
    override val dialogProperties = DialogProperties(dismissOnClickOutside = true)
    @Composable override fun Render(params: ConfirmDeleteDestination, viewModel: CatalogViewModel) {
        AlertComponent(/* ... */ alertFamilyType = AlertFamilyType.Message(trailingComponent = null), /* ... */)
    }
}

class SortSheetEntry @Inject constructor(override val navigator: dagger.Lazy<INavigationGraphManager>) :
    INavigationEntryBottomSheet<SortSheetDestination, CatalogViewModel>() {
    @OptIn(ExperimentalMaterial3Api::class)
    override val modalBottomSheetProperties = ModalBottomSheetProperties()
    @Composable override fun Render(params: SortSheetDestination, viewModel: CatalogViewModel) { SortOptions(onPick = { /* ... */ navigator.get().goBack() }) }
}
```
Using the same ViewModel class as the screen (in the same graph) shares the instance. For a transient status sheet owned by a single screen, use
`BottomSheetComponent` instead ([components-layout](components-layout.md)).

## 4. Add a bottom-nav tab

Make the graph implement `INavigationGraphBottomNavBar`:
```kotlin
class ProfileNavGraph @Inject constructor() : INavigationGraph(), INavigationGraphBottomNavBar {
    override val order = 2
    override val icon = R.drawable.ic_profile
    override val label = "Profile"
    override val notificationCount: StateFlow<Int> = MutableStateFlow(0)   // never use flowOf(0) as StateFlow
    override val isFab = false
}
```
The bar appears while a destination of a bottom-nav graph is on top. **Tab taps are a no-op today** (`BaseActivity` passes `onTabSelected = { }`), so wire it in
`BaseActivity` to `goToDestination(<tab start destination>)` ([app-and-activity](app-and-activity.md#extension-guide)). Screens in these graphs should apply
`nestedScroll(LocalBottomNavBarScrollConnection.current)` and `Modifier.bottomNavBarPadding()`.

## 5. Validated form with submit

Follow the complete example in [forms](forms.md#complete-example). UI wiring:
```kotlin
TextFieldComponent(Modifier.fillMaxWidth(), state = vm.name, placeholder = "Name", enabled = true)
TextFieldComponent(Modifier.fillMaxWidth(), state = vm.email, placeholder = "Email", enabled = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done))
DateFieldComponent(Modifier.fillMaxWidth(), state = vm.birthDate, placeholder = "Birth date", enabled = true)
CheckboxComponent(
    modifier = Modifier, state = vm.terms,
    checkboxStyleType = CheckboxStyleType.Text("I accept the terms", "Required to continue"),
    accessibilityStateSelect = AccessibilityState.Select("Accepted", "Not accepted"),
    accessibilityStateClick = AccessibilityState.Click(""),
    enabled = true,
    onValueChange = { vm.terms.change(it) },                  // REQUIRED: the default handler is broken
)
BlockButtonComponent(Modifier.fillMaxWidth(), BlockButtonStyleType.Primary, BlockButtonSizeType.Large, "Create account",
    null, null, enabled = !state.isLoading, accessibilityState = AccessibilityState.Click("Wait"), onClick = { vm.handleUiEvents(Event.Submit) })
```
Remember the display limitations: errors on empty fields and on date fields aren't shown, so surface a summary message in state.

## 6. Load data and render `UiResult`

```kotlin
// ViewModel property
val banner: StateFlow<UiResult<BannerDto>> = withUseCaseScopeLazily(
    onError = { UiResult.Error(it.response ?: "Error ${it.httpCode}") },
    onException = { UiResult.Error(it.exception.message ?: "Unexpected error") },
    block = { getBanner() },
)

// UI
when (val r = vm.banner.collectAsStateWithLifecycle().value) {
    is UiResult.Loading -> CircularProgressIndicator()
    is UiResult.Success -> Banner(r.data)
    is UiResult.Error -> AlertComponent(Modifier, AlertStyleType.Error, AlertFamilyType.Message(null), "Couldn't load", r.message, null)
    is UiResult.NoContent -> Unit
}
```
See [coroutines](coroutines.md#withusecasescopelazily) for re-subscription semantics.

## 7. Paged list

```kotlin
val items: Flow<PagingData<ItemDto>> = withUseCaseScopePaging<Int, ItemDto>(
    pageSize = 20,
    block = { page -> repository.page((page as Int?) ?: 1) },   // ApiResult<PagingResponse<Int, ItemDto>>, pageId = next page or null
)

// UI (PagingComponent is commented out; use paging-compose directly)
val lazyItems = vm.items.collectAsLazyPagingItems()
LazyColumn(Modifier.nestedScroll(LocalBottomNavBarScrollConnection.current)) {
    items(count = lazyItems.itemCount, key = lazyItems.itemKey { it.id }) { index -> lazyItems[index]?.let { ItemRow(it) } }
    if (lazyItems.loadState.append is LoadState.Loading) item { CircularProgressIndicator() }
}
```
`collectAsLazyPagingItems` comes from `androidx.paging:paging-compose`. core/presentation exposes `androidx-paging` (see `libs.versions.toml` for the exact artifact) and
you may need to add `paging-compose` to the feature module.

## 8. Collapsing toolbar

```kotlin
val behavior = exitUntilCollapsedScrollBehavior()     // or enterAlwaysScrollBehavior() / pinnedScrollBehavior()
Column(Modifier.fillMaxSize()) {
    ToolbarComponent(
        modifier = Modifier,
        scrollBehavior = behavior,
        toolbarFamilyType = ToolbarFamilyType.Animate(
            expandedContent = { HeroHeader(state) },
            collapsedContent = { TopNavBarComponent(title = state.title, onNavigateBack = { navigator.get().goBack() }) },
        ),
    )
    LazyColumn(Modifier.weight(1f).nestedScroll(behavior.nestedScrollConnection)) { /* ... */ }
}
```
Details: [components-navigation](components-navigation.md#toolbarcomponent-collapsing-header).

## 9. React to session warnings and timeouts

Implement the hooks in the app's `MainActivity` (they're `TODO()` today):
```kotlin
override fun onSessionWarning() {
    navigationGraphManager.get().goToDestination(SessionWarningDestination)   // e.g. an INavigationEntryDialog with "Stay signed in"
}
override fun onSessionTimeout() {
    super.onSessionTimeout()                                                  // invalidates the session
    navigationGraphManager.get().goToDestination(LoginDestination)
}
```
"Stay signed in" should call `sessionManager.restartSessionTimeout()`. Any touch already does via `onUserInteraction`. See [core-data session](../core-data/session.md).

## 10. Add a design token or component style

- **Token:** add the value to the token object (for example `val space40 = 40.dp` in `SpaceDimensions`, or a new palette role in `BaseContentPalette` **and every**
  light/dark × contrast implementation). Never hard-code colours in components. Read `LocalPalette.current.*`. See [theme-tokens](theme-tokens.md).
- **Component style:** add a variant to the sealed `*StyleType` and a branch to its internal `remember*Style` (keyed by the type and theme locals). Prefer the
  existing `Custom` variants for one-offs. See [components-actions](components-actions.md#extension-guide).

## Related pages

- [Navigation 3](navigation3.md)
- [Base architecture](base-architecture.md)
- [Forms](forms.md)
- [Form fields](form-fields.md)
- [Coroutines](coroutines.md)
- [core-data recipes](../core-data/recipes.md)
