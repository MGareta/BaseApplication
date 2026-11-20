---
type: build reference
title: Dependency visibility matrix
description: Which libraries and project classes are compile-visible in each module type (app, core:data, core:presentation, feature data, feature presentation) and in their unit tests, derived from the convention plugins' api, implementation and test-fixture declarations. Includes the gaps that cause unresolved-reference errors and how to fix them.
tags: [core-dependencies, gradle, dependencies, api, implementation, classpath, troubleshooting]
sources:
  - id: openwiki-source-3bfcb28142050978edf94754
    resource: repo://app/build.gradle.kts
  - id: openwiki-source-f9a7de034d8728b3e7dd333d
    resource: repo://core/data/src/testFixtures/java/com/core/data/BaseTest.kt
  - id: openwiki-source-6e98d638297776b5f81f559d
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/AppPlugin.kt
  - id: openwiki-source-ca878af941a44f593f84eec4
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CoreDataPlugin.kt
  - id: openwiki-source-b93e89ddcc6bb7ffc877b9e1
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CorePresentationPlugin.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-2605241fb6d0ff529d3cf13d
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/viewmodel/BaseViewModel.kt
  - id: openwiki-source-9ade9fd0b236966d699bbe38
    resource: repo://core/presentation/src/main/java/com/core/presentation/coroutines/CoroutinesUseCaseRunner.kt
  - id: openwiki-source-7e3d591f8636cbc331831be8
    resource: repo://core/presentation/src/testFixtures/java/com/core/presentation/BaseTest.kt
