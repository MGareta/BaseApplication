---
type: component reference
title: "Components: buttons, badge, alert, amount"
description: Reference for core/presentation's action and feedback components. Covers BlockButtonComponent, HyperlinkButtonComponent, IconButtonComponent, BadgeComponent, AlertComponent and AmountComponent, with every parameter, every style/size/family variant and the theme tokens each maps to, plus accessibility behaviour and known issues.
tags: [core-presentation, compose, components, buttons, alert, badge, amount, design-system]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-119b18e790c339454c1153f0
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/alert/AlertComponent.kt
  - id: openwiki-source-ad4d9eb1e1326d344b968267
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/alert/model/AlertStyle.kt
  - id: openwiki-source-1b30e3657a0ef2304252b44f
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/amount/AmountComponent.kt
  - id: openwiki-source-f8fb6ac3cb4a38bedfdd825b
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/badge/model/BadgeSize.kt
  - id: openwiki-source-0a43ff430363c2f805422aee
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/badge/model/BadgeStyle.kt
  - id: openwiki-source-0e25faed01e9f6f8edc5f998
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/button/block/BlockButtonComponent.kt
  - id: openwiki-source-21cee3b568647480a85dcfd2
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/button/block/model/BlockButtonSize.kt
  - id: openwiki-source-84984a164d47b964cc075c06
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/button/block/model/BlockButtonStyle.kt
  - id: openwiki-source-cf964016ec19c79096ac02fe
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/button/hyperlink/HyperlinkButtonComponent.kt
  - id: openwiki-source-b66b32d5dbd539e1e1135550
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/components/button/hyperlink/model/HyperlinkButtonType.kt
  - id: openwiki-source-6ca0c45addf0b6ca6953d9b1
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/AccessibilityUtils.kt
  - id: openwiki-source-9c083d2620d164d5dad2dd79
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/utils/Extensions.kt
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Components: buttons, badge, alert, amount

All components require being inside [`Theme`](theme.md) (they read `LocalPalette`, `LocalDimensions` and `LocalTypographies`).
None of the parameters have defaults except where noted, so you must pass `modifier` explicitly (usually `Modifier`).
Styles and sizes are **sealed interfaces** whose `Custom` variant lets you override tokens.

## Package layout

```
ui/components/
├── button/
│   ├── block/{BlockButtonComponent.kt, model/BlockButtonSize.kt, model/BlockButtonStyle.kt}
│   ├── hyperlink/{HyperlinkButtonComponent.kt, model/HyperlinkButtonSize.kt, model/HyperlinkButtonType.kt}
│   └── icon/{IconButtonComponent.kt, model/IconButtonSize.kt, model/IconButtonStyle.kt}
├── badge/{BadgeComponent.kt, model/BadgeSize.kt, model/BadgeStyle.kt}
├── alert/{AlertComponent.kt, model/AlertFamily.kt, model/AlertStyle.kt}
└── amount/{AmountComponent.kt, model/AmountSize.kt}
```
`CurrencyType` (used by Amount) lives in `form/fields/input/amount/model/CurrencyType.kt`, and `AccessibilityState` in `ui/utils/AccessibilityUtils.kt`.

---

## BlockButtonComponent

```kotlin
@Composable
fun BlockButtonComponent(
    modifier: Modifier,
    blockButtonStyleType: BlockButtonStyleType,
    blockButtonSizeType: BlockButtonSizeType,
    text: String,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    accessibilityState: AccessibilityState.Click,   // disabledReason announced when !enabled
    onClick: () -> Unit,
)
```
It's built on Material3 `OutlinedButton` with `elevation = null`. Layout: a fixed square slot (side = text font size in dp) for the leading icon,
the centred text with `weight(1f)` and horizontal padding `space`, then a square slot for the trailing icon. The empty slots are kept even when the icon is `null`, which keeps the text centred.
The modifier chain is `modifier.then(Modifier.size(size)).width(IntrinsicSize.Min)`.

