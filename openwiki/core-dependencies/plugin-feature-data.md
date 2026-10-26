---
type: build reference
title: "Plugin: feature.data"
description: Reference for FeatureDataPlugin (plugin id feature.data), the convention plugin for feature data modules such as :demo:data. Covers the plugins it applies (Android library, serialization, KSP, Hilt, Detekt), its Android configuration, its dependencies (api core:data, Hilt, core:data test fixtures), what it doesn't set up (namespace, Hilt test codegen, Robolectric test config), and a complete module example.
tags: [core-dependencies, gradle, convention-plugin, feature-module, data-layer]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-5ad5829264c75ebc25f88ee6
    resource: repo://demo/data/build.gradle.kts
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Plugin: `feature.data` (`com.core.dependencies.plugins.FeatureDataPlugin`)

## Purpose

Use it for a feature's **data layer** module (repositories, Retrofit services, DTOs, data Hilt modules). It re-exports core/data, so the feature presentation module
(and `:app`) can see `ApiResult` and core managers by depending on it.

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
demo/data/build.gradle.kts        # current user (no main sources yet)
```

## What `apply(target)` does

1. **Plugins:** `com.android.library`, `org.jetbrains.kotlin.plugin.serialization`, `com.google.devtools.ksp`, `com.google.dagger.hilt.android`, `dev.detekt`. No Compose and no Dokka.
2. **`LibraryExtension`:** `defaultConfiguration` (compileSdk 37, minSdk 26, **namespace `com.base.application`**, Java 21, test fixtures, toolchain 21), `configureBuildTypes`,
   `configurePackaging`. **No `configureTests()`.**
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Dependency |
|---|---|
| `api` | `project(":core:data")`, which also exposes kotlinx-serialization-json and Retrofit/OkHttp |
| `ksp` | `kotlin-metadata-jvm`, `hilt-android-compiler` |
| `implementation` | `hilt-android` |
| `testImplementation` | `testFixtures(project(":core:data"))`, which gives the test-unit bundle (coroutines-test, robolectric, hilt-android-testing, mockk-android) |
| `detektPlugins` | `detekt-rules-ktlint-wrapper`, `detekt-rules-libraries` |

## Example module

```kotlin
// orders/data/build.gradle.kts
plugins {
    alias(coreLibs.plugins.feature.data)
}

android {
    namespace = "com.orders.data"            // ALWAYS set; the default is shared
}

dependencies {
    // extra libraries only this feature needs, e.g. implementation(coreLibs.androidx.datastore)
}
```
Then `include(":orders:data")` in `settings.gradle.kts`, and make it reachable from `:app` (directly or via the feature presentation module) so Hilt picks up its modules.
Typical content: `OrdersApiService` + an `OrdersNetworkModule` using `RetrofitFactory` (see [core-data network](../core-data/network.md)).

## Gotchas

- **Namespace:** `:demo:data` doesn't set one, so it shares `com.base.application` with `:app` and `:demo:presentation`.
- **Hilt in tests:** no `kspTest(hilt-compiler)`, so `@HiltAndroidTest` tests won't compile their generated components. Add it to the module or the plugin.
- **Robolectric:** no `configureTests()` (no `isIncludeAndroidResources`, no `--add-opens`). Copy core/data's `BaseTest` pattern and add the config if needed. `BaseTest` itself isn't shared,
  because core/data's test fixtures contain no sources.
- `:demo:data` is included in settings but not depended on by any module.

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Recipes](recipes.md)
- [Plugin: feature.presentation](plugin-feature-presentation.md)
- [core-data overview](../core-data/overview.md)
- [core-data testing](../core-data/testing.md)
