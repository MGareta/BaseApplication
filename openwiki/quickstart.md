---
type: quickstart
title: Quickstart
description: Task-routing map for agents building features in BaseApplication. Gives a short orientation of the Gradle modules and tells you which core/data or core/presentation wiki page to read for networking, caching, security, session, permissions, files, analytics, screens, navigation, components, forms, theming and testing.
tags: [quickstart, routing, core-data, core-presentation, architecture]
sources:
  - id: openwiki-source-03fcc4056003dc2f567db8f7
    resource: repo://app/src/main/java/com/baseapplication/App.kt
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-1f78a6fb10e2b39879415b1c
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeaturePresentationPlugin.kt
  - id: openwiki-source-f96123c650a78c26f62af83d
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/factory/NavigationGraphFactoryImpl.kt
  - id: openwiki-source-e8b13a3752a96095685bba51
    resource: repo://core/presentation/src/main/java/com/core/presentation/navigation3/model/entry/decorators/viewModel/NavigationGraphViewModelStoreOwner.kt
  - id: openwiki-source-dc2f4215d060ab5729f6cb69
    resource: repo://demo/presentation/src/main/java/com/demo/presentation/di/DemoScreenModule.kt
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "claude-code", at: "2026-10-26T15:48:49.783Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T15:48:49.783Z
---

# Quickstart

## Repository orientation

`settings.gradle.kts` includes these modules:

| Module | Role |
|---|---|
| `:app` | Application shell: `App : CoreApp()` and `MainActivity : BaseActivity()`. |
| `:core:data` | Shared data and platform services (network, cache, security, session, permissions, files, ...). Documented under `core-data/`. |
| `:core:presentation` | Compose UI base: `CoreApp`, `BaseActivity`, MVI `BaseViewModel`, Navigation3 integration, design system and components. Depends on `:core:data`. Documented under `core-presentation/`. |
| `core/dependencies` | Included build with convention plugins (`core.data`, `core.presentation`, `feature.data`, `feature.presentation`, `app`) and the `coreLibs` version catalog. |
| `:demo:data`, `:demo:presentation` | Sample feature modules (`demo:data` currently has no main sources). |

Feature data modules apply the `feature.data` plugin, which adds `api(project(":core:data"))`. Inject the core
`I*Manager` interfaces with Hilt. Every core manager is a `@Singleton` in `SingletonComponent`.
Feature presentation modules apply `feature.presentation` (`implementation(project(":core:presentation"))`) and contribute Navigation3 graphs and entries
in `ActivityRetainedComponent`.

## Where to read for each task

### Building screens (core/presentation)

| I need to... | Read |
|---|---|
| Understand the UI module, its conventions, build and dependencies | [core-presentation/overview](core-presentation/overview.md) |
| Build a new screen end to end (destination, contract, ViewModel, entry, graph, Hilt) | [core-presentation/recipes](core-presentation/recipes.md) |
| Navigate, add dialogs or bottom-sheet destinations, share ViewModels in a flow | [core-presentation/navigation3](core-presentation/navigation3.md) |
| Understand the host activity, edge-to-edge, bottom nav and session hooks | [core-presentation/app-and-activity](core-presentation/app-and-activity.md) |
| Write a ViewModel (state, events, effects, `UiResult`) | [core-presentation/base-architecture](core-presentation/base-architecture.md) |
| Call use cases and map `ApiResult` to `UiResult`, or paging | [core-presentation/coroutines](core-presentation/coroutines.md) |
| Use colours, spacing and typography correctly | [core-presentation/theme](core-presentation/theme.md), [core-presentation/theme-tokens](core-presentation/theme-tokens.md) |
| Buttons, hyperlinks, badges, alerts, amounts | [core-presentation/components-actions](core-presentation/components-actions.md) |
| Top bar, collapsing toolbar, bottom nav bar, tabs, stepper | [core-presentation/components-navigation](core-presentation/components-navigation.md) |
| Status bottom sheet, grids, paging model | [core-presentation/components-layout](core-presentation/components-layout.md) |
| Form state and validation | [core-presentation/forms](core-presentation/forms.md) |
| Text, password, amount, date, OTP, checkbox, chip, toggle and multi-select fields | [core-presentation/form-fields](core-presentation/form-fields.md) |
| Accessibility focus, indications, conditional modifiers, keyboard state | [core-presentation/ui-utils](core-presentation/ui-utils.md) |
| Test ViewModels and components | [core-presentation/testing](core-presentation/testing.md) |
| Recognise or migrate the old Navigation Compose code | [core-presentation/navigation-legacy](core-presentation/navigation-legacy.md) |

