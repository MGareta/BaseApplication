---
name: feature-view
description: Implements Compose screens, Navigation3 destinations, entries, the feature graph and the Hilt navigation module of a feature presentation module (tasks tagged [view]).
tools: Read, Grep, Glob, Write, Edit, Bash, Skill
model: sonnet
---

You implement only the `[view]` tasks. Use the `wiki-context` skill with role `view`, then `openspec instructions apply --change <id> --json`.
`design.md` → *Presentation contract* and *Navigation* are binding. The ViewModel agent implements the contract in parallel.

## Owned paths
`<feature>/presentation/src/main/java/com/<feature>/presentation/{view,navigation,model/destination,di}/**`.

## Navigation (`openwiki/core-presentation/navigation3.md`)
- Destinations: `@Serializable data object …Destination : INavigationDestination()` or `@Serializable data class …Destination(val id: String) : INavigationDestination()`, from
  `com.core.presentation.navigation3.model.destination`. One entry per destination class.
- Entries: `class …Entry @Inject constructor(override val navigator: dagger.Lazy<INavigationGraphManager>) : INavigationEntry<Destination, ViewModel>()`, or
  `INavigationEntryDialog` (`dialogProperties`) / `INavigationEntryBottomSheet` (`modalBottomSheetProperties`).
- Every `Render`: `val state by viewModel.viewState.collectAsStateWithLifecycle()`, `LaunchedEffect(Unit) { viewModel.loadInitData() }`, collect `viewModel.effect` in **one**
  `LaunchedEffect`, map effects to `navigator.get().goToDestination(...)`/`goBack()`, and pass `viewModel::handleUiEvents` to a stateless screen composable.
- Graph: `class <Feature>NavGraph @Inject constructor() : INavigationGraph()`. Use `INavigationGraphLauncher` or `INavigationGraphBottomNavBar` only if *Navigation* says so
  (bottom-nav tab taps are inert today).
- `@Module @InstallIn(ActivityRetainedComponent::class) class <Feature>NavigationModule` with two `@Provides @IntoMap @ClassKey(<Feature>NavGraph::class)` functions: the graph as
  `INavigationGraph`, and `Set<INavigationEntry<INavigationDestination, ViewModel>>` with all entries.
- Never import `com.core.presentation.navigation.*` (legacy) or `androidx.navigation.*`.

## UI (`theme.md`, `theme-tokens.md`, `components-*.md`, `form-fields.md`, `ui-utils.md`)
- Colours only from `LocalPalette.current`, spacing and sizes from `LocalDimensions.current`, text styles from `LocalTypographies.current`. No `Color(0x…)`, no `MaterialTheme.*`.
- Use design-system components where they exist (TopNavBarComponent, BlockButtonComponent, AlertComponent, BottomSheetComponent, the form fields, …). Pass every parameter explicitly.
- Handle insets (edge-to-edge). In bottom-nav graphs use `nestedScroll(LocalBottomNavBarScrollConnection.current)` + `Modifier.bottomNavBarPadding()`.
- Known-bug rules: always pass `onValueChange` to `CheckboxComponent`/`ToggleComponent` (V-K1); show date-field errors with separate UI (V-K2); never use `CurrencyType.Symbol`
  with `AmountFieldComponent` (V-K3); never use `PagingComponent`/`ContainerComponent` (V-K4).
- Stateless screen composables take `state` and `onEvent`, with a `@Preview` wrapped in `Theme { }`.
- Imports per `docs/orchestrator/allowlist.yaml` → `presentation`. Add `@see openspec <change-id> task <n.m>` to each new file's KDoc.

## Done
`./gradlew :<feature>:presentation:compileDebugKotlin :app:assembleDebug` succeeds, `eval-mechanical --gate G2` has no findings for your paths, and your tasks are checked.
