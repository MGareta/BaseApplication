# BaseApplication

BaseApplication is a comprehensive, modular Android application template built with Kotlin and Jetpack Compose. It provides a robust and scalable foundation for modern Android development, incorporating best practices and a rich set of pre-built core features. This repository is designed to accelerate development by providing a clean architecture and reusable components for common application needs.

## Architecture

The project follows a multi-module clean architecture to promote separation of concerns, scalability, and maintainability.

-   **`app`**: The main application module responsible for assembling all the core and feature modules.
-   **`core`**: Contains the foundational logic and UI components shared across the entire application.
    -   **`core-data`**: Manages all data-related operations, including networking, caching, security, file management, and session handling.
    -   **`core-presentation`**: Provides base classes for UI, a custom navigation framework, a complete theming system, and a library of reusable Jetpack Compose components.
    -   **`core-dependencies`**: A convention plugin module that centralizes an manages dependency versions across the project, ensuring consistency.
-   **`demo`**: A sample feature module that demonstrates how to implement a new feature using the components and architecture provided by the `core` modules.

## Key Features

This base application comes packed with a wide range of ready-to-use managers and components to handle common development tasks.

### Core Data (`core-data`)

-   **Networking**:
    -   Built with Retrofit and OkHttp.
    -   Custom `ApiResult` sealed interface (`Success`, `Error`, `Exception`, `NoContent`) for robust and predictable response handling.
    -   `NetworkResultCallAdapterFactory` to seamlessly integrate `ApiResult` with Retrofit.
    -   Environment manager (`IEnvironmentManager`) to easily switch between `DEV`, `UAT`, and `PROD` API endpoints.
    -   Support for SSL Pinning for enhanced security in production environments.
-   **Caching**:
    -   A flexible `ICacheManager` supporting multiple cache scopes.
    -   `MemoryCacheScope`: An in-memory LRU cache for fast access to frequently needed data.
    -   `DiskCacheScope`: A persistent on-disk cache with AES encryption for sensitive data.
    -   Supports Time-To-Live (TTL) and immortal cache configurations.
    -   Includes a `repositoryPattern` utility to simplify the cache-then-network data fetching strategy.
-   **Security**:
    -   `ISecurityManager` providing both AES and RSA encryption/decryption.
    -   Leverages the Android KeyStore for secure key management.
-   **Session Management**:
    -   `ISessionManager` to handle user session state (`Active`, `Inactive`).
    -   Automatic session timeout with a warning mechanism via `SessionCommand` Flow.
    -   Securely caches session data in memory.
-   **File & Download Management**:
    -   `IFileManager` to save and find files in the app's private storage, handling permissions automatically.
    -   `IDownloadManager` to download and save files from a URL.
-   **Permissions & Activity Results**:
    -   `IPermissionManager` to abstract away the complexity of requesting runtime permissions.
    -   `IActivityResultManager` for a modern, coroutine-based approach to handling results from activities.
-   **Analytics & Notifications**:
    -   `IAnalyticsManager` with a pluggable framework design (Firebase included).
    -   `INotificationsManager` to manage push notification permissions and interactions.

### Core Presentation (`core-presentation`)

-   **Type-Safe Navigation**: A custom navigation framework built on Jetpack Navigation Compose.
    -   Define navigation graphs using `INavigationGraph`.
    -   Create screen, dialog, or bottom sheet entries with `INavigationGraphEntry`.
    -   Navigate with type-safe destinations using `INavigationManager`.
-   **Jetpack Compose UI Toolkit**: A rich library of customizable and reusable components.
    -   **CTAs**: `BlockCta`, `IconCta`, and `LinkCta` with primary, secondary, and tertiary styles.
    -   **Forms**: A powerful form validation system with `FormState`, `TextFieldState`, `SelectFieldState`, and more. Includes a collection of built-in `Validators` (Email, Required, Min/Max length, etc.).
    -   **Lists**: `PagingComponent` for efficiently displaying paginated data and `NonLazyGridComponent` for simple grids.
    -   **Components**: `TabComponent`, `TagComponent`, `TileComponent`, `BottomNavBarComponent`, and more.
-   **Theming**:
    -   A centralized `Theme` with strongly-typed `ColorsPalette`, `Dimensions`, and `Typographies`.
    -   Full support for light and dark modes.
    -   Defines a clear and consistent design system.
-   **Base Classes**:
    -   `BaseActivity`: Handles session timeout logic, navigation event collection, and sets up the root UI.
    -   `BaseViewModel`: Implements the MVI pattern with `UiState`, `Event`, and `Effect`. Includes `CoroutinesUseCaseRunner` for managing background tasks.

## Getting Started

### Prerequisites

-   Android Studio
-   JDK 21

### Creating a New Feature

To add a new feature, you can follow the structure of the `demo` module:

1.  **Create new modules**: For example, `feature_login/data` and `feature_login/presentation`.
2.  **Define a Navigation Graph**: In the `presentation` module, create an `INavigationGraph` implementation for your feature.
    ```kotlin
    @Serializable
    class LoginNavGraph @Inject constructor() : INavigationGraph {
        override val startingDestination: KClass<out INavigationDestination>
            get() = LoginNavDestination::class
        // ...
    }
    ```
3.  **Define Destinations and Entries**:
    -   Create serializable data classes that implement `INavigationDestination`.
    -   Create classes that implement `INavigationGraphScreenEntry` (or Dialog/BottomSheet) for each screen.
4.  **Create ViewModel and View**:
    -   Implement your `ViewModel` by extending `BaseViewModel`.
    -   Create your Composable `Screen`, which will observe the ViewModel's state.
5.  **Dependency Injection**: Use Hilt to provide your navigation graphs and entries by binding them into the Dagger graph, similar to `DemoScreenModule`.
6.  **Add to `app` module**: Add your new feature's presentation module as a dependency in the `app/build.gradle.kts` file.
