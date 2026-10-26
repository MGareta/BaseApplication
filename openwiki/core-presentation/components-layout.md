---
type: component reference
title: "Components: container, bottom sheet, lists and paging"
description: Reference for core/presentation's layout components. Covers BottomSheetComponent and its styles, NonLazyGridComponent, the PagingResponse model used by withUseCaseScopePaging, and the ContainerComponent, PagingComponent and list Loading/Error helpers, which are currently commented out and not compiled.
tags: [core-presentation, compose, components, bottom-sheet, grid, paging, layout]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-f9dc87a14feb4e6505487f30
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/bottomsheet/BottomSheetComponent.kt
  - id: openwiki-source-1ebcd49c7bef92d70cbeae7c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/bottomsheet/model/BottomSheetStyle.kt
  - id: openwiki-source-d8ace04b38eff0a0ea3f130e
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/container/ContainerComponent.kt
  - id: openwiki-source-84dd3fea3ac3f1c41c8095b7
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/list/model/PagingResponse.kt
  - id: openwiki-source-9d3e5ca0878ac37096dc3a1a
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/list/NonLazyGridComponent.kt
  - id: openwiki-source-40149f5fd35e52c77e91275f
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/list/PagingComponent.kt
  - id: openwiki-source-cd09fa6f1e214213be3f3210
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/list/utils/Extensions.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Components: container, bottom sheet, lists and paging

## Package layout and status

```
ui/components/
├── bottomsheet/
│   ├── BottomSheetComponent.kt          # active
│   └── model/BottomSheetStyle.kt        # active
├── container/
│   └── ContainerComponent.kt            # ENTIRE FILE COMMENTED OUT
└── list/
    ├── NonLazyGridComponent.kt          # active
    ├── PagingComponent.kt               # ENTIRE FILE COMMENTED OUT
    ├── model/PagingResponse.kt          # active
    └── utils/Extensions.kt              # ENTIRE FILE COMMENTED OUT
```

`ContainerComponent.kt`, `PagingComponent.kt` and `list/utils/Extensions.kt` are fully commented out, including their `package` lines, so
they **don't compile and can't be used**. These three files currently have uncommitted modifications in the working tree.
They're documented below so agents know what existed and what would be needed to restore them.

---

