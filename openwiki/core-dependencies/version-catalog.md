---
type: build reference
title: Version catalog (coreLibs)
description: Every entry in core/dependencies/gradle/libs.versions.toml (the coreLibs catalog). Covers all versions, libraries with Maven coordinates, plugins with ids, and the test-unit bundle, each mapped to the convention plugins, build scripts or modules that use it, with unused and BoM-managed entries flagged.
tags: [core-dependencies, gradle, version-catalog, dependencies, versions]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T16:41:29.588Z
sources:
  - id: openwiki-source-2a9daaac1604f238ef4c63fb
    resource: repo://build.gradle.kts
  - id: openwiki-source-4a1624a1ef947742c273fc18
    resource: repo://core/dependencies/gradle/libs.versions.toml
generated: { by: "claude-code", at: "2026-10-26T16:41:29.588Z" }
---

# Version catalog (`coreLibs`)

File: `core/dependencies/gradle/libs.versions.toml`. It's imported as `coreLibs` by **both** `core/dependencies/settings.gradle.kts` and the root `settings.gradle.kts`.
Plugins look entries up by their dashed alias (`coreLibs.findLibrary("google-hilt-android")`). Scripts use dot accessors (`coreLibs.plugins.core.data`, `coreLibs.android.gradle`).

## `[versions]`

| Key | Value | Used by |
|---|---|---|
| `android-gradle-plugin` | 9.4.1 | lib `android-gradle`, plugins `android-application`/`android-library` |
| `kotlin` | 2.4.21 | libs `kotlin-metadata`, `kotlin-gradle`; plugins `kotlin-compose`, `kotlin-serializable` |
| `kotlin-serialization` | 1.12.0 | lib `kotlin-serialization` |
| `kotlin-coroutines-test` | 1.11.0 | lib `kotlin-coroutines-test` |
| `androidx-compose` | 2026.09.00 | lib `androidx-compose` (BoM) |
| `androidx-compose-material3` | 1.4.0 | lib `androidx-compose-material3` (overrides the BoM) |
| `androidx-lifecycle` | 2.11.0 | lib `androidx-lifecycle-viewModel` |
| `androidx-navigation` | 2.10.2 | lib `androidx-navigation` |
| `androidx-navigation-hilt` | 1.4.0 | lib `androidx-navigation-hilt` |
| `androidx-navigation3` | 1.2.0 | libs `androidx-navigation3-runtime`, `-ui` |
| `androidx-datastore` | 1.3.0 | lib `androidx-datastore` |
| `androidx-paging` | 3.5.3 | lib `androidx-paging` |
| `androidx-browser` | 1.10.0 | lib `androidx-browser` |
| `google-hilt` | 2.60.1 | libs `google-hilt-android`, `-compiler`, `-test`; plugin `google-hilt` |
| `google-ksp` | 2.3.12 | plugin `google-ksp` |
| `google-crashlytics` | 3.0.8 | plugin `google-crashlytics` |
| `google-firebase-bom` | 35.1.0 | lib `google-firebase-bom` |
| `squareup-retrofit` | 3.0.0 | lib `squareup-retrofit` |
| `squareup-kotlinx` | 3.0.0 | lib `squareup-retrofit-converter` |
| `squareup-okhttp` | 5.5.1 | lib `squareup-okhttp-logging-interceptor` |
| `robolectric` | 4.17 | lib `robolectric` |
| `mockk` | 1.14.11 | lib `mockk` |
| `dokka` | 2.2.0 | plugin `dokka` |
| `detekt` | 2.0.0-alpha.6 | libs `detekt-gradle`, `detekt-ktlint`, `detekt-library`; plugin `detekt` |

## `[libraries]`

