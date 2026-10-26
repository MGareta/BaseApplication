---
type: design tokens
title: Design tokens reference
description: Complete value tables for core/presentation's design tokens. Covers the seven color families (10–100 shades), the background, border and content palette roles for light and dark themes at default, medium and high contrast, dp/em/sp dimensions, and the body, display, label and title typography styles at each contrast level.
tags: [core-presentation, design-system, tokens, colors, palette, typography, dimensions]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:11:01.468Z
sources:
  - id: openwiki-source-084aa007f2dc01113065a8d9
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/colors/Colors.kt
  - id: openwiki-source-d73a36ab3f48b3c44d0a1633
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/colors/gray/GrayColors.kt
  - id: openwiki-source-18c2f4c28906b3ffcddd54a2
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/colors/green/GreenColors.kt
  - id: openwiki-source-953b10fe019498977830ae46
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/dp/space/SpaceDimensions.kt
  - id: openwiki-source-22adc76f24172bc1dcef901c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/dp/target/TargetDimensions.kt
  - id: openwiki-source-e60f51738531fadfaf0055a7
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/em/EmDimensions.kt
  - id: openwiki-source-c6bcc643716e9d1ea58ec174
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/sp/body/BodyDimensions.kt
  - id: openwiki-source-05be52a7ee154389851f65a1
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/sp/display/DisplayDimensions.kt
  - id: openwiki-source-af48a0c776422ed7d5e7a916
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/sp/label/LabelDimensions.kt
  - id: openwiki-source-1bc33a8a49b673bf0bb957af
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/sp/SpDimensions.kt
  - id: openwiki-source-1e148abaad484ea497c943a4
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/dimensions/sp/title/TitleDimensions.kt
  - id: openwiki-source-c955e599c4748a54261a3506
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/background/DarkBackgroundPalette.kt
  - id: openwiki-source-32aac9ace42e3821685a2dca
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/background/LightBackgroundPalette.kt
  - id: openwiki-source-5af95aa9df88b96139ec93a5
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/border/DarkBorderPalette.kt
  - id: openwiki-source-553e69b7dbf6559d5ac0545a
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/border/LightBorderPalette.kt
  - id: openwiki-source-bfa384872811da5e2e36ed8f
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/ColorsPalette.kt
  - id: openwiki-source-1bfa18c29ea74f9ea6fdf781
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/content/DarkContentPalette.kt
  - id: openwiki-source-0bdae297755479eeb6c81412
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/palette/content/LightContentPalette.kt
  - id: openwiki-source-94c0f2afc239dc880998cc09
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/base/BaseTypography.kt
  - id: openwiki-source-c9f53596ebef75359d5ce5ac
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/body/BodyTypographies.kt
  - id: openwiki-source-7d00c552e0e1398bc3253723
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/display/DisplayTypographies.kt
  - id: openwiki-source-fe3362aa476e563226fffe5c
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/label/LabelTypographies.kt
  - id: openwiki-source-db96337e9bc65d9f3b7b2b87
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/title/TitleTypographies.kt
  - id: openwiki-source-de680a649bd6ac118a244e3a
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/Typographies.kt
generated: { by: "claude-code", at: "2026-10-26T16:11:01.468Z" }
---

# Design tokens reference

Access the tokens inside [`Theme`](theme.md):

```kotlin
LocalColors.current.green.green50                 // raw color families (rarely needed)
LocalPalette.current.backgroundPalette.surface    // semantic roles (preferred)
LocalPalette.current.contentPalette.primary
LocalPalette.current.borderPalette.active
LocalDimensions.current.dp.space.space16          // dp: border / radius / space / target
LocalDimensions.current.sp.body.body16            // sp sizes (prefer typographies)
LocalDimensions.current.em.relaxed                // line-height multipliers
LocalTypographies.current.title.medium            // TextStyle
```

**Prefer palette roles over raw colors** so light/dark and contrast variants work automatically.

## Color families (`ui/theme/colors`)

`@Immutable data class Colors(blue, gray, green, orange, red, turquoise, yellow, white = Color.White, black = Color.Black)`. Each family is a `data object` with shades 10–100.

