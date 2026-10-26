---
type: component reference
title: Form field components
description: Reference for every form field composable in core/presentation. Covers text, text area, password, amount (with CurrencyType and AmountVisualTransformation), date, range date and OTP inputs; checkbox, chip and toggle selects; and multi-checkbox, multi-chip and multi-toggle groups. Includes parameters, the state each one binds to, styles and tokens, accessibility and known bugs.
tags: [core-presentation, compose, forms, text-field, date-picker, otp, checkbox, chip, toggle, multiselect]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-99e5f3d3c5400ce76d2ffbf6
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/amount/AmountFieldComponent.kt
  - id: openwiki-source-b7c51510e29416290307074c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/amount/model/CurrencyType.kt
  - id: openwiki-source-cb23cbb0434663e36d41293d
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/date/DateFieldComponent.kt
  - id: openwiki-source-692dff9251780df5b686c4e8
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/date/RangeDateFieldComponent.kt
  - id: openwiki-source-7a33cb26134743217a9f363b
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/otp/OtpFieldComponent.kt
  - id: openwiki-source-a10afd7326203f411a0fdf81
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/text/TextAreaComponent.kt
  - id: openwiki-source-0482c1d1152970385a9c9dd3
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/input/text/TextFieldComponent.kt
  - id: openwiki-source-9daa537c6c6fb90afe86592b
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/multiselect/checkbox/MultiCheckboxComponent.kt
  - id: openwiki-source-2bf94f7fa07fbea5bf50b7b8
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/multiselect/chip/MultiChipComponent.kt
  - id: openwiki-source-a1da564c7b588d71b6eb23d0
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/multiselect/toggle/MultiToggleComponent.kt
  - id: openwiki-source-cc7ac34383fd62b287b50f60
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/select/checkbox/CheckboxComponent.kt
  - id: openwiki-source-6046c58432e662747761a505
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/select/chip/ChipComponent.kt
  - id: openwiki-source-f94ad8df32f8bfb35af5f259
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/form/fields/select/toggle/ToggleComponent.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Form field components

Every field is **bound to a field state object** (see [forms](forms.md)). The field reads `state.value`, writes through `state.change(...)`,
and shows `state.errorMessage` after `state.validate()` has set an error. Create the states in the ViewModel (or `remember` them) and call
`FormState(listOf(...)).validate()` on submit.

| Field composable | State type |
|---|---|
| `TextFieldComponent`, `TextAreaComponent`, `PasswordFieldComponent`, `AmountFieldComponent`, `OtpFieldComponent` | `TextFieldState` |
| `DateFieldComponent` | `DateFieldFieldState` |
| `RangeDateFieldComponent` | `RangeDateFieldFieldState` |
| `CheckboxComponent`, `ChipComponent`, `ToggleComponent` | `SelectFieldState` |
| `MultiCheckboxComponent`, `MultiChipComponent`, `MultiToggleComponent` | `MultiSelectFieldState<SelectFieldState>` + one `SelectFieldState` per item |

## Package layout

```
ui/components/form/fields/
├── input/
│   ├── text/{TextFieldComponent.kt, TextAreaComponent.kt}
│   ├── password/PasswordFieldComponent.kt
│   ├── amount/{AmountFieldComponent.kt, model/AmountFieldStyle.kt, model/CurrencyType.kt, utils/AmountVisualTransformation.kt}
│   ├── date/{DateFieldComponent.kt, RangeDateFieldComponent.kt, model/DateFieldStyle.kt}
│   └── otp/{OtpFieldComponent.kt, model/OtpFieldStyle.kt}
├── select/
│   ├── checkbox/{CheckboxComponent.kt, model/CheckboxStyle.kt}
│   ├── chip/{ChipComponent.kt, model/ChipStyle.kt}
│   └── toggle/{ToggleComponent.kt, model/ToggleStyle.kt}
└── multiselect/
    ├── checkbox/MultiCheckboxComponent.kt
    ├── chip/MultiChipComponent.kt
    └── toggle/MultiToggleComponent.kt
```
(State classes such as `TextFieldState` are documented in [forms](forms.md).)

---

## Text inputs