**Disabled handling:** the underlying button is always `enabled = true` (so it stays focusable for TalkBack). Clicks are ignored when `enabled == false`, and
semantics add `stateDescription = disabledReason` + `disabled()`.

`BlockButtonSizeType` (sealed):

| Variant | Height | Horizontal padding (Tertiary) | Vertical padding | Text–icon space | Text style |
|---|---|---|---|---|---|
| `Small` | `target40` (40 dp) | 12 dp (0 dp) | 8 dp | 12 dp | `body.small` (14 sp) |
| `Medium` | `target48` (48 dp) | 16 dp (0 dp) | 12 dp | 16 dp | `body.medium` (16 sp) |
| `Large` | `target56` (56 dp) | 16 dp (0 dp) | 16 dp | 16 dp | `body.large` (18 sp) |
| `Custom(size: DpSize, paddingValues: PaddingValues, space: Dp, textStyle: TextStyle)` | custom | | | | |

Width is always `Dp.Unspecified` for the built-ins.

`BlockButtonStyleType` (sealed: `Primary`, `Secondary`, `Tertiary`). The state is resolved from the `interactionSource` (hovered/focused/pressed):

| Style | Default | Pressed | Hovered / Focused | Disabled |
|---|---|---|---|---|
| Primary | bg `background.accent`, content `content.onAccent`, no border | same | + 1 dp border `border.onAccent` | bg `background.disabled`, content `content.disabled` |
| Secondary | transparent, content `content.accent`, 1 dp border `border.active` | same as default | border `border.onAccent` | transparent, `content.disabled`, border `border.disabled` |
| Tertiary | transparent, `content.accent`, no border | same | 1 dp border `border.onAccent` | bg `background.disabled`, `content.disabled` |

## HyperlinkButtonComponent

```kotlin
@Composable
fun HyperlinkButtonComponent(
    modifier: Modifier,
    text: String,
    hyperlinks: Map<String, HyperlinkButtonType>,     // substring -> link action
    hyperlinkButtonSizeType: HyperlinkButtonSizeType,
    onClick: (HyperlinkButtonType) -> Unit,
)
```
It renders a `Text` with an `AnnotatedString`, colour `content.primary`. Every key of `hyperlinks` found in `text` as a **whole word** (regex
`\bkey\b`, longest keys first) becomes a `LinkAnnotation.Clickable` styled with: normal `content.link` + underline; focused/hovered `content.accent` on
`background.accentLow`; pressed `content.onAccent` on `background.accent`. Clicking calls `onClick(hyperlinks[match])`.
The annotated string is remembered by `(text, hyperlinks, palette)`.

`HyperlinkButtonType` (sealed): `Action(val action: () -> Unit)` and `Url(val url: java.net.URL)`. The component **doesn't open URLs or run
the action itself**. Handle both in `onClick`.

`HyperlinkButtonSizeType`: `Small` → `body.small`, `Medium` → `body.medium`, `Custom(textStyle)`.

Gotchas: keys that start or end with non-word characters (for example `"(terms)"`) never match because of `\b`. If the same key appears twice, both occurrences are links.

## IconButtonComponent

```kotlin
@Composable
fun IconButtonComponent(
    modifier: Modifier,
    iconButtonStyleType: IconButtonStyleType,
    iconButtonSizeType: IconButtonSizeType,
    @DrawableRes icon: Int,
    enabled: Boolean,
    accessibilityState: AccessibilityState.Click,
    contentDescription: String?,
    onClick: () -> Unit,
)
```
It's built on Material3 `OutlinedIconButton` (here `enabled` **is** passed through, unlike BlockButton). The icon fills the button minus the size padding.
Disabled semantics are the same as BlockButton.

`IconButtonSizeType`: `Small` 40×40 dp, padding 12 dp; `Medium` 48×48, padding 12; `Large` 56×56, padding 16; `Custom(size: DpSize, paddingValues: PaddingValues)`.
`IconButtonStyleType`: `Primary`, `Secondary`, `Tertiary`, with **the same colour/border matrix as BlockButton** (the files are near-duplicates).

