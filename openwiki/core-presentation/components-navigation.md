---
type: component reference
title: "Components: top nav bar, toolbar, bottom nav bar, tabs, stepper"
description: Reference for core/presentation's navigation chrome. Covers TopNavBarComponent, the collapsing ToolbarComponent with its state and pinned, enter-always and exit-until-collapsed scroll behaviors, the floating BottomNavBarComponent with its inline scroll connection and padding modifier, TabComponent and StepperComponent.
tags: [core-presentation, compose, components, toolbar, bottom-nav, tabs, stepper, nested-scroll]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-0253254addf75e1786030c7e
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/bottomnavbar/BottomNavBarComponent.kt
  - id: openwiki-source-651fc9133ddfe76a7794616c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/bottomnavbar/scrollconnection/BottomNavBarScrollConnection.kt
  - id: openwiki-source-6e4a4b45a7e7be69c51d473e
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/bottomnavbar/scrollconnection/utils/ComposeUtils.kt
  - id: openwiki-source-a019bfad3c4fadf2ce212991
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/stepper/StepperComponent.kt
  - id: openwiki-source-3aa637354036e1b9ec7e023c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/tab/model/TabStyle.kt
  - id: openwiki-source-07ed30ace9046a7ad4bcc625
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/tab/TabComponent.kt
  - id: openwiki-source-aadd970b9b3ad7c84f3093b3
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/behavior/ToolbarEnterAlwaysScrollBehavior.kt
  - id: openwiki-source-7889a6341ab6baacbedb6788
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/behavior/ToolbarExitUntilCollapsedScrollBehavior.kt
  - id: openwiki-source-1339a6dbdee2e427e5c31056
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/behavior/utils/Utils.kt
  - id: openwiki-source-85760c3bcecebc4e22863df8
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/model/ToolbarFamily.kt
  - id: openwiki-source-aeab395696be887d732325d5
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/state/ToolbarComponentState.kt
  - id: openwiki-source-9b13cf9eedf504aa59a8b752
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/state/utils/Utils.kt
  - id: openwiki-source-eb85d7580b615eea5425ab90
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/toolbar/ToolbarComponent.kt
  - id: openwiki-source-55d0dcf874e3c3880b388961
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/topnavbar/TopNavBarComponent.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Components: top nav bar, toolbar, bottom nav bar, tabs, stepper

All components require [`Theme`](theme.md).

## Package layout

```
ui/components/
├── topnavbar/TopNavBarComponent.kt
├── toolbar/
│   ├── ToolbarComponent.kt
│   ├── model/ToolbarFamily.kt
│   ├── state/{ToolbarComponentState.kt, utils/Utils.kt}
│   └── behavior/{base/IToolbarScrollBehavior.kt, ToolbarPinnedScrollBehavior.kt,
│                 ToolbarEnterAlwaysScrollBehavior.kt, ToolbarExitUntilCollapsedScrollBehavior.kt, utils/Utils.kt}
├── bottomnavbar/
│   ├── BottomNavBarComponent.kt
│   ├── model/BottomNavBarStyle.kt
│   └── scrollconnection/{BottomNavBarScrollConnection.kt, model/BottomNavBarInlineBehavior.kt, utils/ComposeUtils.kt}
├── tab/{TabComponent.kt, model/TabStyle.kt}
└── stepper/StepperComponent.kt
```

---