### TextFieldComponent
```kotlin
@Composable
fun TextFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,                                   // rendered as the floating LABEL
    helpText: String? = null,
    successText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textAlign: TextAlign = TextAlign.Unspecified,
    enabled: Boolean,
    readOnly: Boolean = false,
    onValueChange: ((String) -> Unit)? = null,             // default: state.change(it)
)
```
It's a Material3 `OutlinedTextField`, `singleLine = true`, `shape = radius4` (4 dp), text style `body.medium` (+`textAlign`).
- **Label:** `placeholder` is shown as the `label` (`body.medium`). The Material `placeholder` slot is unused.
- **Error display:** `isError = state.hasError && state.value?.isNotBlank() == true && !isFocused`. The supporting row shows, when the value is non-blank,
  the error (`content.error`, `label.medium`, hidden from a11y because the error is in the field semantics) if unfocused and in error, else `successText` (`content.success`)
  if unfocused, else `helpText` (`content.secondary`). When the value is blank, only `helpText` is shown. The supporting text ignores the 16 dp parent padding (`ignoreHorizontalParentPadding`).
- **Semantics:** `mergeDescendants = true`, `error(errorMessage)` while in error, `disabled()` when not enabled.
- **Keyboard:** `Tab`/`Shift+Tab` move focus. IME `Done`/`Go`/`Search`/`Send` clear focus then call your matching action. `Next`/`Previous` move focus and
  **ignore your `onNext`/`onPrevious`**.
- **Colours:** text focused `content.primary`, unfocused `content.secondary`, disabled `content.disabled`, error `content.error`. Container transparent,
  disabled `background.disabled`, error `background.errorLow`. Border focused `border.active`, unfocused `border.standard`, disabled `border.disabled`, error `border.error`.
  Icons, label, prefix and suffix follow the same primary/secondary/disabled/error scheme.

> **Gotcha:** an error is only *displayed* while the value is non-blank. A failing `Validators.Required` on an empty field makes `validate()` return `false` but shows **no**
> visual error. Show your own message (for example via screen state) for empty required fields.

### TextAreaComponent
```kotlin
@Composable
fun TextAreaComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean,
)
```
It has the same behaviour and colours as `TextFieldComponent`, but it's multi-line (`minLines = 5`, no icons, prefix or suffix, always `state.change`). The supporting row adds a
**character counter** `"<length>/<limit>"` on the right, where `limit` is the first `Validators.MaxSize` in `state.validators` or `"∞"`. The counter turns
`content.error` when in error and is announced with `R.string.max_chars` ("%s of %s"). `MaxSize` isn't enforced while typing: the text can exceed it until `validate()`.

### PasswordFieldComponent
```kotlin
@Composable
fun PasswordFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,
    helpText: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean,
)
```
It wraps `TextFieldComponent` with `keyboardType = Password`, `PasswordVisualTransformation()` until toggled, and a trailing Tertiary/Medium icon button that toggles visibility
(`ic_password_show`/`ic_password_hide`, a11y "Show password"/"Hide password"). Visibility is local `remember` state, so it isn't saved across configuration changes.

### AmountFieldComponent
```kotlin
@Composable
fun AmountFieldComponent(
    modifier: Modifier,
    state: TextFieldState,          // stores RAW DIGITS in minor units, e.g. "12345" == 123.45 with 2 decimals
    fractionDigits: Int?,           // null -> 2
    currencyType: CurrencyType,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean,
)
```
It's a `TextFieldComponent` with `keyboardType = Number`, `textAlign = End`, a currency **suffix** (`title.large` with `FontWeight.Light`), and
`AmountVisualTransformation(fractionDigits, currencyType = null, style)`. Input filter: only digit-only strings are accepted, and **any value starting with `"0"` is rejected**,
so you can't type a leading zero (the integer part shows `0` until enough digits are typed).
Convert the raw digits with `String.inputToAmount(fractionDigits): BigDecimal` from `ui/utils/Extensions.kt` (empty gives `0.0`).

Currency resolution: `Symbol` calls `Currency.getInstance(currencyType.currency).symbol`, `Code` calls `Currency.getInstance(...).currencyCode`, and `Custom` uses the string.
> **Bug:** `CurrencyType.Symbol(value).currency` already holds the *symbol* (for example `"€"`), and `Currency.getInstance("€")` throws
> `IllegalArgumentException`. Use `CurrencyType.Code(...)` or `CurrencyType.Custom("€")` with this component.

`AmountFieldStyle` (internal, `rememberAmountFieldStyle()`): `integerTextStyle = title.large`, `decimalTextStyle = body.large`.

