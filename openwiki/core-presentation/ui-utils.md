---
type: api reference
title: UI utilities
description: Reference for core/presentation's ui/utils package. Covers AccessibilityState and accessibilityFocusTarget, the BorderIndication press/hover/focus indication, ComposableLifecycle and rememberFlowWithLifecycle, conditional Modifier helpers, density conversions, ignoreHorizontalParentPadding, amount helpers (inputToAmount, splitByDecimalSeparator) and isKeyboardOpenAsState.
tags: [core-presentation, compose, utilities, accessibility, modifiers, lifecycle, keyboard]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-6ca0c45addf0b6ca6953d9b1
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/AccessibilityUtils.kt
  - id: openwiki-source-f5ace90654bb6a8f0a4c9efd
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/BorderIndication.kt
  - id: openwiki-source-3846d483f0f897d13b7ed69f
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/ComposeUtils.kt
  - id: openwiki-source-9c083d2620d164d5dad2dd79
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/Extensions.kt
  - id: openwiki-source-b4168defbb7d852bdf5a54c2
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/KeyboardUtils.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# UI utilities (`com.core.presentation.ui.utils`)

```
ui/utils/
├── AccessibilityUtils.kt   # AccessibilityState, Modifier.accessibilityFocusTarget
├── BorderIndication.kt     # IndicationNodeFactory drawing border/overlay on interactions
├── ComposeUtils.kt         # ComposableLifecycle, rememberFlowWithLifecycle
├── Extensions.kt           # dp/px conversion, conditional modifiers, ignoreHorizontalParentPadding, amount helpers
└── KeyboardUtils.kt        # isKeyboardOpenAsState
```

## AccessibilityUtils.kt

### `AccessibilityState`
```kotlin
@Immutable
sealed interface AccessibilityState {
    data class Click(val disabledReason: String)                       // NOTE: does not implement AccessibilityState
    data class Select(val selected: String, val notSelected: String) : AccessibilityState
    data class Description(val description: String)                    // NOTE: does not implement AccessibilityState
}
```
These are the typed accessibility inputs required by components:
- `Click`: buttons, fields and selects. `disabledReason` becomes the `stateDescription` when the control is disabled. Pass `""` when it can't be disabled.
- `Select`: checkbox, chip and toggle. The texts announced for checked and unchecked.
- `Description`: Badge. Replaces the semantic text.

Only `Select` actually implements the sealed interface. `Click` and `Description` are just nested classes, so `AccessibilityState` can't be used polymorphically for them.

### `Modifier.accessibilityFocusTarget`
```kotlin
@Composable
fun Modifier.accessibilityFocusTarget(key: Any?, requester: FocusRequester, delayMillis: Long = 50L): Modifier
```
Whenever `key` changes, it clears focus, waits `delayMillis`, then `requester.requestFocus()`. It returns `this.focusRequester(requester).focusable()`. Use it to move TalkBack or keyboard
focus to a heading or an error after content changes:
```kotlin
val requester = remember { FocusRequester() }
Text(text = state.title, modifier = Modifier.accessibilityFocusTarget(key = state.step, requester = requester))
```

## BorderIndication.kt

```kotlin
class BorderIndication(borderColor: Color, strokeWidth: Dp, shape: Shape) : IndicationNodeFactory
```
It creates a private `BorderIndicationNode` (`Modifier.Node`, `DrawModifierNode`) that collects the `InteractionSource`:
- **Pressed:** draws a black 30% overlay over the whole bounds.
- **Hovered or focused** (not pressed): draws the shape outline stroke (`strokeWidth`, `borderColor`) plus a black 10% overlay.
- `equals`/`hashCode` are based on colour, width and shape, as `IndicationNodeFactory` requires.

The overlay uses `drawRect` over the full size, **not clipped to `shape`**, so clip the element first (`Modifier.clip(shape)`) for rounded components (the bottom nav bar does this).
```kotlin
Modifier
    .clip(CircleShape)
    .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = BorderIndication(LocalPalette.current.borderPalette.onAccent, LocalDimensions.current.dp.border.border2, CircleShape),
        onClick = onClick,
    )
```