| Shade | Blue | Gray | Green | Orange | Red | Turquoise | Yellow |
|---|---|---|---|---|---|---|---|
| 10 | `#E6F2FA` | `#F8F9FA` | `#F2F9E5` | `#FFF3E6` | `#FBEAEA` | `#E0F7F6` | `#FFF9E1` |
| 20 | `#B3D9F1` | `#E9ECEF` | `#D0E6A5` | `#FFDCB3` | `#F3C2C2` | `#B2EBF2` | `#FFEFB2` |
| 30 | `#80BFE8` | `#DEE2E6` | `#B0D36D` | `#FFC480` | `#EB9999` | `#80DEEA` | `#FFE583` |
| 40 | `#4DA6DE` | `#CED4DA` | `#91C13D` | `#FFAC4D` | `#E37171` | `#26C6DA` | `#FFDB54` |
| 50 | `#0076CE` | `#ADB5BD` | `#86BC24` | `#ED6C02` | `#D32F2F` | `#00A3A1` | `#FFD100` |
| 60 | `#005EAA` | `#6C757D` | `#6A951C` | `#BD5602` | `#A92626` | `#00838F` | `#CCA700` |
| 70 | `#004780` | `#495057` | `#517215` | `#8E4101` | `#7F1C1C` | `#006064` | `#997D00` |
| 80 | `#002F55` | `#343A40` | `#384F0E` | `#5F2B01` | `#541313` | `#004D4C` | `#665400` |
| 90 | `#00182B` | `#212529` | `#212D08` | `#2F1600` | `#2A0909` | `#002B2C` | `#332A00` |
| 100 | `#000C15` | `#121212` | `#0A0E02` | `#180B00` | `#150505` | `#001414` | `#1A1500` |

Property names follow `<family><shade>`, for example `BlueColors.blue50`, `GrayColors.gray100`.

## Palettes (`ui/theme/palette`)

`interface ColorsPalette { backgroundPalette: BaseBackgroundPalette; borderPalette: BaseBorderPalette; contentPalette: BaseContentPalette }`, implemented by
`sealed class LightsColorsPalette` and `sealed class DarkColorsPalette`, each with `data object DefaultContrast`, `MediumContrast` and `HighContrast`, built from the matching
`Light*/Dark*{Background,Border,Content}Palette` sealed classes. `Theme` picks one by dark mode and system contrast (≥ 1.0 high, ≥ 0.5 medium, else default).

### Background roles (`BaseBackgroundPalette`)

| Role | Light default | Light medium | Light high | Dark default | Dark medium | Dark high |
|---|---|---|---|---|---|---|
| `base` | White | White | White | gray100 | gray100 | Black |
| `surface` | gray10 | White | White | gray90 | gray100 | Black |
| `accent` | green50 | green60 | green80 | green50 | green40 | green20 |
| `accentLow` | green10 | green10 | green10 | green90 | green90 | Black |
| `successLow` | green10 | green10 | green10 | green90 | green90 | green90 |
| `errorLow` | red10 | red10 | red10 | red90 | red90 | red90 |
| `warningLow` | yellow10 | yellow10 | yellow10 | yellow90 | yellow90 | yellow90 |
| `infoLow` | blue10 | blue10 | blue10 | blue90 | blue90 | blue90 |
| `overlay` | Black 35% | Black 35% | Black 35% | Black 60% | Black 60% | Black 60% |
| `disabled` | gray20 | gray30 | gray40 | gray90 | gray80 | gray70 |

### Border roles (`BaseBorderPalette`)

| Role | Light default | Light medium | Light high | Dark default | Dark medium | Dark high |
|---|---|---|---|---|---|---|
| `subtle` | gray20 | gray40 | Black | gray90 | gray70 | White |
| `standard` | gray40 | gray60 | Black | gray70 | gray40 | White |
| `onAccent` | orange50 | orange70 | orange90 | orange50 | orange30 | orange20 |
| `active` | green50 | green70 | green90 | green50 | green30 | green20 |
| `error` | red50 | red50 | red90 | red40 | red40 | red20 |
| `disabled` | gray20 | gray20 | gray20 | gray80 | gray80 | gray80 |

`onAccent` (orange) is used as the **focus/hover ring** colour across components.

### Content roles (`BaseContentPalette`)

| Role | Light default | Light medium | Light high | Dark default | Dark medium | Dark high |
|---|---|---|---|---|---|---|
| `primary` | gray100 | gray100 | Black | gray10 | gray10 | White |
| `secondary` | gray70 | gray90 | Black | gray40 | gray20 | White |
| `link` | blue50 | blue70 | blue100 | blue40 | blue30 | blue10 |
| `accent` | green50 | green60 | green80 | green50 | green40 | green20 |
| `disabled` | gray40 | gray40 | gray40 | gray60 | gray60 | gray60 |
| `onAccent` | Black | Black | **White** | Black | Black | Black |
| `success` | green80 | green90 | green100 | green30 | green20 | green10 |
| `error` | red80 | red90 | red100 | red30 | red20 | red10 |
| `warning` | yellow90 | yellow90 | yellow90 | yellow30 | yellow30 | yellow30 |
| `info` | blue80 | blue80 | blue80 | blue30 | blue30 | blue30 |