#### AmountVisualTransformation (internal)
`internal class AmountVisualTransformation(fractionDigits: Int?, currencyType: CurrencyType?, style: AmountFieldStyle, cursor: OffsetMappingType = MOVABLE, locale: Locale = Locale.getDefault()) : VisualTransformation`
- It formats the raw digits: the last `fractionDigits` (default 2) digits are decimals (left-padded with zeros), and the rest are grouped in threes with the locale grouping
  separator (`0` if empty). Empty input renders `"--"`. The integer uses the integer style's size, and the decimal separator and decimals use the decimal style's size.
  If `currencyType != null`, `" <currency>"` is appended (the field passes `null` and uses the suffix instead).
- `enum class OffsetMappingType { FIXED, MOVABLE }` (public): `FIXED` keeps the cursor at the end, and `MOVABLE` maps offsets skipping separators.

`CurrencyType` (`amount/model/CurrencyType.kt`): `sealed class CurrencyType(val currency: String)` with `Symbol(value: java.util.Currency)` (currency = symbol),
`Code(value: Currency)` (currency = ISO code) and `Custom(value: String)`. It's also used by `AmountComponent` ([components-actions](components-actions.md)).

### OtpFieldComponent
```kotlin
@Composable
fun OtpFieldComponent(
    modifier: Modifier,
    state: TextFieldState,          // MUST contain Validators.MaxSize -> number of boxes
    helpText: String? = null,
    successText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean,
)
```
A `BasicTextField` (`keyboardType = NumberPassword`) whose decoration draws `limit` equal-weight boxes (min 48 dp tall, `radius4`, `title.medium`) plus a
show/hide toggle (Tertiary/Medium icon button, `Role.Switch` with a toggleable state). `limit` is the first `Validators.MaxSize<String>`. **Without one it throws
`IllegalStateException("Max size validator is mandatory")`.** Input longer than `limit` is ignored, and the value is trimmed. The digits are masked with
`PasswordVisualTransformation` by default. A blinking 2×24 dp cursor is drawn in the next empty box while focused. The boxes are hidden from accessibility, and the field
exposes `maxTextLength = limit`, error and disabled semantics. The supporting text follows the same rules as TextField (but it isn't gated on non-blank).

`OtpFieldStyle` (internal, `rememberOtpFieldStyle(enabled, isError, isFocused, isDigitFocused, isDigitHovered)`):

| Condition | Background | Content | Border (1 dp) |
|---|---|---|---|
| `!enabled` | `background.disabled` | `content.disabled` | `border.disabled` |
| unfocused & error | `background.errorLow` | `content.error` | `border.error` |
| current box focused/hovered | transparent | `content.primary` | `border.active` |
| otherwise | transparent | `content.primary` | `border.standard` |

The toggle's `contentDescription` is always "Show password", even when the code is visible.

### DateFieldComponent
```kotlin
@Composable
fun DateFieldComponent(
    modifier: Modifier,
    state: DateFieldFieldState,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    enabled: Boolean,
)
```
It renders a **read-only** `TextFieldComponent` showing `DatePickerDefaults.dateFormatter().formatDate(state.value)` (default locale). A tap (pointer input on the
`Initial` pass) or an a11y click opens a Material3 `DatePickerDialog` + `DatePicker` (`showModeToggle = false`). Selectable dates are limited to
`[MinDate.limit, MaxDate.limit]` from the state's validators, or `DatePickerDefaults.YearRange` (Jan 1 to Dec 31) by default. OK (Primary/Small `BlockButtonComponent`,
disabled until a date is selected, a11y reason "Select a date") calls `state.change(Calendar(millis))`. Cancel (Secondary/Small) closes the dialog.

`DateFieldStyle` (internal, shared with the range picker): background `background.base`, content `content.primary`, disabled background/content
`background.disabled`/`content.disabled`, selected day `background.accent`, today border `border.active`, in-range day `background.accentLow`, divider `border.standard`.

> **Gotchas:** the displayed text field uses a **new, validator-less `TextFieldState` built on every recomposition**, so `DateFieldFieldState` validation errors
> are **never shown**. Picker bounds are inclusive while `Validators.MinDate`/`MaxDate` are **exclusive** (`after`/`before`). The picker works in UTC millis while
> `Calendar.getInstance()` uses the local time zone, so edge dates can shift by a day.

