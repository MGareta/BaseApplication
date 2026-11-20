---
type: build reference
title: "Plugin: feature.presentation"
description: Reference for FeaturePresentationPlugin (plugin id feature.presentation), the convention plugin for feature UI modules such as :demo:presentation. Covers the plugins it applies (Android library, Compose, serialization, KSP, Hilt, Detekt), Compose enablement, its dependencies (implementation core:presentation, Hilt, core:presentation test fixtures), the core/data visibility gap, and a complete module example.
tags: [core-dependencies, gradle, convention-plugin, feature-module, compose, presentation]
sources:
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-bbb0f73ef5c40bd97bd940fa
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/utils/Extensions.kt
  - id: openwiki-source-7e3d591f8636cbc331831be8
    resource: repo://core/presentation/src/testFixtures/java/com/core/presentation/BaseTest.kt
  - id: openwiki-source-823d791cada9047e35d0e367
    resource: repo://demo/presentation/build.gradle.kts
  - id: openwiki-source-06e0b2c5eb0f49f9782454e5
    resource: repo://login/presentation/build.gradle.kts
generated: { by: "claude-code", at: "2026-11-02T14:42:55.956Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-02T14:42:55.956Z
---

# Plugin: `feature.presentation` (`com.core.dependencies.plugins.FeaturePresentationPlugin`)

## Purpose

Use it for a feature's **UI module**: screens, contracts, ViewModels, Navigation3 entries and graphs, and their Hilt navigation module (destinations live in the shared `:common:navigation` module, which also applies this plugin; see
[core-presentation recipes](../core-presentation/recipes.md)).

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
demo/presentation/build.gradle.kts
login/presentation/build.gradle.kts
```

## What `apply(target)` does

1. **Plugins:** `com.android.library`, `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization` (for `@Serializable` destinations),
   `com.google.devtools.ksp`, `com.google.dagger.hilt.android`, `dev.detekt`.
2. **`LibraryExtension`:** `defaultConfiguration` (namespace default `com.base.application`), `configureBuildTypes`, `configurePackaging`, `configureTests()` (Android resources for JVM tests plus JDK module-open JVM arguments), and `buildFeatures { compose = true }`.
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Dependency |
|---|---|
| `implementation` | `project(":core:presentation")`, which exposes Compose, Material3, Navigation3, Paging Compose, lifecycle-viewmodel-compose, hilt-navigation-compose, browser and serialization via `api` |
| `implementation` | `project(":common:navigation")` (shared destinations), added only when the module path is not `:common:navigation` |
| `ksp` | `kotlin-metadata-jvm`, `hilt-android-compiler` |
| `implementation` | `hilt-android` |
| `testImplementation` | `testFixtures(project(":core:presentation"))`, which provides the test-unit bundle and shared `com.core.presentation.BaseTest` |
| `kspTest` | `hilt-android-compiler` for test-source Hilt component generation |
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
    // :common:navigation (destinations: com.common.navigation.destination.orders.*) is added by the plugin
    implementation(project(":orders:data"))   // needed to see ApiResult / core-data types and the feature's repositories
}
```
Register it in `settings.gradle.kts` and add `implementation(project(":orders:presentation"))` to `app/build.gradle.kts`.

## Gotchas

- **Do not declare `:common:navigation` in the feature build file.** The plugin adds it. The plugin skips it via `if (path != ":common:navigation")` because `:common:navigation` also applies `feature.presentation`; without the guard it would depend on itself and fail with a circular task dependency. `:app` gets it from `AppPlugin`, and `:core:presentation` (`CorePresentationPlugin`) must never depend on it (circular with `:common:navigation`).
- **No core/data on the classpath.** `ApiResult`, `ISessionManager`, etc. aren't visible unless you add the feature data module (or core:data). `BaseViewModel`'s use-case helpers
  use `ApiResult`, so most ViewModels need it ([dependency-visibility](dependency-visibility.md#gaps-and-fixes)).
- **Namespace:** the plugin's default is `com.base.application`; current feature modules override it (`:demo:presentation` uses `com.demo.presentation`; `:login:presentation` uses `com.login.presentation`; `:common:navigation` uses `com.common.navigation`). Set a unique namespace in each new module.
- Unit tests are configured with `configureTests()` and `kspTest(hilt-compiler)`. They inherit `com.core.presentation.BaseTest` from the core:presentation test fixtures. No Compose UI test artifacts are added ([core-presentation testing](../core-presentation/testing.md)).
- The Compose BoM isn't applied in this module. Versionless Compose artifacts rely on transitive versions (see [plugin-core-presentation](plugin-core-presentation.md#gotchas)).

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Recipes](recipes.md)
- [core-presentation recipes](../core-presentation/recipes.md)
- [Plugin: feature.data](plugin-feature-data.md)