generated: { by: "claude-code", at: "2026-11-02T14:42:55.956Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-02T14:42:55.956Z
---

# Dependency visibility matrix

Use this page when you get **"Unresolved reference"** or **"Cannot access class … check your module classpath"** errors.

Rules: a module sees its own `api` and `implementation` dependencies, plus the **`api` dependencies of those** (transitively through `api` chains only).
An `implementation` dependency of a dependency is **not** visible at compile time (it's on the runtime classpath only). Unit tests (`src/test`) also see the module's own
test fixtures' `testFixturesApi` dependencies and anything added with `testImplementation`.

## What each plugin declares

| Plugin | `api` | `implementation` | Other |
|---|---|---|---|
| `core.data` | kotlinx-serialization-json, retrofit | datastore-preferences, hilt-android, retrofit converter-kotlinx-serialization, okhttp logging-interceptor, firebase BoM + analytics + crashlytics | `ksp`: kotlin-metadata, hilt-compiler. `testFixturesApi`: test-unit bundle. `kspTest`: hilt-compiler |
| `core.presentation` | kotlinx-serialization-json, compose foundation, ui-tooling-preview, material3, lifecycle-viewmodel-compose, navigation-ui-ktx, hilt-navigation-compose, navigation3-runtime, navigation3-ui, paging-compose, browser | **project(":core:data")**, kotlin-reflect, compose BoM (platform), hilt-android | `debugImplementation`: ui-tooling. `ksp`: kotlin-metadata, hilt-compiler. `testFixturesApi`: test-unit bundle and `project(":core:data")`. `kspTest`: hilt-compiler |
| `feature.data` | **project(":core:data")** | hilt-android | `ksp`: kotlin-metadata, hilt-compiler. `testImplementation`: `testFixtures(project(":core:data"))`; `kspTest`: hilt-compiler; `configureTests()` |
| `feature.presentation` (also used by `:common:navigation`) | none | **project(":core:presentation")**, hilt-android | `ksp`: kotlin-metadata, hilt-compiler. `testImplementation`: `testFixtures(project(":core:presentation"))`; `kspTest`: hilt-compiler; `configureTests()` |
| `app` | none | **project(":core:presentation")**, hilt-android | `ksp`: kotlin-metadata, hilt-compiler. The app module also adds `implementation(project(":demo:presentation"))` and `implementation(project(":login:presentation"))` |

Retrofit's own published dependencies include OkHttp, so `okhttp3.*` types (`OkHttpClient`, `Interceptor`, `ResponseBody`) are typically visible wherever retrofit is.

## Compile-time visibility (main source set)

| Visible in → | `:core:data` | `:core:presentation` | feature data | feature presentation | `:app` |
|---|---|---|---|---|---|
| core/data classes (`ApiResult`, `I*Manager`, `ActivityProvider`, …) | own | ✔ (implementation) | ✔ (api) | **✘** | **✘** |
| core/presentation classes (`BaseViewModel`, components, Navigation3 types, `Theme`) | ✘ | own | ✘ | ✔ | ✔ |
| kotlinx-serialization-json | ✔ | ✔ | ✔ (via core:data api) | ✔ (via core:presentation api) | ✔ |
| Retrofit / OkHttp | ✔ | ✔ | ✔ | **✘** | **✘** |
| okhttp logging-interceptor, converter, DataStore, Firebase | ✔ | ✘ | ✘ | ✘ | ✘ |
| Hilt runtime (`@HiltViewModel`, `@Module`, `dagger.*`) | ✔ | ✔ | ✔ | ✔ | ✔ |
| Compose foundation, Material3, ui-tooling-preview | ✘ | ✔ | ✘ | ✔ | ✔ |
| lifecycle-viewmodel-compose, hilt-navigation-compose | ✘ | ✔ | ✘ | ✔ | ✔ |
| Navigation Compose (navigation-ui-ktx), Navigation3 runtime/ui | ✘ | ✔ | ✘ | ✔ | ✔ |
| paging-compose (incl. paging runtime) | ✘ | ✔ | ✘ | ✔ | ✔ |
| androidx.browser (Custom Tabs) | ✘ | ✔ | ✘ | ✔ | ✔ |
| kotlin-reflect | ✘ | ✔ | ✘ | ✘ | ✘ |

## Unit-test (`src/test`) visibility

| Module type | Test libraries available | Hilt test code generation (`kspTest` hilt-compiler) | Robolectric JVM config (`configureTests`) | Shared base classes |
|---|---|---|---|---|
| `:core:data` | test-unit bundle via its test fixtures | ✔ | ✔ | `com.core.data.BaseTest` in `src/testFixtures` |
| `:core:presentation` | test-unit bundle via its test fixtures, including core:data fixtures | ✔ | ✔ | `com.core.presentation.BaseTest` in `src/testFixtures` |
| feature data | test-unit bundle and BaseTest via `testFixtures(":core:data")` | ✔ | ✔ | `com.core.data.BaseTest` |
| feature presentation | test-unit bundle and core:presentation fixtures via `testFixtures(":core:presentation")` | ✔ | ✔ | `com.core.presentation.BaseTest` |
| `:app` | **nothing** | ✘ | ✘ | none |

No module gets Compose UI test artifacts (`ui-test-junit4`, `ui-test-manifest`) or `androidTest` dependencies.

## Gaps and fixes

1. **Feature presentation modules can't see core/data types.** `core.presentation` depends on core:data with `implementation`, but its public API exposes core/data
   types: `CoroutinesUseCaseRunner` (implemented by `BaseViewModel`) takes and returns `ApiResult`. A feature ViewModel that calls `withUseCaseScopeLazily { useCase() }`
   needs `ApiResult` on its classpath. Fix (choose one):
   - in the feature presentation module: `implementation(project(":<feature>:data"))` (feature data `api`s core:data, so the types become visible), or
   - in `CorePresentationPlugin`: change `implementation(project(":core:data"))` to `api(...)`.
2. **`:app` can't see core/data** (for example to set `IEnvironmentManager.environment` in `App.onCreate`). Add `implementation(project(":core:data"))` to `app/build.gradle.kts`,
   or make core:presentation `api` core:data.
3. **`:app` doesn't depend on `:demo:data`** (only `:demo:presentation`). Hilt aggregates bindings from all modules on the app's runtime classpath, so data modules must be
   reachable from `:app`, either directly or through a presentation module that depends on them.
4. **Destinations live in `:common:navigation`.** A feature presentation module (`:demo:presentation`, `:login:presentation`, ...) adds `implementation(project(":common:navigation"))` to import `com.common.navigation.destination.<feature>.<Screen>ScreenNavDestination`, including other features' destinations for cross-feature navigation, without depending on the other feature. `:common:navigation` applies `feature.presentation`, so it sees only `:core:presentation` (and its `api` libraries) and cannot depend on features; destination arguments are therefore primitives/IDs or `core`/`:common:navigation` types. Keep it pure: destination classes only. `:app` needs its own `implementation(project(":common:navigation"))` to reference a destination directly.
5. **Feature-module test setup:** both feature plugins call `configureTests()` and add Hilt test code generation with `kspTest(hilt-compiler)`. Feature-data tests receive `com.core.data.BaseTest` through `testFixtures(":core:data")`; feature-presentation tests receive `com.core.presentation.BaseTest` through `testFixtures(":core:presentation")`.
6. **Firebase/DataStore in features:** they're `implementation` in core:data. Add them in the feature module if you need the APIs directly.

## Data flow

```mermaid
flowchart BT
    data[":core:data"]
    pres[":core:presentation"] -->|implementation| data
    fdata["feature data"] -->|api| data
    fpres["feature presentation"] -->|implementation| pres
    app[":app"] -->|implementation| pres
    app -->|implementation| fpres
    fpres -. "add implementation to see ApiResult" .-> fdata
```

## Related pages

- [Version catalog](version-catalog.md)
- [Plugin: core.data](plugin-core-data.md)
- [Plugin: core.presentation](plugin-core-presentation.md)
- [Plugin: feature.data](plugin-feature-data.md)
- [Plugin: feature.presentation](plugin-feature-presentation.md)
- [Plugin: app](plugin-app.md)
- [core-presentation overview](../core-presentation/overview.md)
- [core-data overview](../core-data/overview.md)
