---
type: how-to guide
title: Build recipes
description: Step-by-step Gradle recipes for BaseApplication. Covers creating and wiring feature data and presentation modules, adding or upgrading libraries and plugins in the coreLibs catalog, adding bundles, writing a new convention plugin, bumping SDK and JVM levels, enabling unit or Compose UI tests for a module type, and the commands to build and verify.
tags: [core-dependencies, gradle, recipes, how-to, modules, version-catalog]
sources:
  - id: openwiki-source-3bfcb28142050978edf94754
    resource: repo://app/build.gradle.kts
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-f9a7de034d8728b3e7dd333d
    resource: repo://core/data/src/testFixtures/java/com/core/data/BaseTest.kt
  - id: openwiki-source-e109b62d9d6821d541faa04b
    resource: repo://core/dependencies/build.gradle.kts
  - id: openwiki-source-4a1624a1ef947742c273fc18
    resource: repo://core/dependencies/gradle/libs.versions.toml
  - id: openwiki-source-b93e89ddcc6bb7ffc877b9e1
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CorePresentationPlugin.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-7e3d591f8636cbc331831be8
    resource: repo://core/presentation/src/testFixtures/java/com/core/presentation/BaseTest.kt
  - id: openwiki-source-823d791cada9047e35d0e367
    resource: repo://demo/presentation/build.gradle.kts
  - id: openwiki-source-b68c69ba290f0fee793ec69b
    resource: repo://gradle.properties
  - id: openwiki-source-06e0b2c5eb0f49f9782454e5
    resource: repo://login/presentation/build.gradle.kts
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "claude-code", at: "2026-11-02T14:42:55.956Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-02T14:42:55.956Z
---

# Build recipes

All build logic lives in `core/dependencies` (plugins, `Config`, `Extensions.kt`) and `core/dependencies/gradle/libs.versions.toml` (the catalog). Module build files stay minimal.

## 1. Create a feature data module

1. Create `orders/data/` with `build.gradle.kts`:
   ```kotlin
   plugins { alias(coreLibs.plugins.feature.data) }
   android { namespace = "com.orders.data" }
   dependencies {}
   ```
   and `orders/data/src/main/AndroidManifest.xml` with `<manifest />` (an empty manifest is enough for a library).
2. Register it in the root `settings.gradle.kts`: `include(":orders:data")`.
3. Put code under `src/main/java/com/orders/data/...`: Retrofit service, repository, a Hilt `@Module @InstallIn(SingletonComponent::class)`.
4. Make it reachable from `:app` (Hilt needs every module with bindings on the app classpath). Add `implementation(project(":orders:data"))` to the feature presentation module
   (recommended, since it also exposes `ApiResult`) or to `app/build.gradle.kts`.

## 2. Create a feature presentation module

1. `orders/presentation/build.gradle.kts`:
   ```kotlin
   plugins { alias(coreLibs.plugins.feature.presentation) }
   android { namespace = "com.orders.presentation" }
   dependencies {
       implementation(project(":common:navigation"))   // shared destinations
       implementation(project(":orders:data"))
   }
   ```
