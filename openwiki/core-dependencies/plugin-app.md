---
type: build reference
title: "Plugin: app"
description: Reference for AppPlugin (plugin id app), the convention plugin for the :app application module. Covers the Gradle plugins it applies, ApplicationExtension configuration (application id, version, debug suffixes, release shrinking signed with the debug key), Compose, its dependencies, and what the app module can and can't see.
tags: [core-dependencies, gradle, convention-plugin, android-application, app]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-3bfcb28142050978edf94754
    resource: repo://app/build.gradle.kts
  - id: openwiki-source-6e98d638297776b5f81f559d
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/AppPlugin.kt
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Plugin: `app` (`com.core.dependencies.plugins.AppPlugin`)

## Purpose

It configures the single Android **application** module (`:app`). Apply it with `plugins { alias(coreLibs.plugins.app) }`. It isn't meant for any other module.

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/AppPlugin.kt
app/build.gradle.kts
```

## What `apply(target)` does

1. **Catalog lookups:** plugins `android-application`, `kotlin-compose`, `google-ksp`, `google-hilt`, `detekt`. Libraries `kotlin-metadata`, `google-hilt-android`,
   `google-hilt-compiler`, `detekt-ktlint`.
2. **Applies plugins (in order):** `com.android.application`, `org.jetbrains.kotlin.plugin.compose` (**applied twice**, which is harmless because the second apply is a no-op),
   `com.google.devtools.ksp`, `com.google.dagger.hilt.android`, `dev.detekt`. It **doesn't apply** `kotlin-serializable`, Dokka, Crashlytics or Google Services.
3. **`ApplicationExtension`:**
   - `defaultConfiguration(this)`: compileSdk 37, minSdk 26, targetSdk 37, namespace `com.base.application`, Java 21, test fixtures enabled, JVM toolchain 21.
   - `configureBuildTypes()`: release `isMinifyEnabled = true` + `proguard-android-optimize.txt` + `proguard-rules.pro`.
   - `configurePackaging()`: META-INF license excludes.
   - `defaultConfig { applicationId = "com.base.application"; versionCode = 1; versionName = "1.0" }`.
   - `buildTypes.release { isShrinkResources = true; signingConfig = signingConfigs.getByName("debug") }`: **release builds are signed with the debug keystore**.
   - `buildTypes.debug { applicationIdSuffix = ".debug"; versionNameSuffix = "-debug" }`, so the debug app id is `com.base.application.debug`.
   - `buildFeatures { compose = true }`.
4. `configureDetekt()`.
5. **Dependencies:**

   | Configuration | Dependency |
   |---|---|
   | `implementation` | `project(":core:presentation")` |
   | `ksp` | `kotlin-metadata-jvm` (Kotlin 2.4.21), `hilt-android-compiler` (2.60.1) |
   | `implementation` | `hilt-android` (2.60.1) |
   | `detektPlugins` | `detekt-rules-ktlint-wrapper` |

`app/build.gradle.kts` adds `implementation(project(":demo:presentation"))` and an empty `android {}`. No namespace override, so it stays `com.base.application`.

## Visibility consequences

- `:app` sees core/presentation and its `api` libraries (Compose, Material3, Navigation3, Paging, …). It **doesn't** see core/data (`IEnvironmentManager`, `ApiResult`) because core:presentation
  uses `implementation`. To configure the environment in `App.onCreate`, add `implementation(project(":core:data"))`.
- Hilt aggregates `@Module`/`@InstallIn` from every module on the runtime classpath, so feature modules must be reachable from `:app` (`:demo:data` currently isn't).
- No test dependencies or `kspTest` are configured for the app.

## Example

```kotlin
// app/build.gradle.kts
plugins {
    alias(coreLibs.plugins.app)
}

android {}

dependencies {
    implementation(project(":demo:presentation"))
    implementation(project(":orders:presentation"))   // each feature presentation module
    implementation(project(":orders:data"))           // make data modules' Hilt bindings reachable if not already transitive
}
```

## Gotchas

- Release signing with the debug key is only acceptable for local testing. Add a proper `signingConfigs.create("release")` before distribution.
- Release builds minify and shrink resources while all modules ship empty ProGuard rules. Navigation3 reflection, kotlinx-serialization and Retrofit may need keep rules
  (see [core-presentation overview](../core-presentation/overview.md#proguard--r8)).
- Firebase (used by core/data analytics) needs the Google Services plugin and `google-services.json` in `:app`. Neither is configured.

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Config](config.md)
- [Extensions](extensions.md)
- [CoreApp and BaseActivity](../core-presentation/app-and-activity.md)
- [Recipes](recipes.md)
