---
type: quickstart
title: Quickstart
description: Task-routing map for agents building features in BaseApplication. Gives a short orientation of the Gradle modules and tells you which core/data wiki page to read for networking, caching, security, session, permissions, files, downloads, analytics, activity results, notifications, environments and testing.
tags: [quickstart, routing, core-data, architecture]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T12:11:14.549Z
sources:
  - id: openwiki-source-03fcc4056003dc2f567db8f7
    resource: repo://app/src/main/java/com/baseapplication/App.kt
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-e620d7484b72a53c7fa812cd
    resource: repo://settings.gradle.kts
generated: { by: "claude-code", at: "2026-10-26T12:11:14.549Z" }
---

# Quickstart

## Repository orientation

`settings.gradle.kts` includes these modules:

| Module | Role |
|---|---|
| `:app` | Application shell: `App : CoreApp()` and `MainActivity : BaseActivity()`. |
| `:core:data` | Shared data and platform services (network, cache, security, session, permissions, files, ...). **This wiki documents it.** |
| `:core:presentation` | Compose UI base: `CoreApp`, `BaseActivity`, `CoroutinesUseCaseRunner`, components. Depends on `:core:data`. |
| `core/dependencies` | Included build with convention plugins (`core.data`, `core.presentation`, `feature.data`, `feature.presentation`, `app`) and the `coreLibs` version catalog. |
| `:demo:data`, `:demo:presentation` | Sample feature modules (`demo:data` currently has no main sources). |

Feature data modules apply the `feature.data` plugin, which adds `api(project(":core:data"))`. Inject the core
`I*Manager` interfaces with Hilt. Every core manager is a `@Singleton` in `SingletonComponent`.

## Where to read for each task

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
