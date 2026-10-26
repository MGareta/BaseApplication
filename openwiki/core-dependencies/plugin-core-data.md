---
type: build reference
title: "Plugin: core.data"
description: Reference for CoreDataPlugin (plugin id core.data), the convention plugin for the :core:data library. Covers the plugins it applies (including Dokka and Detekt), its Android configuration (it's the only plugin that calls configureTests), and every dependency with its configuration (api serialization and Retrofit, implementation DataStore, Hilt, OkHttp logging and Firebase, the test-unit bundle via test fixtures).
tags: [core-dependencies, gradle, convention-plugin, core-data, retrofit, firebase, testing]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-a89a46a6562e5231bac73204
    resource: repo://core/data/build.gradle.kts
  - id: openwiki-source-4a1624a1ef947742c273fc18
    resource: repo://core/dependencies/gradle/libs.versions.toml
  - id: openwiki-source-ca878af941a44f593f84eec4
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/CoreDataPlugin.kt
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Plugin: `core.data` (`com.core.dependencies.plugins.CoreDataPlugin`)

## Purpose

It configures the shared data library `:core:data`. It isn't intended for feature modules (use [`feature.data`](plugin-feature-data.md)).

## File layout

```
core/dependencies/src/main/java/com/core/dependencies/plugins/CoreDataPlugin.kt
core/data/build.gradle.kts
```

## What `apply(target)` does

1. **Plugins applied:** `com.android.library`, `org.jetbrains.kotlin.plugin.serialization`, `com.google.devtools.ksp`, `com.google.dagger.hilt.android`,
   `org.jetbrains.dokka` (**only plugin that applies Dokka**), `dev.detekt`.
2. **`LibraryExtension`:** `defaultConfiguration` (compileSdk 37, minSdk 26, namespace default, Java 21, test fixtures, toolchain 21), `configureBuildTypes`,
   `configurePackaging`, **`configureTests`** (unit tests include Android resources, plus the `--add-opens` JVM args for Robolectric and MockK on JDK 21).
3. `configureDetekt()`.
4. **Dependencies:**

| Configuration | Catalog alias | Coordinates (version) | Why |
|---|---|---|---|
| `ksp` | `kotlin-metadata` | `org.jetbrains.kotlin:kotlin-metadata-jvm` (2.4.21) | KSP/Hilt metadata support |
| `api` | `kotlin-serialization` | `org.jetbrains.kotlinx:kotlinx-serialization-json` (1.12.0) | DTOs, cache JSON, exported to consumers |
| `implementation` | `androidx-datastore` | `androidx.datastore:datastore-preferences` (1.3.0) | Disk cache |
| `implementation` | `google-hilt-android` | `com.google.dagger:hilt-android` (2.60.1) | DI runtime |
| `ksp` | `google-hilt-compiler` | `com.google.dagger:hilt-android-compiler` (2.60.1) | DI codegen |
| `api` | `squareup-retrofit` | `com.squareup.retrofit2:retrofit` (3.0.0) | Feature API services, exported |
| `implementation` | `squareup-retrofit-converter` | `com.squareup.retrofit2:converter-kotlinx-serialization` (3.0.0) | JSON converter |
| `implementation` | `squareup-okhttp-logging-interceptor` | `com.squareup.okhttp3:logging-interceptor` (5.5.1) | HTTP logging |
| `implementation` (platform) | `google-firebase-bom` | `com.google.firebase:firebase-bom` (35.1.0) | Firebase versions |
| `implementation` | `google-firebase-crashlytics` | `com.google.firebase:firebase-crashlytics` (BoM) | Analytics framework |
| `implementation` | `google-firebase-analytics` | `com.google.firebase:firebase-analytics` (BoM) | Analytics framework |
| `testFixturesApi` | bundle `test-unit` | coroutines-test 1.11.0, robolectric 4.17, hilt-android-testing 2.60.1, mockk-android 1.14.11 | Unit tests here and in feature data modules |
| `kspTest` | `google-hilt-compiler` | | `@HiltAndroidTest` components |
| `detektPlugins` | `detekt-ktlint`, `detekt-library` | `dev.detekt:*` (2.0.0-alpha.6) | Lint rules |

`core/data/build.gradle.kts` adds `namespace = "com.core.data"` and `testInstrumentationRunner = "com.core.data.DefaultTestRunner"` (the class no longer exists, and nothing
uses instrumented tests).

## Visibility consequences

Consumers (`:core:presentation` via `implementation`, feature data via `api`) see core/data classes plus kotlinx-serialization and Retrofit (and OkHttp transitively).
They **don't** see DataStore, the converter, logging-interceptor or Firebase. See [dependency-visibility](dependency-visibility.md).

## Gotchas

- The Firebase libraries are present, but the Crashlytics Gradle plugin and Google Services plugin aren't applied anywhere, so Firebase initialization depends on the app module providing them.
- Dokka is applied but has no configuration. Run `./gradlew :core:data:dokkaGenerate` (Dokka 2 task names) to produce API docs.
- `testFixturesApi` leaks the test bundle to every module that adds `testFixtures(project(":core:data"))`. There are no actual `src/testFixtures` sources.

## Related pages

- [core-data overview](../core-data/overview.md)
- [core-data testing](../core-data/testing.md)
- [Dependency visibility](dependency-visibility.md)
- [Version catalog](version-catalog.md)
- [Extensions](extensions.md)
