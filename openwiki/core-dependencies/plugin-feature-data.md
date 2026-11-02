---
type: build reference
title: "Plugin: feature.data"
description: Reference for FeatureDataPlugin (plugin id feature.data), the convention plugin for feature data modules such as :demo:data. Covers its applied plugins, Android and test configuration, dependencies (api core:data, Hilt, test fixtures and Hilt test codegen), and a complete module example.
tags: [core-dependencies, gradle, convention-plugin, feature-module, data-layer]
sources:
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-5ad5829264c75ebc25f88ee6
    resource: repo://demo/data/build.gradle.kts
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-02T14:42:55.956Z
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
   `configurePackaging` and `configureTests()` (Android resources for JVM tests plus JDK module-open JVM arguments).
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Dependency |
|---|---|
| `api` | `project(":core:data")`, which also exposes kotlinx-serialization-json and Retrofit/OkHttp |
| `ksp` | `kotlin-metadata-jvm`, `hilt-android-compiler` |
| `implementation` | `hilt-android` |
| `testImplementation` | `testFixtures(project(":core:data"))`, which gives the test-unit bundle (coroutines-test, robolectric, hilt-android-testing, mockk-android) |
| `kspTest` | `hilt-android-compiler` for test-source Hilt component generation |
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
- **Hilt in tests:** `kspTest(hilt-compiler)` is configured, so test-source Hilt components can be generated. The core:data test fixture dependency supplies test libraries, but its fixture source set contains no `BaseTest` to share.
- **Robolectric:** `configureTests()` enables Android resources for JVM tests and adds JDK module-open JVM arguments.
- `:demo:data` is included in settings but not depended on by any module.

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Recipes](recipes.md)
- [Plugin: feature.presentation](plugin-feature-presentation.md)
- [core-data overview](../core-data/overview.md)
- [core-data testing](../core-data/testing.md)