### RangeDateFieldComponent
```kotlin
@Composable
fun RangeDateFieldComponent(
    modifier: Modifier,
    state: RangeDateFieldFieldState,     // value: android.util.Range<Calendar>
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    enabled: Boolean,
)
```
It has the same pattern with `DateRangePicker` / `rememberDateRangePickerState`. The read-only field shows the start date as **prefix**, `" ---> "` as text and the end date as **suffix**, centred.
Prefix and suffix are announced as "From, …" / "To, …". OK is enabled only when both ends are selected and calls `state.change(Range(start, end))`. It has the same gotchas
(errors not displayed, inclusive vs. exclusive bounds, time zone).

---

## Select inputs (single boolean)

Shared parameters:

| Parameter | Meaning |
|---|---|
| `state: SelectFieldState` | Boolean state (`getData()` is the checked value). |
| `accessibilityStateSelect: AccessibilityState.Select(selected, notSelected)` | `stateDescription` text for checked/unchecked. |
| `accessibilityStateClick: AccessibilityState.Click(disabledReason)` | Appended to `stateDescription` when disabled. |
| `enabled: Boolean` | When `false` the control stays focusable but ignores clicks and exposes `disabled()`. |
| `onValueChange: ((Boolean) -> Unit)? = null` | Override. Default behaviour differs per component (see bugs). |

### CheckboxComponent
```kotlin
@Composable
fun CheckboxComponent(modifier, state: SelectFieldState, checkboxStyleType: CheckboxStyleType,
    accessibilityStateSelect, accessibilityStateClick, enabled: Boolean, onValueChange: ((Boolean) -> Unit)? = null)
```
A `Row` with `toggleable(role = Role.Checkbox)` and a 16 dp gap: a Material3 `Checkbox` (hidden from a11y, `onCheckedChange = null`) plus a label column.

`CheckboxStyleType` (sealed):
| Variant | Label |
|---|---|
| `Text(text, description)` | `text` in `body.medium`/`content.primary` and `description` in `body.small`/`content.secondary`. |
| `Hyperlink(text, hyperlinks: Map<String, HyperlinkButtonType>, hyperlinkButtonSizeType)` | A `HyperlinkButtonComponent`. `Url` links open in a **Custom Tab**, and `Action` links run their action. When **every** link has been clicked, the checkbox is automatically set to `true` (a "read the terms first" pattern). |
| `Custom(content: @Composable () -> Unit)` | Your content. |

`CheckboxStyle` (internal): on hover/focus the row gets `background.overlay` and a 1 dp `border.onAccent` border. The box: checked uses mark `content.primary` on `background.accent`,
unchecked uses border `border.active`, and disabled uses `content.disabled`/`background.disabled`/`border.disabled`.

> **Bug:** with `onValueChange = null`, the toggle handler evaluates `onValueChange?.invoke(it) ?: { state.change(value = it) }`, which **creates a lambda but never calls
> it**, so tapping does nothing. Always pass `onValueChange = { state.change(it) }` (or your handler).

### ChipComponent
```kotlin
@Composable
fun ChipComponent(modifier, state: SelectFieldState, chipStyleType: ChipStyleType,
    accessibilityStateSelect, accessibilityStateClick, enabled: Boolean, onValueChange: ((Boolean) -> Unit)? = null)
```
A Material3 `FilterChip` (always `enabled = true` internally). A click toggles the value. With `onValueChange == null` it calls `state.change(!value)` (this default **works**).
`ChipStyleType`: `Text(text)` or `Custom(content)`.

`ChipStyle` (internal):
| State | Background | Content | Border (1 dp) |
|---|---|---|---|
| selected & enabled | `background.accent` | `content.primary` | `border.standard` (or `border.onAccent` when hovered/focused) |
| selected & disabled | `background.accentLow` | `content.disabled` | `border.disabled` |
| unselected, hovered/focused | `background.overlay` | `content.onAccent` | `border.onAccent` |
| unselected & disabled | `background.disabled` | `content.disabled` | `border.disabled` |
| unselected | `background.base` | `content.primary` | none |

### ToggleComponent
```kotlin
@Composable
fun ToggleComponent(modifier, state: SelectFieldState, toggleStyleType: ToggleStyleType,
    accessibilityStateSelect, accessibilityStateClick, enabled: Boolean, onValueChange: ((Boolean) -> Unit)? = null)
```
A `Row` (`toggleable(role = Role.Switch)`, `SpaceBetween`) with a label column and a Material3 `Switch` (hidden from a11y). `ToggleStyleType`: `Text(text, description)` or `Custom(content)`.
`ToggleStyle` (internal): on hover/focus `background.overlay` with a `border.onAccent` border. The switch: on uses thumb `content.primary`, track `background.accent`, border `border.standard`;
off uses thumb `content.accent`, track `background.surface`, border `border.standard`; disabled uses `content.disabled`/`background.disabled`/`border.disabled`.