2. `include(":orders:presentation")` in `settings.gradle.kts`.
3. `app/build.gradle.kts`: `implementation(project(":orders:presentation"))`.
4. Add the screen destination `OrdersListScreenNavDestination` to `:common:navigation` (`common/navigation/src/main/java/com/common/navigation/destination/orders/`, package `com.common.navigation.destination.orders`, public), then add Navigation3 entries, a graph and a `@Module @InstallIn(ActivityRetainedComponent::class)` with `@IntoMap @ClassKey` providers
   ([core-presentation recipes](../core-presentation/recipes.md#1-new-feature-screen-navigation3)).
5. The demo modules now declare explicit namespaces. `:app` still includes `:demo:presentation`, while `:demo:data` is not depended on; remove unused demo modules from settings and app wiring if replacing them.

## 3. Add or upgrade a library

1. Edit **only** `core/dependencies/gradle/libs.versions.toml`:
   ```toml
   [versions]
   coil = "3.3.0"
   [libraries]
   coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
   ```
   Omit `version.ref` for BoM-managed artifacts (like `androidx-compose-foundation`).
2. Choose where to expose it:
   - **One module only:** `implementation(coreLibs.coil.compose)` in that module's `build.gradle.kts` (aliases with dashes become dot accessors).
   - **Every module of a type:** in the plugin, `val coil = coreLibs.findLibrary("coil-compose").get()`, then `implementation(dependencyNotation = coil)`
     (or `api(...)` in a core plugin to re-export to features).
3. Upgrading: change the `[versions]` value. Watch the couplings: `kotlin` drives the Kotlin Gradle plugin, the compose and serialization compiler plugins, and kotlin-metadata. KSP (`google-ksp`) must
   support that Kotlin version. AGP (`android-gradle-plugin`) must support `compileSdk` and the Gradle wrapper (9.8.2). The Compose BoM and the pinned `material3` must be compatible.

## 4. Add a Gradle plugin from the catalog

1. `[plugins] my-plugin = { id = "com.example.plugin", version.ref = "myPlugin" }`.
2. Root `build.gradle.kts`: `alias(coreLibs.plugins.my.plugin) apply false`. This puts it on the build classpath so convention plugins can apply it by id.
3. Apply it in a module (`plugins { alias(coreLibs.plugins.my.plugin) }`) or in a convention plugin:
   `pluginManager.apply(coreLibs.findPlugin("my-plugin").get().get().pluginId)`. If the plugin class needs its API types, add `compileOnly(coreLibs.<gradle-artifact>)` in `core/dependencies/build.gradle.kts`.
   Example: wire Crashlytics and Google Services into `AppPlugin` for Firebase.

## 5. Add a bundle

```toml
[bundles]
test-compose = ["androidx-compose-ui-test-junit4"]
```
Use it with `coreLibs.findBundle("test-compose").get()` in plugins or `coreLibs.bundles.test.compose` in scripts.

## 6. Add a new convention plugin

1. `core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDomainPlugin.kt`:
   ```kotlin
   class FeatureDomainPlugin : Plugin<Project> {
       override fun apply(target: Project) = with(target) {
           pluginManager.apply("org.jetbrains.kotlin.jvm")       // plus detekt, etc.
           configureDetekt()
           dependencies { implementation(dependencyNotation = coreLibs.findLibrary("kotlin-serialization").get()) }
       }
   }
   ```
2. Register it in `core/dependencies/build.gradle.kts`:
   `gradlePlugin { plugins { register("featureDomain") { id = "feature.domain"; implementationClass = "com.core.dependencies.plugins.FeatureDomainPlugin" } } }`.
3. Add a catalog alias `feature-domain = { id = "feature.domain", version = "unspecified" }` and apply it with `alias(coreLibs.plugins.feature.domain)`.
4. `validatePlugins` runs with `failOnWarning`, so keep the plugin free of validation warnings.

## 7. Change SDK, JVM or app identity

Edit `core/dependencies/src/main/java/com/core/dependencies/config/Config.kt` ([config](config.md)): `compileSdkVersion`, `targetSdkVersion`, `minSdkVersion`,
`applicationId`, `versionCode`/`versionName`, `javaVersion`/`kotlinJvm`. For JVM changes, also update `core/dependencies/build.gradle.kts` (`java { ... }`) and make sure a matching JDK
toolchain is available.

## 8. Enable tests for a module type

- **Robolectric unit tests in feature modules:** `FeatureDataPlugin` and `FeaturePresentationPlugin` already call `configureTests()` and add
  `kspTest(dependencyNotation = hiltCompilerDependency)`. `:core:data` provides `com.core.data.BaseTest` in `core/data/src/testFixtures`, and `FeatureDataPlugin`
  supplies it through `testFixtures(project(":core:data"))`. `:core:presentation` provides its own `com.core.presentation.BaseTest` in its test fixtures and exposes
  the core:data fixture dependency; `FeaturePresentationPlugin` supplies the presentation fixtures through `testFixtures(project(":core:presentation"))`.
  Kotlin test fixture sources are enabled via `android.experimental.enableTestFixturesKotlinSupport`.
- **Compose UI tests (JVM):** add catalog entries `androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }` and
  `androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }`, then in `CorePresentationPlugin`/`FeaturePresentationPlugin` add
  `testImplementation(platform(composeBom))`, `testImplementation(uiTestJunit4)`, `debugApi(uiTestManifest)` (adds `debugImplementation`) and `configureTests()`.
- **Instrumented tests:** currently removed. You'd need `androidTestImplementation` helpers (not present in `Extensions.kt`), a runner (`testInstrumentationRunner`) and AndroidX test deps.

## 9. Build and verify

```bash
./gradlew help                                  # configuration sanity (plugins resolve, catalog valid)
./gradlew :core:dependencies:validatePlugins    # may need: ./gradlew -p core/dependencies validatePlugins
./gradlew assembleDebug                         # all modules
./gradlew :core:data:testDebugUnitTest          # the module with tests
./gradlew detekt                                # static analysis (autoCorrect rewrites files)
./gradlew :app:dependencies --configuration debugCompileClasspath   # inspect visibility
```

## Related pages

- [Overview](overview.md)
- [Version catalog](version-catalog.md)
- [Dependency visibility](dependency-visibility.md)
- [Plugin: feature.data](plugin-feature-data.md)
- [Plugin: feature.presentation](plugin-feature-presentation.md)
- [core-presentation recipes](../core-presentation/recipes.md)