## Dimensions (`ui/theme/dimensions`)

`@Immutable data class Dimensions(dp: DpDimensions = DpDimensions(), em: EmDimensions = EmDimensions(), sp: SpDimensions = SpDimensions())`.
`DpDimensions(border, radius, space, target)` and `SpDimensions(body, display, label, title)` hold `data object`s.

| Group | Tokens |
|---|---|
| `dp.border` (`BorderDimensions`) | `border0` 0 dp, `border1` 1, `border2` 2, `border3` 3, `border8` 8 |
| `dp.radius` (`RadiusDimensions`) | `radius4` 4 dp, `radius8` 8, `radius16` 16 |
| `dp.space` (`SpaceDimensions`) | `space0` 0, `space2` 2, `space4` 4, `space8` 8, `space12` 12, `space14` 14, `space16` 16, `space24` 24, `space32` 32, `space48` 48, `space64` 64, `space80` 80, `space96` 96 (dp) |
| `dp.target` (`TargetDimensions`, touch targets) | `target24` 24 dp, `target40` 40, `target48` 48, `target56` 56 |
| `em` (`EmDimensions`, line-height multipliers) | `tight` 1.2em, `normal` 1.4em, `relaxed` 1.5em |
| `sp.body` | `body14` 14 sp, `body16` 16, `body18` 18 |
| `sp.display` | `display32` 32 sp, `display40` 40, `display48` 48 |
| `sp.label` | `label12` 12 sp, `label14` 14, `label16` 16 |
| `sp.title` | `title20` 20 sp, `title24` 24, `title28` 28 |

## Typography (`ui/theme/typographies`)

`@Immutable class Typographies(spDimensions, emDimensions, contrastLevel)` exposes `body`, `display`, `label` and `title`, each with `small`, `medium` and `large` `TextStyle`s built by
`BaseTypography.buildStyle(fontWeight, fontSize, lineHeight, letterSpacing)`: Inter family, `lineHeight = fontSize × em multiplier`, `includeFontPadding = true`.

| Style | Size (sp) | Line height | Weight (default / medium / high contrast) | Letter spacing (contrast < 1 / ≥ 1) |
|---|---|---|---|---|
| `body.small` | 14 | ×1.5 (relaxed) | Normal / Normal / Medium | 0.1 / 0.2 sp |
| `body.medium` | 16 | ×1.5 | Normal / Normal / Medium | 0.2 / 0.3 sp |
| `body.large` | 18 | ×1.5 | Normal / Normal / Medium | 0.2 / 0.3 sp |
| `display.small` | 32 | ×1.2 (tight) | Bold / Bold / Black | −0.2 / −0.1 sp |
| `display.medium` | 40 | ×1.2 | Bold / Bold / Black | −0.5 / −0.3 sp |
| `display.large` | 48 | ×1.2 | Bold / Bold / Black | −1 / −0.8 sp |
| `label.small` | 12 | ×1.2 | Medium / Medium / Bold | 0.4 / 0.5 sp |
| `label.medium` | 14 | ×1.2 | Medium / Medium / Bold | 0.2 / 0.3 sp |
| `label.large` | 16 | ×1.2 | Medium / Medium / Bold | 0.1 / 0.2 sp |
| `title.small` | 20 | ×1.4 (normal) | SemiBold / **Bold** / ExtraBold | 0 / 0.1 sp |
| `title.medium` | 24 | ×1.4 | SemiBold / Bold / ExtraBold | −0.1 / 0 sp |
| `title.large` | 28 | ×1.4 | SemiBold / Bold / ExtraBold | −0.2 / −0.1 sp |

Only `title` distinguishes medium contrast (≥ 0.5). The others switch only at high contrast (≥ 1.0).

## Source file map

All paths are under `core/presentation/src/main/java/com/core/presentation/ui/theme/`.