## TopNavBarComponent

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBarComponent(
    modifier: Modifier = Modifier,
    title: String,
    actions: List<@Composable () -> Unit> = emptyList(),
    onNavigateBack: (() -> Unit)? = null,     // shows a back button when non-null
    onNavigateClose: (() -> Unit)? = null,    // shows a close button (after actions) when non-null
)
```
It's a Material3 `CenterAlignedTopAppBar`:
- Title is `Text(title, color = content.primary, style = body.large)`.
- The navigation icon is a Tertiary/Small `IconButtonComponent` with `R.drawable.ic_back` and `contentDescription = R.string.accessibility_Back` ("Back").
- Actions are each lambda in `actions`, then an optional close button (`R.drawable.ic_close`, "Close").
- `containerColor` and `scrolledContainerColor` are both `background.surface`.
- No `windowInsets` override, so Material3 defaults (status bar insets) apply, which is correct for edge-to-edge.

```kotlin
TopNavBarComponent(
    title = "Profile",
    onNavigateBack = { navigator.get().goBack() },
    actions = listOf({ IconButtonComponent(/* ... */) }),
)
```

---

## ToolbarComponent (collapsing header)

```kotlin
@Composable
fun ToolbarComponent(
    modifier: Modifier,
    scrollBehavior: IToolbarScrollBehavior,
    toolbarFamilyType: ToolbarFamilyType,
)
```
A custom collapsing header driven by nested scroll. **You must attach `scrollBehavior.nestedScrollConnection` to the scrolling content**
with `Modifier.nestedScroll(...)`. The header itself is also draggable (a `Modifier.scrollable` on the header) unless the behavior is pinned.

### `ToolbarFamilyType` (sealed)

| Variant | Behaviour |
|---|---|
| `Default(content: @Composable () -> Unit)` | The whole `content` scrolls away (offset by `heightOffset`, clipped). `heightOffsetLimit = -contentHeight`, so it can fully disappear. A shadow of `border2` (2 dp) animates in when `overlappedFraction > 0` (content scrolled under it). |
| `Animate(expandedContent, collapsedContent)` | Two slots. The **collapsed** content is always shown (clipped to its own height). The **expanded** content (offset by `heightOffset`) collapses until only the collapsed slot remains: `heightOffsetLimit = collapsedHeight - expandedHeight`. Accessibility: while `collapsedFraction < limit` (limit = `1 - collapsed/expanded`), the collapsed slot's semantics are cleared, otherwise the expanded slot's. Only one is exposed to TalkBack at a time. |

Both variants measure their content with `SubcomposeLayout`, write `expandedHeight`/`collapsedHeight` into the state, and settle with
`settleComponent` when the header drag ends.

### `IToolbarScrollBehavior` and factories

```kotlin
@Stable
interface IToolbarScrollBehavior {
    val state: ToolbarComponentState
    val isPinned: Boolean
    val snapAnimationSpec: AnimationSpec<Float>?
    val flingAnimationSpec: DecayAnimationSpec<Float>?
    val nestedScrollConnection: NestedScrollConnection
}
```

Create behaviors with the composable factories in `toolbar/behavior/utils/Utils.kt`:

| Factory | Implementation | Behaviour |
|---|---|---|
| `pinnedScrollBehavior(state = rememberToolbarComponentState(), canScroll = { true })` | `ToolbarPinnedScrollBehavior` | Never collapses (`isPinned = true`). Only tracks `contentOffset` (for the Default-family shadow). No snap or fling. |
| `enterAlwaysScrollBehavior(state, canScroll, snapAnimationSpec = spring(StiffnessMediumLow), flingAnimationSpec = rememberSplineBasedDecay())` | `ToolbarEnterAlwaysScrollBehavior` | `onPreScroll` consumes every scroll delta into `heightOffset`: it collapses when scrolling down and re-appears **as soon as** you scroll up, anywhere in the list. |
| `exitUntilCollapsedScrollBehavior(state, canScroll, snapAnimationSpec, flingAnimationSpec)` | `ToolbarExitUntilCollapsedScrollBehavior` | It collapses on upward drags in pre-scroll, and expands only in `onPostScroll` when the list can't consume more (that is, at the top of the list). |

`canScroll` lets you disable the behavior (for example while content is shorter than the screen). The behavior classes are public and constructible directly,
but the default `state` factory `rememberToolbarComponentState` is **`internal`**, so outside the module, use the factory functions with the default state.

### `ToolbarComponentState`
```kotlin
@Stable
class ToolbarComponentState(initialHeightOffsetLimit: Float, initialHeightOffset: Float, initialContentOffset: Float)
```
| Property | Meaning |
|---|---|
| `heightOffsetLimit: Float` | Most negative offset (`-expanded` or `collapsed - expanded`). It's set by the component. |
| `heightOffset: Float` | Current collapse offset, coerced to `[heightOffsetLimit, 0]`. |
| `expandedHeight`, `collapsedHeight: Float` | Measured px, set by the component. |
| `contentOffset: Float` | Accumulated content scroll, used for `overlappedFraction`. |
| `collapsedFraction: Float` | `heightOffset / heightOffsetLimit` (0 = expanded, 1 = collapsed). |
| `overlappedFraction: Float` | `0..1`, how much content has scrolled under the header. |
| `collapsedLimit: Lazy<Float>` | `1 - collapsedHeight / expandedHeight` (recomputed on each `get`). |
| `companion object Saver` | `listSaver` of `(heightOffsetLimit, heightOffset, contentOffset)`. `rememberToolbarComponentState` uses `rememberSaveable`. |

`internal suspend fun settleComponent(state, velocity, flingAnimationSpec, snapAnimationSpec): Velocity` (`state/utils/Utils.kt`) returns immediately if
`collapsedFraction < 0.01` or `== 1`. Otherwise it decays the fling into `heightOffset`, then, if the header is partially collapsed and
`collapsedFraction >= 1 - collapsed/expanded`, snaps it **fully collapsed**. It never snaps back to expanded.

### Example (from the module's preview)

```kotlin
val scrollBehavior = exitUntilCollapsedScrollBehavior()