## BadgeComponent

```kotlin
@Composable
fun BadgeComponent(
    modifier: Modifier,
    badgeStyleType: BadgeStyleType,
    badgeSizeType: BadgeSizeType,
    text: String?,                                   // null renders an empty dot
    accessibilityState: AccessibilityState.Description,
)
```
A pill `Text` with a `CircleShape` background and 4 dp horizontal padding. A custom `layout` makes width ≥ height (so short text is a circle).
Semantics `text` is replaced by `accessibilityState.description`.

| `BadgeStyleType` | Background | Content |
|---|---|---|
| `Alert` | `background.warningLow` | `content.warning` |
| `Inbox` | `background.accentLow` | `content.accent` |
| `Info` | `background.infoLow` | `content.info` |
| `Notification` | `background.accent` | `content.onAccent` |
| `Custom(backgroundColor, contentColor)` | custom | custom |

`BadgeSizeType`: `Small` → `label.small` (12 sp), `Medium` → `label.medium` (14 sp), `Large` → `label.large` (16 sp), `Custom(textStyle)`.

## AlertComponent

```kotlin
@Composable
fun AlertComponent(
    modifier: Modifier,
    alertStyleType: AlertStyleType,
    alertFamilyType: AlertFamilyType,
    title: String,
    text: String?,
    content: (@Composable () -> Unit)?,               // extra slot under title/text
)
```

`AlertFamilyType` (sealed) chooses the presentation:

| Family | Presentation |
|---|---|
| `TopTrigger(onDismiss: () -> Unit)` | A `Dialog` (`usePlatformDefaultWidth = false`) whose window gravity is set to **TOP**. A rounded (8 dp) row with icon, title (`title.small`), text (`body.small`, `content.primary`), the `content` slot, and a small tertiary close `IconButtonComponent` calling `onDismiss`. |
| `Message(trailingComponent: (@Composable () -> Unit)?)` | **Inline** (no dialog). A rounded row with a 1 dp border, icon, title (`title.small`), text (`body.medium`), the `content` slot, then `trailingComponent` at the end. |
| `Modal(onDismiss: () -> Unit)` | A `Dialog` with gravity **BOTTOM**. A header row with icon, title (`title.medium`) and a medium tertiary close button, then text (`body.medium`) and the `content` slot. |

`AlertStyleType` (sealed `data object`s):

| Style | Background | Title/icon colour | Border (Message only) |
|---|---|---|---|
| `Warning` | `background.warningLow` | `content.warning` | `border.error` |
| `Info` | `background.infoLow` | `content.info` | `border.error` |
| `Success` | `background.successLow` | `content.success` | `border.error` |
| `Error` | `background.errorLow` | `content.error` | `border.error` |

The body `text` always uses `content.primary`. Title and text groups use `semantics(mergeDescendants = true)`, and containers are traversal groups.

Known issues (verified in source):
- **All families use `R.drawable.ic_password_show` as the leading icon and `ic_password_hide` as the close icon**, instead of
  `ic_info`/`ic_warning`/`ic_success`/`ic_error`/`ic_close`, which exist in `res/drawable` but aren't referenced.
- **Every style uses `border.error`** for the Message border.
- The close button's `contentDescription` is the hard-coded `"Close"` (not `R.string.accessibility_close`).
- In `Modal`, the caller's `modifier` is applied to both the outer `Column` and the inner header `Row`.
- `Message.trailingComponent` is passed internally as a parameter named `leadingComponent` but is rendered at the **end**.

## AmountComponent