## ComposeUtils.kt

```kotlin
@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onComposableStarted: () -> Unit,
    onComposableStopped: () -> Unit,
    vararg keys: Any?,
)
```
It wraps `LifecycleStartEffect(keys, lifecycleOwner)`: `onComposableStarted` runs on `ON_START` (or immediately if already started), and `onComposableStopped` runs on `ON_STOP` **or** when the
composable leaves the composition. The legacy navigation stack uses it, and it's handy in Navigation3 `Render` to pause polling, analytics, etc.

```kotlin
@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): Flow<T>
```
It returns `remember(flow, lifecycle) { flow.flowWithLifecycle(lifecycle, minActiveState) }`. Use it to collect one-off effects only while started:
`LaunchedEffect(Unit) { rememberedEffects.collect { ... } }`.

## Extensions.kt

| Function | Semantics |
|---|---|
| `@Composable fun Dp.dpToPx(): Float` | `toPx()` with `LocalDensity` (note the theme forces `fontScale = 1`, which doesn't affect dp). |
| `@Composable fun Dp.dpToRoundPx(): Int` | `roundToPx()`. |
| `@Composable fun Float.pxToDp(): Dp` | `toDp()`. |
| `@Composable fun Modifier.thenOnTrue(condition: Boolean, block: @Composable Modifier.() -> Modifier)` | Appends `Modifier.block()` when `true`. |
| `@Composable fun Modifier.thenOnFalse(condition, block)` | Appends when `false`. |
| `@Composable fun Modifier.then(condition: Boolean, onTrue, onFalse)` | One or the other. |
| `@Composable fun <T : Any> Modifier.thenOnNotNull(condition: T?, block: @Composable Modifier.(T) -> Modifier)` | Appends with the non-null value (used by Checkbox/Toggle borders). |
| `@Composable fun <T : Any> Modifier.thenOnNull(condition: T?, block)` | Appends when `null`. |
| `@Composable fun <T : Any> Modifier.then(condition: T?, onNotNull, onNull)` | One or the other. |
| `fun Modifier.ignoreHorizontalParentPadding(horizontal: Dp)` | Measures the child with `maxWidth + 2 * horizontal` so it can bleed into the parent's horizontal padding (used to align the text-field supporting text with the field edges). |
| `fun String.inputToAmount(fractionDigits: Int): BigDecimal` | Converts the raw digit string of `AmountFieldComponent` to an amount: `BigDecimal(BigInteger(this), fractionDigits)` (`"12345"`, 2 → `123.45`). Empty gives `BigDecimal(0.0)`. Non-digits throw `NumberFormatException`. |
| `internal fun Number?.splitByDecimalSeparator(fractionDigits: Int?, positivePrefix: String? = null, locale: Locale = Locale.getDefault()): Pair<String, String?>` | Locale-formats with min = max fraction digits (`fractionDigits ?: 2`), `RoundingMode.DOWN`, and an optional positive prefix. It returns `(integerPart, decimalSeparator + decimals)` or `(formatted, null)` if there's no separator. `null` gives `("---", "<sep>--")`. Used by `AmountComponent`. |

All conditional helpers are `@Composable` (the blocks may read composition locals), so they can only be used in composable scope.
They return `this` unchanged when no branch applies.

## KeyboardUtils.kt

```kotlin
@Composable
fun isKeyboardOpenAsState(): State<Boolean>
```
It registers a `ViewTreeObserver.OnGlobalLayoutListener` on `LocalView` (removed on dispose) and reports `true` when the hidden part of the window
(`rootView.height - visibleDisplayFrame.bottom`) is greater than 15% of the screen height. It works with `adjustResize` (the app's `MainActivity` uses it). Prefer
`WindowInsets.isImeVisible` in new code. This heuristic is kept for compatibility (the commented-out `ContainerComponent` used it).

## Related pages

- [Buttons, badge, alert](components-actions.md)
- [Form fields](form-fields.md)
- [Bottom nav bar](components-navigation.md)
- [Legacy navigation](navigation-legacy.md)