| File | Type | Content |
|---|---|---|
| `colors/Colors.kt` | `@Immutable data class Colors` | Aggregates the families plus `white`/`black`. |
| `colors/blue/BlueColors.kt` | `data object BlueColors` | `blue10`…`blue100` |
| `colors/gray/GrayColors.kt` | `data object GrayColors` | `gray10`…`gray100` |
| `colors/green/GreenColors.kt` | `data object GreenColors` | `green10`…`green100` (explicitly typed `Color`) |
| `colors/orange/OrangeColors.kt` | `data object OrangeColors` | `orange10`…`orange100` |
| `colors/red/RedColors.kt` | `data object RedColors` | `red10`…`red100` |
| `colors/turquoise/TurquoiseColors.kt` | `data object TurquoiseColors` | `turquoise10`…`turquoise100` |
| `colors/yellow/YellowColors.kt` | `data object YellowColors` | `yellow10`…`yellow100` |
| `palette/ColorsPalette.kt` | `interface ColorsPalette`, `sealed class LightsColorsPalette`, `sealed class DarkColorsPalette` | Palette composition × 3 contrasts |
| `palette/background/base/BaseBackgroundPalette.kt` | `@Immutable interface BaseBackgroundPalette` | Background roles |
| `palette/background/LightBackgroundPalette.kt` | `sealed class LightBackgroundPalette` (`DefaultContrast`, `MediumContrast`, `HighContrast`) | Light background values |
| `palette/background/DarkBackgroundPalette.kt` | `sealed class DarkBackgroundPalette` (same objects) | Dark background values |
| `palette/border/base/BaseBorderPalette.kt` | `@Immutable interface BaseBorderPalette` | Border roles |
| `palette/border/LightBorderPalette.kt` | `sealed class LightBorderPalette` | Light border values |
| `palette/border/DarkBorderPalette.kt` | `sealed class DarkBorderPalette` | Dark border values |
| `palette/content/base/BaseContentPalette.kt` | `@Immutable interface BaseContentPalette` | Content roles |
| `palette/content/LightContentPalette.kt` | `sealed class LightContentPalette` | Light content values |
| `palette/content/DarkContentPalette.kt` | `sealed class DarkContentPalette` | Dark content values |
| `dimensions/Dimensions.kt` | `@Immutable data class Dimensions(dp, em, sp)` | Root |
| `dimensions/dp/DpDimensions.kt` | `@Immutable data class DpDimensions(border, radius, space, target)` | |
| `dimensions/dp/border/BorderDimensions.kt` | `data object BorderDimensions` | `border0`…`border8` |
| `dimensions/dp/radius/RadiusDimensions.kt` | `data object RadiusDimensions` | `radius4/8/16` |
| `dimensions/dp/space/SpaceDimensions.kt` | `data object SpaceDimensions` | `space0`…`space96` |
| `dimensions/dp/target/TargetDimensions.kt` | `data object TargetDimensions` | `target24/40/48/56` |
| `dimensions/em/EmDimensions.kt` | `data class EmDimensions(tight, normal, relaxed)` | Line-height multipliers |
| `dimensions/sp/SpDimensions.kt` | `data class SpDimensions(body, display, label, title)` | |
| `dimensions/sp/body/BodyDimensions.kt` | `data object BodyDimensions` | `body14/16/18` |
| `dimensions/sp/display/DisplayDimensions.kt` | `data object DisplayDimensions` | `display32/40/48` |
| `dimensions/sp/label/LabelDimensions.kt` | `data object LabelDimensions` | `label12/14/16` |
| `dimensions/sp/title/TitleDimensions.kt` | `data object TitleDimensions` | `title20/24/28` |
| `typographies/Typographies.kt` | `@Immutable class Typographies(spDimensions, emDimensions, contrastLevel)` | `body`, `display`, `label`, `title` |
| `typographies/base/BaseTypography.kt` | `@Immutable abstract class BaseTypography` | Inter `FontFamily`, `buildStyle(...)` |
| `typographies/body/BodyTypographies.kt` | `class BodyTypographies : BaseTypography` | `small`/`medium`/`large` |
| `typographies/display/DisplayTypographies.kt` | `class DisplayTypographies : BaseTypography` | `small`/`medium`/`large` |
| `typographies/label/LabelTypographies.kt` | `class LabelTypographies : BaseTypography` | `small`/`medium`/`large` |
| `typographies/title/TitleTypographies.kt` | `class TitleTypographies : BaseTypography` | `small`/`medium`/`large` |

## Extension guide

- **New colour shade or family:** add it to the `data object` (or a new family object plus a property in `Colors`).
- **New palette role:** add the property to the `Base*Palette` interface, then to the constructor and **all six** `data object`s (light/dark × 3 contrasts). Compilation enforces this.
- **New dimension:** add it to the relevant `data object`. **New text style:** add a `val` built with `buildStyle(...)` to the typography class. Follow the contrast-dependent weight and spacing pattern.

## Related pages

- [Theme](theme.md)
- [Components](components-actions.md)
- [Form fields](form-fields.md)
