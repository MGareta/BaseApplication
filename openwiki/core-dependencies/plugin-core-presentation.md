---
type: build reference
title: "Plugin: core.presentation"
description: Reference for CorePresentationPlugin (plugin id core.presentation), the convention plugin for the :core:presentation library. Covers the plugins it applies (Compose, serialization, KSP, Hilt, Detekt), Compose enablement, every dependency with its configuration (implementation core:data, the many api AndroidX UI libraries, Compose BoM, debug tooling, test fixtures), and the visibility and versioning consequences.
tags: [core-dependencies, gradle, convention-plugin, core-presentation, compose, navigation3]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-4a1624a1ef947742c273fc18
    resource: repo://core/dependencies/gradle/libs.versions.toml
  - id: openwiki-source-b93e89ddcc6bb7ffc877b9e1
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CorePresentationPlugin.kt
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Plugin: `core.presentation` (`com.core.dependencies.plugins.CorePresentationPlugin`)

## Purpose

It configures the shared UI library `:core:presentation`. Feature UI modules use [`feature.presentation`](plugin-feature-presentation.md) instead.

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/CorePresentationPlugin.kt
core/presentation/build.gradle.kts
```

## What `apply(target)` does

1. **Plugins:** `com.android.library`, `org.jetbrains.kotlin.plugin.compose`, `org.jetbrains.kotlin.plugin.serialization`, `com.google.devtools.ksp`,
   `com.google.dagger.hilt.android`, `dev.detekt`.
2. **`LibraryExtension`:** `defaultConfiguration`, `configureBuildTypes`, `configurePackaging`, `buildFeatures { compose = true }`. **No `configureTests()`.**
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Alias | Coordinates (version) |
|---|---|---|
| `implementation` | | `project(":core:data")` |
| `ksp` | `kotlin-metadata` | `org.jetbrains.kotlin:kotlin-metadata-jvm` (2.4.21) |
| `implementation` | | `kotlin("reflect")` (Kotlin plugin version) |
| `api` | `kotlin-serialization` | `kotlinx-serialization-json` (1.12.0) |
| `implementation` (platform) | `androidx-compose` | `androidx.compose:compose-bom` (2026.09.00) |
| `api` | `androidx-compose-foundation` | `androidx.compose.foundation:foundation` (BoM) |
| `api` | `androidx-compose-preview` | `androidx.compose.ui:ui-tooling-preview` (BoM) |
| `debugImplementation` (helper `debugApi`) | `androidx-compose-preview-debug` | `androidx.compose.ui:ui-tooling` (BoM) |
| `api` | `androidx-compose-material3` | `androidx.compose.material3:material3` (**1.4.0**, pinned, overrides the BoM) |
| `api` | `androidx-lifecycle-viewModel` | `androidx.lifecycle:lifecycle-viewmodel-compose` (2.11.0) |
| `api` | `androidx-navigation` | `androidx.navigation:navigation-ui-ktx` (2.10.2) |
| `api` | `androidx-navigation-hilt` | `androidx.hilt:hilt-navigation-compose` (1.4.0) |
| `api` | `androidx-navigation3-runtime` | `androidx.navigation3:navigation3-runtime` (1.2.0) |
| `api` | `androidx-navigation3-ui` | `androidx.navigation3:navigation3-ui` (1.2.0) |
| `api` | `androidx-paging` | `androidx.paging:paging-compose` (3.5.3) |
| `api` | `androidx-browser` | `androidx.browser:browser` (1.10.0) |
| `implementation` | `google-hilt-android` | `hilt-android` (2.60.1) |
| `ksp` | `google-hilt-compiler` | `hilt-android-compiler` (2.60.1) |
| `testFixturesApi` | bundle `test-unit` | coroutines-test, robolectric, hilt-android-testing, mockk-android |
| `kspTest` | `google-hilt-compiler` | |
| `detektPlugins` | `detekt-ktlint`, `detekt-library` | |

`core/presentation/build.gradle.kts` adds `namespace = "com.core.presentation"`.

## Visibility consequences

- Consumers (feature presentation modules, `:app`) see core/presentation classes and **all `api` libraries** above: Compose foundation, Material3, Navigation Compose, Navigation3, Paging Compose, Browser and serialization.
- Consumers **don't** see core/data, even though core/presentation's public API (`CoroutinesUseCaseRunner`/`BaseViewModel`) references `ApiResult`. See [dependency-visibility](dependency-visibility.md#gaps-and-fixes).
- `navigation-ui-ktx` is the **View-based** Navigation UI artifact, not `navigation-compose`. The legacy `com.core.presentation.navigation` package uses Compose navigation APIs that come transitively from
  `hilt-navigation-compose`. Navigation3 is the active stack.

## Gotchas

- **The BoM isn't exported.** The Compose BoM is `implementation(platform(...))`, while `foundation` and `ui-tooling-preview` are versionless `api` dependencies. Consumer modules don't
  get the BoM constraint on their compile classpath, so these versionless artifacts must be resolved from other paths (Material3 1.4.0 pulls `foundation`). If a consumer
  fails with "could not find …:ui-tooling-preview:", add `implementation(platform(coreLibs.androidx.compose))` to the feature plugin, or make the BoM `api(platform(...))`. This hasn't been verified by building.
- Material3 is pinned to 1.4.0 independently of the BoM, so check compatibility when bumping the BoM.
- No `configureTests()` and no Compose UI test artifacts (see [core-presentation testing](../core-presentation/testing.md)).
- `kotlin-reflect` is an implementation dependency. Navigation3 entry type resolution uses Java reflection (`genericSuperclass`), not kotlin-reflect, but `KClass.objectInstance` needs it.

## Related pages

- [core-presentation overview](../core-presentation/overview.md)
- [core-presentation testing](../core-presentation/testing.md)
- [Dependency visibility](dependency-visibility.md)
- [Version catalog](version-catalog.md)