```kotlin
@Composable
fun AmountComponent(
    modifier: Modifier,
    amountSizeType: AmountSizeType,
    amount: BigDecimal?,                 // null renders "---,--" and the a11y text "Amount not available"
    fractionDigits: Int?,                // null -> 2 decimals; 0 -> no decimal part
    positivePrefix: String?,             // e.g. "+" for positive numbers
    currencyType: CurrencyType,
    locale: Locale = Locale.getDefault(),
)
```
Formatting uses internal `Number?.splitByDecimalSeparator(fractionDigits, positivePrefix, locale)` (`ui/utils/Extensions.kt`):
`NumberFormat.getInstance(locale)` with min = max fraction digits = `fractionDigits ?: 2` (the currency default doesn't apply because
`NumberFormat.getInstance` has no currency), `RoundingMode.DOWN` (truncates), and grouping per locale. It returns `(integerPart, decimalSeparator + decimals)`.
The integer part is drawn with the size's `integerStyle`, and the decimal part plus `" " + currency` with `decimalStyle`. When `fractionDigits` is `null` or `0`,
the currency follows the integer part instead. All text uses `content.primary`.

`AmountSizeType`: `Small` (integer `body.small` / decimals `label.small`), `Medium` (`body.medium` / `label.small`), `Large` (`body.large` / `body.small`),
`Custom(integerStyle, decimalStyle)`.

`CurrencyType` (sealed class with `val currency: String`): `Symbol(value: java.util.Currency)` → `value.symbol`; `Code(value: Currency)` → `value.currencyCode`;
`Custom(value: String)` → `value`.

Gotcha: with `fractionDigits = null`, the formatter still produces 2 decimals, but the decimal span only renders when `fractionDigits > 0`.
The output is therefore the integer part plus the currency, and **the decimals are silently dropped** (`AmountComponent.kt` lines 77–96).
Pass an explicit `fractionDigits` (for example `2`) to show cents.

## Usage examples

```kotlin
BlockButtonComponent(
    modifier = Modifier.fillMaxWidth(),
    blockButtonStyleType = BlockButtonStyleType.Primary,
    blockButtonSizeType = BlockButtonSizeType.Large,
    text = stringResource(R.string.continue_label),
    leadingIcon = null,
    trailingIcon = R.drawable.ic_arrow_right,
    enabled = state.isFormValid,
    accessibilityState = AccessibilityState.Click(disabledReason = "Complete all fields to continue"),
    onClick = { onEvent(Event.Submit) },
)

HyperlinkButtonComponent(
    modifier = Modifier,
    text = "I accept the terms and privacy policy",
    hyperlinks = mapOf(
        "terms" to HyperlinkButtonType.Url(URL("https://example.com/terms")),
        "privacy policy" to HyperlinkButtonType.Action { onEvent(Event.OpenPrivacy) },
    ),
    hyperlinkButtonSizeType = HyperlinkButtonSizeType.Small,
    onClick = { link ->
        when (link) {
            is HyperlinkButtonType.Url -> uriHandler.openUri(link.url.toString())
            is HyperlinkButtonType.Action -> link.action()
        }
    },
)

AlertComponent(
    modifier = Modifier.padding(16.dp),
    alertStyleType = AlertStyleType.Error,
    alertFamilyType = AlertFamilyType.Message(trailingComponent = null),
    title = "Payment failed",
    text = state.errorMessage,
    content = null,
)

AmountComponent(
    modifier = Modifier,
    amountSizeType = AmountSizeType.Large,
    amount = BigDecimal("1234.56"),
    fractionDigits = 2,
    positivePrefix = "+",
    currencyType = CurrencyType.Symbol(Currency.getInstance("EUR")),
)
```

## Extension guide

- **New style or size:** add a `data object` to the sealed `*StyleType`/`*SizeType` and a branch to the matching `remember*Style`/`remember*Size`
  (internal, keyed by the type, `palette`/`dimensions`/`typographies` and interaction states). Use palette roles, never raw colours.
- **One-off overrides:** prefer the `Custom` variants (Badge, BlockButton/IconButton size, Hyperlink size, Amount size).
- **New alert icon per style:** add an icon field to `AlertStyle` and use the existing `ic_info`/`ic_warning`/`ic_success`/`ic_error` drawables.

## Related pages

- [Theme](theme.md)
- [Design tokens](theme-tokens.md)
- [UI utilities (`AccessibilityState`, `BorderIndication`)](ui-utils.md)
- [Form fields (`CurrencyType`)](form-fields.md)