Column(Modifier.fillMaxSize()) {
    ToolbarComponent(
        modifier = Modifier,
        scrollBehavior = scrollBehavior,
        toolbarFamilyType = ToolbarFamilyType.Animate(
            expandedContent = { ProfileHeaderExpanded(state) },
            collapsedContent = { TopNavBarComponent(title = state.name, onNavigateBack = onBack) },
        ),
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { /* items */ }
}
```

---

## BottomNavBarComponent

```kotlin
@Composable
fun BottomNavBarComponent(
    modifier: Modifier,
    bottomNavGraphs: Set<INavigationGraphBottomNavBar>,     // navigation3 graph type
    selectedNavGraph: INavigationGraphBottomNavBar?,
    scrollConnection: BottomNavBarScrollConnection,
    onTabSelected: (INavigationGraphBottomNavBar) -> Unit,
)
```
`BaseActivity` renders it automatically for graphs that implement `INavigationGraphBottomNavBar` (see [app-and-activity](app-and-activity.md)). Features normally
don't call it directly. They make the graph implement `INavigationGraphBottomNavBar` (`order`, `icon`, `label`, `notificationCount`, `isFab`).

Rendering:
- Graphs are sorted by `order` (remembered once, without a key).
- An `AnimatedContent` (fade) switches between the **expanded** and **inline** layouts according to `scrollConnection.isInline`. `onSizeChanged` writes
  the bar height into `scrollConnection.bottomBarHeight`.
- **Tabs pill** (non-FAB graphs): `CircleShape`, 2 dp shadow, `background.base`, a 2 dp `border.subtle` border, and equal-weight tab cells. Each cell is `selectable(role = Role.Tab)`
  with `BorderIndication`, shows the icon and (expanded only) the label in `body.small`. The selected cell has a `background.accent` fill.
  The cell's `contentDescription` is the label. The label text itself is hidden from accessibility.
- If there's at most one non-FAB tab, a `Spacer(weight = 1f)` is added.
- **FABs** (`isFab = true`): circular icon buttons (`Role.Button`), padded 8 dp (expanded) or 4 dp (inline), `contentDescription = label`.
- Clicking any item calls `scrollConnection.expand()` and then `onTabSelected(graph)`.
- Accessibility: the row is a traversal group with `CollectionInfo(rowCount = 1, columnCount = size)`, and items have `CollectionItemInfo(columnIndex = order)`.

`BottomNavBarStyle` (internal, `rememberBottomNavBarStyle(isSelected)`): background `background.accent` if selected else `background.base`;
content `content.onAccent` (for every state); indication `BorderIndication(border.onAccent, border2, CircleShape)`.

**`notificationCount: StateFlow<Int>` is never read by the component**, so badges aren't rendered.

### Scroll connection

`val LocalBottomNavBarScrollConnection = staticCompositionLocalOf<BottomNavBarScrollConnection> { error(...) }` is provided by `BaseActivity`.

```kotlin
@Stable
class BottomNavBarScrollConnection(
    initialIsInline: Boolean,
    private val scrollThresholdPx: Float,
    private val inlineBehavior: BottomNavBarInlineBehavior,
) : NestedScrollConnection {
    var isInline: Boolean            // private set
    var bottomBarHeight: Dp          // written by the component
    fun expand()
    fun inline()
}
```
`onPostScroll` accumulates `consumed.y` (it resets when the direction flips). Once the accumulation passes `±scrollThresholdPx`, it toggles `isInline` according to
`BottomNavBarInlineBehavior`:

| Entry | Meaning |
|---|---|
| `Never` | Never changes. |
| `OnScrollDown` | Goes inline when the content scrolls down (finger moves up, negative delta), and expands on scroll up. **Default.** |
| `OnScrollUp` | The inverse. |

It never consumes scroll (returns `Offset.Zero`).

Helpers (`scrollconnection/utils/ComposeUtils.kt`):
- `@Composable fun rememberBottomNavBarScrollConnection(initialIsInline = false, scrollThreshold: Dp = space48, inlineBehavior = OnScrollDown)`.
- `@Composable fun Modifier.bottomNavBarPadding(extraPadding: Dp = space16): Modifier` adds an animated bottom padding of `bottomBarHeight + extraPadding`,
  so the last content isn't hidden under the floating bar. It **reads `LocalBottomNavBarScrollConnection`**, so it crashes outside `BaseActivity`/a provider.

Screen pattern (from `demo/presentation/.../DemoScreen.kt`):
```kotlin
Column(
    modifier = Modifier
        .nestedScroll(LocalBottomNavBarScrollConnection.current)
        .verticalScroll(rememberScrollState())
        .windowInsetsPadding(NavigationBarDefaults.windowInsets)
        .bottomNavBarPadding()
) { /* content */ }
```

---

## TabComponent

```kotlin
@Composable
fun <TAB : Enum<TAB>> TabComponent(
    modifier: Modifier,
    tabs: Map<TAB, TabStyleType>,      // insertion order = tab order
    currentTab: TAB,                   // INITIAL selection only
    content: @Composable (TAB) -> Unit, // renders the selected tab's content below the row
)
```
It's a Material3 `PrimaryTabRow` (transparent container, `content.primary`) with a `PrimaryIndicator` in `content.accent`. Each `Tab` shows its text
(`body.medium`) and/or 24 dp icon, coloured `content.accent` when selected and `content.primary` otherwise.

`TabStyleType` (sealed class with `open val icon: Int?`, `open val text: String?`): `IconText(icon, text)`, `Icon(icon)`, `Text(text)`.

Gotchas: selection is **internal state** (`remember { mutableIntStateOf(index of currentTab) }`, not keyed). Changing `currentTab` later has no effect, and
there's **no callback** for tab changes, so the ViewModel can't observe the selected tab. Icons have `contentDescription = null` (an `Icon`-only tab has no accessible label).

## StepperComponent

```kotlin
@Composable
fun <STEP : Enum<STEP>> StepperComponent(
    modifier: Modifier,
    steps: List<STEP>,
    currentStep: STEP,
    content: @Composable (STEP) -> Unit,    // rendered under the progress bar
)
```
A row of equal-weight pill segments (4 dp gaps). Reached segments (`index <= current`) are filled `background.accent`. Unreached ones are transparent with a 1 dp
`border.onAccent` outline. The current segment is 8 dp tall, the others 4 dp. The whole column has `contentDescription` = `R.string.accessibility_step`
("Step %1$d of %2$d, progress bar"). Stateless: drive `currentStep` from the ViewModel.

```kotlin
enum class OnboardingStep { Personal, Address, Confirm }
StepperComponent(modifier = Modifier, steps = OnboardingStep.entries, currentStep = state.step) { step ->
    when (step) { OnboardingStep.Personal -> PersonalForm(); OnboardingStep.Address -> AddressForm(); OnboardingStep.Confirm -> Summary() }
}
```

## Related pages

- [CoreApp and BaseActivity](app-and-activity.md)
- [Navigation 3 (`INavigationGraphBottomNavBar`)](navigation3.md)
- [UI utilities (`BorderIndication`)](ui-utils.md)
- [Design tokens](theme-tokens.md)
