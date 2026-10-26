---
type: component reference
title: Theme and CompositionLocals
description: Reference for core/presentation's Theme composable. Covers how it provides LocalColors, LocalPalette, LocalDimensions and LocalTypographies; how it resolves the light/dark palette and system contrast (API 34+ listener); its override of the font scale to 1; the Inter downloadable font family; and how components consume the tokens.
tags: [core-presentation, theme, design-system, compose, composition-local, accessibility, fonts]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
sources:
  - id: openwiki-source-c42a9cf0d5d09701692a32e8
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/Theme.kt
  - id: openwiki-source-94c0f2afc239dc880998cc09
    resource: repo://core/presentation/src/main/java/com/core/presentation/ui/theme/typographies/base/BaseTypography.kt
  - id: openwiki-source-47f84f74b71ccd3a3536915b
    resource: repo://core/presentation/src/main/res/font/inter.xml
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
---

# Theme and CompositionLocals (`ui/theme/Theme.kt`)

## Purpose and when to use it

`Theme { ... }` is the design-system root. **Every core/presentation component must be inside it**, because they read the theme `CompositionLocal`s, which have
no defaults and `error(...)` when absent. `BaseActivity` already wraps the whole `NavDisplay` in `Theme`, so screens rendered by Navigation3 entries are covered.
Wrap explicitly in `@Preview`s, tests, and any separate composition (for example `ComposeView` in a dialog window that isn't part of the main tree).

The theme is **not** `MaterialTheme`. Material3 components used internally get explicit colours from the palette. `MaterialTheme.colorScheme` keeps Material defaults,
so avoid relying on it in feature code.

## API

```kotlin
val LocalColors       = staticCompositionLocalOf<Colors>       { error("CompositionLocal LocalColors not present") }
val LocalPalette      = staticCompositionLocalOf<ColorsPalette> { error("CompositionLocal LocalPalette not present") }
val LocalDimensions   = staticCompositionLocalOf<Dimensions>   { error("CompositionLocal LocalDimensions not present") }
val LocalTypographies = staticCompositionLocalOf<Typographies> { error("CompositionLocal LocalTypographies not present") }

@Composable
fun Theme(content: @Composable () -> Unit)
```

`Theme` does:
1. `isDark = isSystemInDarkTheme()`.
2. `contrastLevel = rememberSystemContrast()` (private): on **API 34+** it reads `UiModeManager.contrast` and registers a `ContrastChangeListener` (on the main executor,
   removed in `onDispose`), so contrast changes recompose live. Below API 34 it's always `0f`.
3. `dimensions = remember { Dimensions() }` and `colors = remember { Colors() }`.
4. `palette = remember(isDark, contrastLevel) { resolvePalette(isDark, contrastLevel) }`:

   | | contrast ≥ 1.0 | contrast ≥ 0.5 | otherwise |
   |---|---|---|---|
   | dark | `DarkColorsPalette.HighContrast` | `DarkColorsPalette.MediumContrast` | `DarkColorsPalette.DefaultContrast` |
   | light | `LightsColorsPalette.HighContrast` | `LightsColorsPalette.MediumContrast` | `LightsColorsPalette.DefaultContrast` |
5. `typographies = remember(contrastLevel) { Typographies(dimensions.sp, dimensions.em, contrastLevel) }`.
6. `CompositionLocalProvider(LocalPalette, LocalColors, LocalDimensions, LocalTypographies, LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) { content() }`.

Token values are in [theme-tokens](theme-tokens.md).

## Reading tokens

```kotlin
@Composable
fun SectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(vertical = LocalDimensions.current.dp.space.space8),
        text = text,
        color = LocalPalette.current.contentPalette.primary,
        style = LocalTypographies.current.title.small,
    )
}
```
Components resolve styles in internal `remember*Style(...)` functions keyed on `LocalPalette.current` and `LocalDimensions.current`, so they react to theme and contrast changes.

## Fonts

`BaseTypography` builds one `FontFamily` from six `Font(resId, weight)` entries: `R.font.inter` (Normal), `inter_medium` (Medium), `inter_semibold` (SemiBold), `inter_bold` (Bold),
`inter_extrabold` (ExtraBold) and `inter_black` (Black). The `res/font/*.xml` files are **downloadable fonts** from the Google Fonts provider
(`fontProviderAuthority = com.google.android.gms.fonts`, `fontProviderPackage = com.google.android.gms`, query `Inter`, `Inter:wght500`, …,
certificates `@array/com_google_android_gms_fonts_certs` in `values/font_certs.xml`). `values/preloaded_fonts.xml` defines the `preloaded_fonts` array, but no manifest
meta-data references it in this module. The fonts are fetched through Google Play Services at runtime. Behaviour on devices without Play Services (or offline on
first use) hasn't been verified here. Expect a fallback to the system font or a load failure.

## Gotchas and constraints

- **System font scaling is disabled.** `LocalDensity` is overridden with `fontScale = 1f`, so all `sp` text ignores the user's font-size setting (an accessibility
  regression, WCAG 1.4.4). Remove the override or clamp it (for example `fontScale.coerceAtMost(1.3f)`) if large text must be supported.
- Light/dark follows the system only. There's no app-level override parameter (`BaseActivity` also sets `MODE_NIGHT_FOLLOW_SYSTEM`).
- Contrast-aware palettes and typographies only activate on API 34+.
- `staticCompositionLocalOf`: changing a provided value recomposes the whole subtree. That's fine because the values change only on theme or contrast changes.

## Extension guide

- **Add a new token group** (for example elevation): create the type, add it to `Dimensions` (or a new `CompositionLocal`), and provide it in `Theme`.
- **Add an app-controlled dark mode:** add a parameter `Theme(isDark: Boolean = isSystemInDarkTheme(), content)` and pass it through to `resolvePalette`.

## Related pages

- [Design tokens](theme-tokens.md)
- [Buttons, alerts](components-actions.md)
- [CoreApp and BaseActivity](app-and-activity.md)
- [Overview (resources)](overview.md#manifest-and-resources)