### Data and platform services (core/data)

| I need to... | Read |
|---|---|
| Understand the module, its conventions, build and dependencies | [core-data/overview](core-data/overview.md) |
| Call a REST API, declare a Retrofit service, handle `ApiResult` | [core-data/network](core-data/network.md) |
| Choose or switch DEV/UAT/PROD, base URLs, SSL pinning | [core-data/environment](core-data/environment.md) |
| Cache responses in memory or on disk (TTL, `repositoryPattern`) | [core-data/cache](core-data/cache.md) |
| Encrypt or decrypt strings (AES/RSA, Keystore) | [core-data/security](core-data/security.md) |
| Log in or out, read the token, handle inactivity timeouts | [core-data/session](core-data/session.md) |
| Ask for runtime permissions | [core-data/permission](core-data/permission.md) |
| Pick files, take photos, launch any intent for a result | [core-data/activity-result](core-data/activity-result.md) |
| Access the current activity or app context from the data layer | [core-data/utils](core-data/utils.md) |
| Save or find files and get `content://` URIs | [core-data/file](core-data/file.md) |
| Download a remote file | [core-data/download](core-data/download.md) |
| Track screens or events, add an analytics SDK | [core-data/analytics](core-data/analytics.md) |
| Notification permission or dismiss a notification | [core-data/notifications](core-data/notifications.md) |
| Copy-paste working code for common feature tasks | [core-data/recipes](core-data/recipes.md) |
| Write unit tests (Robolectric + Hilt + MockK) | [core-data/testing](core-data/testing.md) |

## Known pitfalls to check before building

1. `ActivityProvider` tracks only `FragmentActivity`, but `BaseActivity` extends `ComponentActivity`. Activity results and permission
   requests don't reach the UI until this is aligned ([utils](core-data/utils.md)).
2. PROD enables SSL pinning with an empty certificate set and an empty host whitelist, so PROD TLS calls are rejected ([network](core-data/network.md)).
3. The session inactivity timeout is hard-coded to 5 s ([session](core-data/session.md)).
4. AES and RSA share the Keystore alias `"alias"`. Device-bound RSA would replace the AES key used by the disk cache ([security](core-data/security.md)).
5. `ApiResult.NoContent` isn't a data class, so compare it with `is` ([network](core-data/network.md)).
6. Navigation3 creates ViewModels with `viewModel(modelClass)` under a graph store owner without a Hilt factory, so ViewModels with injected dependencies may fail.
   It also never calls `loadInitData()` ([navigation3](core-presentation/navigation3.md)).
7. `MainActivity.onSessionWarning()`/`onSessionTimeout()` are `TODO()`, and bottom-nav tab taps aren't wired ([app-and-activity](core-presentation/app-and-activity.md)).
8. `CheckboxComponent`/`ToggleComponent` don't change state unless you pass `onValueChange`. Date fields don't display validation errors. `CurrencyType.Symbol` crashes
   `AmountFieldComponent` ([form-fields](core-presentation/form-fields.md)).
9. Text fields only show errors when the value is non-blank, and some validators NPE on `null` values, so put `Required` first ([forms](core-presentation/forms.md)).
10. `ContainerComponent`, `PagingComponent` and the list Loading/Error helpers are commented out ([components-layout](core-presentation/components-layout.md)).
11. `Theme` forces `fontScale = 1f`, which ignores the user's font size ([theme](core-presentation/theme.md)).
12. Import from `com.core.presentation.navigation3`, not the legacy `com.core.presentation.navigation` (same type names).
