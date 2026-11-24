# BaseApplication

A modular Android application template built with **Kotlin**, **Jetpack Compose** and **Hilt**. It gives you a clean, scalable architecture and a set of production-ready building blocks (networking, caching, security, session handling, navigation, theming and a Compose component library), so a new app or feature starts from a solid foundation instead of from scratch.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-26-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions)
[![Target SDK](https://img.shields.io/badge/targetSdk-37-3DDC84?logo=android&logoColor=white)](https://developer.android.com/about/versions)
[![JDK](https://img.shields.io/badge/JDK-21-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net)

## Table of Contents

- [Highlights](#highlights)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Core Data](#core-data-coredata)
- [Core Presentation](#core-presentation-corepresentation)
- [Creating a New Feature](#creating-a-new-feature)
- [Build Configuration](#build-configuration)
- [Code Quality and Testing](#code-quality-and-testing)
- [Documentation and AI-assisted Delivery](#documentation-and-ai-assisted-delivery)

## Highlights

- **Multi-module clean architecture** with a strict `data` / `presentation` split per feature.
- **MVI-style presentation layer** (`UiState`, `Event`, `Effect`) on top of a reusable `BaseViewModel`.
- **Type-safe Navigation 3** with feature-owned navigation graphs and shared, serializable destinations.
- **Ready-to-use managers** for networking, caching, encryption, sessions, files, downloads, permissions, analytics and notifications.
- **Compose design system**: typed theming (light and dark), form validation and a library of reusable components.
- **Convention plugins and a centralized version catalog** so every module is configured the same way.
- **Spec-driven, agent-assisted feature delivery** with OpenSpec, OpenWiki and Claude Code (optional).

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin, Kotlin Serialization, Coroutines and Flow |
| UI | Jetpack Compose (BOM), Material 3, Navigation 3, Paging Compose |
| Dependency injection | Hilt (with KSP) |
| Networking | Retrofit, OkHttp, kotlinx.serialization converter |
| Persistence | Jetpack DataStore, encrypted disk cache |
| Observability | Firebase Analytics and Crashlytics |
| Build | Gradle (Kotlin DSL), Android Gradle Plugin, convention plugins, version catalog |
| Quality | Detekt (with ktlint rules), Dokka |
| Testing | JUnit, MockK, Robolectric, Hilt testing, Coroutines test |

Exact versions live in [`core/dependencies/gradle/libs.versions.toml`](core/dependencies/gradle/libs.versions.toml).

## Architecture

The project follows a multi-module clean architecture to promote separation of concerns, scalability and maintainability. Each feature is split into a `data` module and a `presentation` module, and they build on the shared `core` modules.

```text
              ┌──────────────┐
              │     :app     │  assembles features, Hilt entry point
              └──────┬───────┘
                     ▼
        :<feature>:presentation ───► :core:presentation
                     │          └──► :common:navigation (shared destinations)
                     ▼
            :<feature>:data ───────► :core:data
```

- **Data layer**: API services, DTOs, domain models and the `I<Feature>Manager` abstraction with its implementation, exposed through Hilt modules.
- **Presentation layer**: screens, ViewModels, UI models and mappers, navigation entries and analytics tracking.
- **Convention plugins** (`:core:dependencies`) apply the right Android, Kotlin, Hilt, Detekt and Dokka configuration per module type.

## Project Structure

| Module | Description |
|---|---|
| `:app` | Application module. Wires every feature module together and hosts the main activity. |
| `:core:data` | Foundational data services: networking, caching, security, session, files, permissions, analytics and notifications. |
| `:core:presentation` | Base classes, navigation framework, theming and the reusable Compose component library. |
| `:core:dependencies` | Included build that provides the convention plugins and the shared version catalog (`coreLibs`). |
| `:common:navigation` | Shared, serializable Navigation 3 destinations. Features open each other's screens through it without depending on one another. |
| `:<feature>:data` / `:<feature>:presentation` | One pair of modules per feature, built on top of the core modules. |

Other top-level folders:

| Folder | Purpose |
|---|---|
| `config/detekt` | Detekt rule configuration. |
| `openwiki/` | Generated documentation index of the codebase (architecture, recipes, gotchas). |
| `openspec/` | Specs and change proposals for spec-driven feature delivery. |
| `docs/orchestrator/` | Guide and design notes for the agent-assisted delivery pipeline. |
| `scripts/orchestrator/` | Deterministic gate checks used by the pipeline. |
| `.claude/` | Agents, skills, commands and workflows for Claude Code. |

## Getting Started

### Prerequisites

- Android Studio (a recent stable version that supports the Android Gradle Plugin in the version catalog)
- JDK 21
- Android SDK 37 (`compileSdk` / `targetSdk`); the app supports Android 8.0 (API 26) and above

### Clone and build

```bash
git clone <repository-url>
cd BaseApplication
./gradlew :app:assembleDebug
```

Open the project in Android Studio and run the `app` configuration on a device or emulator to launch it.

### Useful Gradle commands

```bash
./gradlew :app:assembleDebug     # build the debug APK
./gradlew test                   # run unit tests of all modules
./gradlew :core:data:test        # run the tests of a single module
./gradlew detekt                 # run static analysis
```

> **Firebase:** analytics is pluggable through `IAnalyticsManager`. Configure your own Firebase project before shipping a release build.

## Core Data (`:core:data`)

- **Networking**
  - Built with Retrofit and OkHttp.
  - `ApiResult` sealed interface (`Success`, `Error`, `Exception`, `NoContent`) for robust and predictable response handling.
  - `NetworkResultCallAdapterFactory` integrates `ApiResult` seamlessly with Retrofit.
  - `IEnvironmentManager` switches between `DEV`, `UAT` and `PROD` API endpoints.
  - SSL pinning support for production environments.
- **Caching**
  - `ICacheManager` with multiple cache scopes.
  - `MemoryCacheScope`: in-memory LRU cache for frequently needed data.
  - `DiskCacheScope`: persistent on-disk cache with AES encryption for sensitive data.
  - Time-To-Live (TTL) and immortal cache configurations.
  - `repositoryPattern` utility that simplifies the cache-then-network strategy.
- **Security**
  - `ISecurityManager` offering AES and RSA encryption and decryption.
  - Keys are managed with the Android KeyStore.
- **Session management**
  - `ISessionManager` handles the session state (`Active`, `Inactive`).
  - Automatic session timeout with a warning mechanism exposed through the `SessionCommand` Flow.
  - Session data is cached securely in memory.
- **Files and downloads**
  - `IFileManager` saves and finds files in the app's private storage and handles permissions automatically.
  - `IDownloadManager` downloads and saves files from a URL.
- **Permissions and activity results**
  - `IPermissionManager` abstracts runtime permission requests.
  - `IActivityResultManager` provides a coroutine-based way to consume activity results.
- **Analytics and notifications**
  - `IAnalyticsManager` with a pluggable design (Firebase included).
  - `INotificationsManager` manages push notification permissions and interactions.

## Core Presentation (`:core:presentation`)

- **Type-safe navigation (Navigation 3)**
  - Define a feature graph with `INavigationGraph`.
  - Declare screen, dialog or bottom-sheet entries with `INavigationGraphEntry`.
  - Navigate to serializable destinations with `INavigationManager`.
  - Entries scoped to a graph share a graph-level ViewModel store.
- **Jetpack Compose UI toolkit**
  - **Buttons and CTAs**: `BlockCta`, `IconCta` and `LinkCta` in primary, secondary and tertiary styles.
  - **Forms**: `FormState`, `TextFieldState`, `SelectFieldState` and more, with built-in `Validators` (email, required, min/max length, etc.).
  - **Lists**: `PagingComponent` for paginated data and `NonLazyGridComponent` for simple grids.
  - **Containers and layout**: containers, toolbars, top and bottom navigation bars, bottom sheets, steppers, tabs, tags, tiles, badges, alerts and amount displays.
- **Theming**
  - Centralized `Theme` with strongly-typed `ColorsPalette`, `Dimensions` and `Typographies`.
  - Light and dark mode support.
  - A clear and consistent design system.
- **Base classes**
  - `BaseActivity`: session timeout handling, navigation event collection and root UI setup.
  - `BaseViewModel`: MVI pattern with `UiState`, `Event` and `Effect`, plus `CoroutinesUseCaseRunner` for background work.

## Creating a New Feature

Every feature is a pair of `data` and `presentation` modules built on the core modules.

1. **Create the modules**: `:<feature>:data` and `:<feature>:presentation`, and register them in `settings.gradle.kts`:

   ```kotlin
   include(":<feature>:data")
   include(":<feature>:presentation")
   ```

   Apply the feature convention plugins in each module's `build.gradle.kts` (`feature.data` and `feature.presentation`). They configure Android, Kotlin, Hilt and the core dependencies for you.

2. **Define a navigation graph** in the `presentation` module by implementing `INavigationGraph`:

   ```kotlin
   @Serializable
   class <Feature>NavGraph @Inject constructor() : INavigationGraph {
       override val startingDestination: KClass<out INavigationDestination>
           get() = <Feature>ScreenNavDestination::class
       // ...
   }
   ```

3. **Define destinations and entries**
   - Create destinations in the shared `:common:navigation` module at `common/navigation/src/main/java/com/common/navigation/destination/<feature>/<Screen>ScreenNavDestination.kt` (package `com.common.navigation.destination.<feature>`). They are public `@Serializable` objects or data classes extending `INavigationDestination()`. The `feature.presentation` plugin already adds `implementation(project(":common:navigation"))` to your presentation module, so do not declare it yourself (`AppPlugin` adds it to `:app` too).

     ```kotlin
     @Serializable
     data object <Feature>ScreenNavDestination : INavigationDestination()
     ```

     To open another feature's screen, import its `<Screen>ScreenNavDestination` and call `navigator.get().goToDestination(...)`.
   - Create classes implementing `INavigationGraphScreenEntry` (or the dialog / bottom-sheet variants) for each screen.

4. **Create the ViewModel and the view**
   - Implement your ViewModel by extending `BaseViewModel`.
   - Create the Composable screen that observes the ViewModel state.

5. **Dependency injection**: bind your navigation graphs and entries into the Dagger graph with Hilt, using a Hilt module in the `presentation` module.

6. **Add the feature to `:app`**: declare the presentation module as a dependency in `app/build.gradle.kts`:

   ```kotlin
   dependencies {
       implementation(project(":<feature>:presentation"))
   }
   ```

## Build Configuration

Build settings are centralized in the `:core:dependencies` included build, so individual modules stay minimal.

| Setting | Value |
|---|---|
| `minSdk` | 26 |
| `targetSdk` / `compileSdk` | 37 |
| Java / Kotlin JVM target | 21 |
| Version catalog | `coreLibs` (`core/dependencies/gradle/libs.versions.toml`) |

Convention plugins: `app`, `core.data`, `core.presentation`, `feature.data` and `feature.presentation`. Repositories are declared only in `settings.gradle.kts` (`FAIL_ON_PROJECT_REPOS`).

## Code Quality and Testing

- **Static analysis**: Detekt with ktlint rules, configured in [`config/detekt/detekt.yml`](config/detekt/detekt.yml).
- **API documentation**: Dokka is applied through the `core.data` convention plugin.
- **Unit tests**: JUnit, MockK and Robolectric for JVM tests, Hilt testing for DI-aware tests and Coroutines test for suspending code.
- **Feature tests** are scenario-mapped: each test traces back to a requirement scenario of the feature spec.

## Documentation and AI-assisted Delivery

The repository ships with an optional workflow in which Claude Code agents deliver a complete feature under strict rules.

- [`openwiki/`](openwiki/index.md): a generated, searchable index of the codebase. Start at [`openwiki/quickstart.md`](openwiki/quickstart.md) to find the right page for networking, caching, navigation, forms, theming, testing and more. A scheduled GitHub Actions workflow refreshes it; do not edit generated pages by hand.
- [`openspec/`](openspec): spec-driven changes (`add-<feature>-feature`) with requirements, scenarios, design and role-tagged tasks.
- [`docs/orchestrator/`](docs/orchestrator): the pipeline design ([`proposal.md`](docs/orchestrator/proposal.md)), a step-by-step [`setup-guide.md`](docs/orchestrator/setup-guide.md) and an interactive [`pipeline.html`](docs/orchestrator/pipeline.html) overview.

The pipeline runs through the gates below. Each team has a builder agent and a read-only evaluator, and a human approves the plan and the final result.

| Phase | Team | Agents |
|---|---|---|
| 0 | Module | `feature-module`, `feature-module-eval` |
| 1 | Data | `feature-data`, `feature-data-test`, `feature-data-eval` |
| 2 | Presentation | `feature-presentation-viewmodel`, `feature-presentation-view`, `feature-presentation-analytics`, `feature-presentation-eval` |
| 3 | Testing | `feature-testing` |

Using these tools is optional: the project builds and runs without them.