## BottomSheetComponent

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    modifier: Modifier,
    bottomSheetStyleType: BottomSheetStyleType,
    title: String,
    text: String?,
    content: (@Composable (SheetState) -> Unit)?,    // receives the sheet state, e.g. to hide() after an action
    onDismiss: () -> Unit,
)
```

A self-contained Material3 `ModalBottomSheet` for **status messages** (warning/info/success/error), shown while it's in composition:
- `sheetState = rememberModalBottomSheetState(confirmValueChange = { if (it == SheetValue.Hidden) onDismiss(); true })`.
- `onDismissRequest = onDismiss`, `containerColor`/`contentColor` from the style, `dragHandle = null`.
- `contentWindowInsets = BottomSheetDefaults.windowInsets + 16 dp left/right`.
- Content column (a traversal group):
  1. A close `IconButtonComponent` (Secondary, Small, `R.drawable.ic_close`, `contentDescription = stringResource(R.string.accessibility_close)`),
     aligned end with 16 dp vertical padding and `traversalIndex = 1f` (read after the content). Clicking it launches `sheetState.hide()`.
  2. A row with the 24 dp style icon and `title` (`title.medium`, no font padding).
  3. Optional `text` (`body.medium`).
  4. `content?.invoke(sheetState)`.

`BottomSheetStyleType` (sealed `data object`s), via internal `rememberBottomSheetStyle` keyed by type and palette:

| Style | Background | Content | Icon |
|---|---|---|---|
| `Warning` | `background.warningLow` | `content.warning` | `R.drawable.ic_warning` |
| `Info` | `background.infoLow` | `content.info` | `R.drawable.ic_info` |
| `Success` | `background.successLow` | `content.success` | `R.drawable.ic_success` |
| `Error` | `background.errorLow` | `content.error` | `R.drawable.ic_error` |

Usage (control visibility with state, because the sheet shows whenever it's composed):

```kotlin
if (state.showPaymentError) {
    BottomSheetComponent(
        modifier = Modifier,
        bottomSheetStyleType = BottomSheetStyleType.Error,
        title = "Payment failed",
        text = state.errorMessage,
        content = { sheetState ->
            val scope = rememberCoroutineScope()
            BlockButtonComponent(
                modifier = Modifier.fillMaxWidth(),
                blockButtonStyleType = BlockButtonStyleType.Primary,
                blockButtonSizeType = BlockButtonSizeType.Medium,
                text = "Retry", leadingIcon = null, trailingIcon = null, enabled = true,
                accessibilityState = AccessibilityState.Click(disabledReason = ""),
                onClick = { scope.launch { sheetState.hide() }; onEvent(Event.Retry) },
            )
        },
        onDismiss = { onEvent(Event.DismissError) },
    )
}
```

**When to use it vs. a navigation bottom sheet:** use `BottomSheetComponent` for transient feedback owned by one screen. For a bottom sheet that is
its own destination (own ViewModel, back-stack entry), use a Navigation3 `INavigationEntryBottomSheet` (see [navigation3](navigation3.md)).

Gotchas:
- **`onDismiss` can fire twice.** Swiping down or tapping the scrim triggers both `confirmValueChange(Hidden)` and `onDismissRequest`. Make `onDismiss`
  idempotent (for example set a boolean flag to false).
- The style icon has `contentDescription = null`, so the type (error/success) is only conveyed by the title text.

## NonLazyGridComponent

```kotlin
@Composable
fun NonLazyGridComponent(
    modifier: Modifier,
    columns: Int,
    itemCount: Int,
    verticalSpacing: Dp,
    horizontalSpacing: Dp,
    content: @Composable (Int) -> Unit,    // called with the item index
)
```
A **non-lazy** grid built from a `Column` of `Row`s, safe to nest inside a `verticalScroll` or a `LazyColumn` item, where lazy grids aren't allowed.
- Rows = `ceil(itemCount / columns)`. Each cell is a `Box` with `fillMaxWidth().weight(1f)`, so all columns have equal width. Empty trailing
  cells in the last row keep the alignment.
- Accessibility: the column has `selectableGroup()` + `CollectionInfo(rowCount, columnCount = columns)`. Every filled cell has `CollectionItemInfo(rowId, 1, columnId, 1)`.
- All items are composed at once, so use it for small, bounded collections (menus, option tiles), not long lists.

```kotlin
NonLazyGridComponent(
    modifier = Modifier.fillMaxWidth(),
    columns = 2,
    itemCount = state.options.size,
    verticalSpacing = LocalDimensions.current.dp.space.space8,
    horizontalSpacing = LocalDimensions.current.dp.space.space8,
) { index -> OptionTile(option = state.options[index]) }
```

## PagingResponse

```kotlin
data class PagingResponse<KEY, ITEM>(val pageId: KEY?, val data: List<ITEM>)
```
The page payload expected by `CoroutinesUseCaseRunner.withUseCaseScopePaging`: `data` becomes `LoadResult.Page.data`, and `pageId` is passed to
`calculateNextPage` to compute `nextKey` (see [coroutines](coroutines.md)). Map your API DTO to it in the repository or use case.

## Disabled components (commented out)

### ContainerComponent (commented out)
It was a `Scaffold`-based screen container with `modifier`, `containerColor = Transparent`, `contentPaddingValues`, an optional `scrollState`
(`verticalScroll`), `topBar`, `bottomBar`, `keyboardContentDocked` (replaces the bottom bar while the keyboard is open, using `isKeyboardOpenAsState()`),
`keyboardContentUndocked` (animated out while the keyboard is open), `imePadding()`, and zero content window insets.
It references APIs that **no longer exist**: `com.core.presentation.ui.utils.conditional` (the current helpers are `thenOnTrue`/`then` in
[ui-utils](ui-utils.md)) and `LocalDimensions.current.dp.dp0` (now `dp.space.space0`).

### PagingComponent (commented out)
It was `fun <ITEM : Any> PagingComponent(modifier, verticalArrangement, contentPadding, items: Flow<PagingData<ITEM>>, content: @Composable (ITEM) -> Unit)`,
a `LazyColumn` over `collectAsLazyPagingItems()` that showed `Loading()`/`Error(message)` rows for the prepend/refresh/append load states and keyed items by
`pagingItems[it].hashCode()`. To page today, use `androidx.paging.compose.collectAsLazyPagingItems()` directly in your screen (paging-compose
availability depends on the module's dependencies, see [overview](overview.md)).

### list/utils/Extensions.kt (commented out)
It held `LazyListScope.Loading()` (a `CircularProgressIndicator` item) and `LazyListScope.Error(message)` (a `Text` item). It references removed tokens
(`contentPalette.mid`, `typographies.mid.l`).

To restore any of these files: uncomment them, then update their token and helper references to the current theme API ([design tokens](theme-tokens.md)).

## Related pages

- [Coroutines (`withUseCaseScopePaging`)](coroutines.md)
- [Buttons and alerts](components-actions.md)
- [Design tokens](theme-tokens.md)
- [Navigation 3 bottom sheet entries](navigation3.md)