| Alias | Coordinates | Version | Consumed by (configuration) |
|---|---|---|---|
| `android-gradle` | `com.android.tools.build:gradle` | 9.4.1 | `core/dependencies/build.gradle.kts` (`compileOnly`) |
| `kotlin-metadata` | `org.jetbrains.kotlin:kotlin-metadata-jvm` | 2.4.21 | all 5 plugins (`ksp`) |
| `kotlin-gradle` | `org.jetbrains.kotlin:kotlin-gradle-plugin` | 2.4.21 | `core/dependencies/build.gradle.kts` (`compileOnly`) |
| `kotlin-serialization` | `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.12.0 | core.data, core.presentation (`api`) |
| `kotlin-coroutines-test` | `org.jetbrains.kotlinx:kotlinx-coroutines-test` | 1.11.0 | only via bundle `test-unit` |
| `androidx-compose` | `androidx.compose:compose-bom` | 2026.09.00 | core.presentation (`implementation(platform)`) |
| `androidx-compose-foundation` | `androidx.compose.foundation:foundation` | BoM | core.presentation (`api`) |
| `androidx-compose-preview` | `androidx.compose.ui:ui-tooling-preview` | BoM | core.presentation (`api`) |
| `androidx-compose-preview-debug` | `androidx.compose.ui:ui-tooling` | BoM | core.presentation (`debugImplementation` via `debugApi`) |
| `androidx-compose-material3` | `androidx.compose.material3:material3` | 1.4.0 | core.presentation (`api`) |
| `androidx-lifecycle-viewModel` | `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.11.0 | core.presentation (`api`) |
| `androidx-navigation` | `androidx.navigation:navigation-ui-ktx` | 2.10.2 | core.presentation (`api`). **The View-based UI artifact, not navigation-compose.** |
| `androidx-navigation-hilt` | `androidx.hilt:hilt-navigation-compose` | 1.4.0 | core.presentation (`api`) |
| `androidx-navigation3-runtime` | `androidx.navigation3:navigation3-runtime` | 1.2.0 | core.presentation (`api`) |
| `androidx-navigation3-ui` | `androidx.navigation3:navigation3-ui` | 1.2.0 | core.presentation (`api`) |
| `androidx-datastore` | `androidx.datastore:datastore-preferences` | 1.3.0 | core.data (`implementation`) |
| `androidx-paging` | `androidx.paging:paging-compose` | 3.5.3 | core.presentation (`api`). The alias name hides that it's the **Compose** artifact. |
| `androidx-browser` | `androidx.browser:browser` | 1.10.0 | core.presentation (`api`) |
| `google-hilt-android` | `com.google.dagger:hilt-android` | 2.60.1 | all 5 plugins (`implementation`) |
| `google-hilt-compiler` | `com.google.dagger:hilt-android-compiler` | 2.60.1 | all 5 plugins (`ksp`); core.data and core.presentation also `kspTest` |
| `google-hilt-test` | `com.google.dagger:hilt-android-testing` | 2.60.1 | only via bundle `test-unit` |
| `google-firebase-bom` | `com.google.firebase:firebase-bom` | 35.1.0 | core.data (`implementation(platform)`) |
| `google-firebase-crashlytics` | `com.google.firebase:firebase-crashlytics` | BoM | core.data (`implementation`) |
| `google-firebase-analytics` | `com.google.firebase:firebase-analytics` | BoM | core.data (`implementation`) |
| `squareup-retrofit` | `com.squareup.retrofit2:retrofit` | 3.0.0 | core.data (`api`) |
| `squareup-retrofit-converter` | `com.squareup.retrofit2:converter-kotlinx-serialization` | 3.0.0 | core.data (`implementation`) |
| `squareup-okhttp-logging-interceptor` | `com.squareup.okhttp3:logging-interceptor` | 5.5.1 | core.data (`implementation`) |
| `robolectric` | `org.robolectric:robolectric` | 4.17 | only via bundle `test-unit` |
| `mockk` | `io.mockk:mockk-android` | 1.14.11 | only via bundle `test-unit` (**the Android/instrumented artifact** used in JVM tests, where `io.mockk:mockk` is the usual choice) |
| `detekt-gradle` | `dev.detekt:detekt-gradle-plugin` | 2.0.0-alpha.6 | `core/dependencies/build.gradle.kts` (`compileOnly`) |
| `detekt-ktlint` | `dev.detekt:detekt-rules-ktlint-wrapper` | 2.0.0-alpha.6 | all 5 plugins (`detektPlugins`) |
| `detekt-library` | `dev.detekt:detekt-rules-libraries` | 2.0.0-alpha.6 | all plugins except `app` (`detektPlugins`) |

No library alias is completely unused.

## `[plugins]`

| Alias | Id | Version | Declared in root `build.gradle.kts` (`apply false`) | Applied by |
|---|---|---|---|---|
| `android-application` | `com.android.application` | 9.4.1 | ✔ | app |
| `android-library` | `com.android.library` | 9.4.1 | ✔ | core.data, core.presentation, feature.data, feature.presentation |
| `kotlin-compose` | `org.jetbrains.kotlin.plugin.compose` | 2.4.21 | ✔ | app (twice), core.presentation, feature.presentation |
| `kotlin-serializable` | `org.jetbrains.kotlin.plugin.serialization` | 2.4.21 | ✔ | core.data, core.presentation, feature.data, feature.presentation |
| `google-ksp` | `com.google.devtools.ksp` | 2.3.12 | ✔ | all 5 |
| `google-hilt` | `com.google.dagger.hilt.android` | 2.60.1 | ✔ | all 5 |
| `google-crashlytics` | `com.google.firebase.crashlytics` | 3.0.8 | ✔ | **none: declared but never applied** |
| `dokka` | `org.jetbrains.dokka` | 2.2.0 | ✔ | core.data only |
| `detekt` | `dev.detekt` | 2.0.0-alpha.6 | ✔ | all 5 |
| `app` | `app` | unspecified | | `app/build.gradle.kts` |
| `core-data` | `core.data` | unspecified | | `core/data/build.gradle.kts` |
| `core-presentation` | `core.presentation` | unspecified | | `core/presentation/build.gradle.kts` |
| `feature-data` | `feature.data` | unspecified | | `demo/data/build.gradle.kts` |
| `feature-presentation` | `feature.presentation` | unspecified | | `demo/presentation/build.gradle.kts` |

## `[bundles]`

| Bundle | Members | Used by |
|---|---|---|
| `test-unit` | `kotlin-coroutines-test`, `robolectric`, `google-hilt-test`, `mockk` | core.data and core.presentation (`testFixturesApi`), reaching feature modules via `testImplementation(testFixtures(...))` |

There's no JUnit entry. JUnit 4 arrives transitively (through Robolectric / coroutines-test), which works but is implicit.

## Gotchas

- **Kotlin and KSP coupling:** KSP `2.3.12` must support Kotlin `2.4.21`. Bump them together.
- **Crashlytics plugin unused** and there's **no Google Services plugin**, while Firebase libraries are used.
- **Misleading aliases:** `androidx-navigation` is `navigation-ui-ktx` (View UI), and `androidx-paging` is `paging-compose`.
- **BoM scope:** versionless Compose artifacts depend on the BoM, which is only applied (as `implementation`) in core.presentation (see [plugin-core-presentation](plugin-core-presentation.md#gotchas)).
- **Alpha tooling:** Detekt `2.0.0-alpha.6`.

## Related pages

- [Dependency visibility](dependency-visibility.md)
- [Recipes (add/upgrade a library)](recipes.md#3-add-or-upgrade-a-library)
- [Overview](overview.md)
- [Code quality](code-quality.md)
