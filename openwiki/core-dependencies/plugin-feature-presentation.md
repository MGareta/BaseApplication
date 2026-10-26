---
type: build reference
title: "Plugin: feature.presentation"
description: Reference for FeaturePresentationPlugin (plugin id feature.presentation), the convention plugin for feature UI modules such as :demo:presentation. Covers the plugins it applies (Android library, Compose, serialization, KSP, Hilt, Detekt), Compose enablement, its dependencies (implementation core:presentation, Hilt, core:presentation test fixtures), the core/data visibility gap, and a complete module example.
tags: [core-dependencies, gradle, convention-plugin, feature-module, compose, presentation]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-823d791cada9047e35d0e367
    resource: repo://demo/presentation/build.gradle.kts
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Plugin: `feature.presentation` (`com.core.dependencies.plugins.FeaturePresentationPlugin`)

## Purpose

Use it for a feature's **UI module**: screens, contracts, ViewModels, Navigation3 destinations, entries and graphs, and their Hilt navigation module (see
[core-presentation recipes](../core-presentation/recipes.md)).

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
demo/presentation/build.gradle.kts
```

## What `apply(target)` does

1. **Plugins:** `com.android.library`, `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization` (for `@Serializable` destinations),
   `com.google.devtools.ksp`, `com.google.dagger.hilt.android`, `dev.detekt`.
2. **`LibraryExtension`:** `defaultConfiguration` (namespace default `com.base.application`), `configureBuildTypes`, `configurePackaging`, `buildFeatures { compose = true }`.
   No `configureTests()`.
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Dependency |
|---|---|
| `implementation` | `project(":core:presentation")`, which exposes Compose, Material3, Navigation3, Paging Compose, lifecycle-viewmodel-compose, hilt-navigation-compose, browser and serialization via `api` |
| `ksp` | `kotlin-metadata-jvm`, `hilt-android-compiler` |
| `implementation` | `hilt-android` |
| `testImplementation` | `testFixtures(project(":core:presentation"))`, which gives the test-unit bundle |
| `detektPlugins` | `detekt-rules-ktlint-wrapper`, `detekt-rules-libraries` |

## Example module

```kotlin
// orders/presentation/build.gradle.kts
plugins {
    alias(coreLibs.plugins.feature.presentation)
}

android {
    namespace = "com.orders.presentation"
}

dependencies {
    implementation(project(":orders:data"))   // needed to see ApiResult / core-data types and the feature's repositories
}
```
Register it in `settings.gradle.kts` and add `implementation(project(":orders:presentation"))` to `app/build.gradle.kts`.

## Gotchas

- **No core/data on the classpath.** `ApiResult`, `ISessionManager`, etc. aren't visible unless you add the feature data module (or core:data). `BaseViewModel`'s use-case helpers
  use `ApiResult`, so most ViewModels need it ([dependency-visibility](dependency-visibility.md#gaps-and-fixes)).
- `:demo:presentation` sets no namespace, so it collides with `:app`.
- No `kspTest`, no Robolectric config and no Compose UI test artifacts ([core-presentation testing](../core-presentation/testing.md)).
- The Compose BoM isn't applied in this module. Versionless Compose artifacts rely on transitive versions (see [plugin-core-presentation](plugin-core-presentation.md#gotchas)).

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Recipes](recipes.md)
- [core-presentation recipes](../core-presentation/recipes.md)
- [Plugin: feature.data](plugin-feature-data.md)