> **Bug:** same as Checkbox. With `onValueChange = null` the toggle never changes. Pass `onValueChange = { state.change(it) }`.

---

## Multi-select groups

```kotlin
@Composable fun MultiCheckboxComponent(modifier, state: MultiSelectFieldState<SelectFieldState>, items: Map<SelectFieldState, CheckboxStyleType>,
    accessibilityStateSelect, accessibilityStateClick, columnCount: Int, enabled: Boolean)
@Composable fun MultiChipComponent(  /* same */ items: Map<SelectFieldState, ChipStyleType>,   /* ... */)
@Composable fun MultiToggleComponent(/* same */ items: Map<SelectFieldState, ToggleStyleType>, /* ... */)   // toggles fillMaxWidth
```
- Items are laid out with [`NonLazyGridComponent`](components-layout.md) (`columns = columnCount`, 8 dp spacing). Each item is the single component bound to its own `SelectFieldState`.
- **Limit** = the first `Validators.MaxSize` in `state.validators`, or unlimited.
  - `limit == 1`: radio behaviour. Selecting an item resets every currently selected item to `false`, then the group value becomes `[item]` (or `[]` if deselected).
  - otherwise: an item can be selected while `selected.size < limit`. Unselected items become **disabled** once the limit is reached, with the disabled reason
    `R.string.accessibility_multiselect_limit_reached` ("Maximum limit of %1$d items reached").
  - Each change calls `selectableState.change(result)` and toggles membership in the group with `state.change(item)` (the `@JvmName("onItemSelected")` overload).
- The group's `contentDescription` summarizes the selection: "N items selected" (no limit), "One item selected"/"Select one item" (limit 1), or "Selected N of M items".
- These groups pass `onValueChange` explicitly, so the Checkbox/Toggle default-handler bug **doesn't** affect them.

```kotlin
// ViewModel
val interests = listOf("Sports", "Music", "Travel").associateWith { SelectFieldState(initial = false, validators = emptyList()) }
val interestsState = MultiSelectFieldState<SelectFieldState>(
    initial = emptyList(),
    validators = listOf(
        Validators.Required("Pick at least one"),
        Validators.MaxSize(2) { "Max 2" },
    ),
)

// UI
MultiChipComponent(
    modifier = Modifier.fillMaxWidth(),
    state = viewModel.interestsState,
    items = viewModel.interests.entries.associate { (label, s) -> s to ChipStyleType.Text(label) },
    accessibilityStateSelect = AccessibilityState.Select(selected = "Selected", notSelected = "Not selected"),
    accessibilityStateClick = AccessibilityState.Click(disabledReason = ""),
    columnCount = 3,
    enabled = true,
)
```

## Usage example: text fields

```kotlin
// ViewModel
val email = TextFieldState(
    initial = null,
    transform = { it.trim() },                                   // Transform<String> is required (fun interface)
    validators = listOf(Validators.Required("Required"), Validators.Email("Invalid email")),
)

// UI
TextFieldComponent(
    modifier = Modifier.fillMaxWidth(),
    state = viewModel.email,
    placeholder = "Email",
    helpText = "We'll never share it",
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
    enabled = true,
)
```

## Extension guide

- **New input type:** wrap `TextFieldComponent` like `PasswordFieldComponent`/`AmountFieldComponent` do (a custom `visualTransformation`, `keyboardOptions`,
  `onValueChange` filter), so error, help and colour behaviour stays consistent.
- **New select look:** add a variant to `CheckboxStyleType`/`ChipStyleType`/`ToggleStyleType` and a branch in the component's label `when`.
- **Fixing the known bugs:** replace `onValueChange?.invoke(it) ?: { state.change(it) }` with
  `onValueChange?.invoke(it) ?: state.change(it)` in Checkbox and Toggle, and `remember` the date fields' inner `TextFieldState`, propagating `state.hasError`/`errorMessage`.

## Related pages

- [Forms: state, validation and transforms](forms.md)
- [Lists (`NonLazyGridComponent`)](components-layout.md)
- [Buttons and hyperlinks](components-actions.md)
- [Design tokens](theme-tokens.md)
- [UI utilities](ui-utils.md)
