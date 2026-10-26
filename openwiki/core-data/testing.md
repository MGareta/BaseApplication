---
type: testing guide
title: Testing core/data
description: How core/data is unit-tested with Robolectric, Hilt testing and MockK. Covers BaseTest, Gradle test configuration, the android.util.Base64 JVM shadow, every test helper, what each test class covers, how modules are replaced, and a template for testing a new manager.
tags: [core-data, testing, robolectric, hilt, mockk, coroutines]
verified:
  - by: openwiki/0.7.2
    at: 2026-10-26T12:11:14.549Z
sources:
  - id: openwiki-source-13737e88f2a6a241f01d8d16
    resource: repo://core/data/src/test/java/android/util/Base64.kt
  - id: openwiki-source-d46c6517713720582371074b
    resource: repo://core/data/src/test/java/com/core/data/activityProvider/ActivityProviderTest.kt
  - id: openwiki-source-ca352994d2ef5b7d33bfbfd9
    resource: repo://core/data/src/test/java/com/core/data/analytics/di/AnalyticsFrameworkModuleTest.kt
  - id: openwiki-source-8a48a6b773cfb4e26c406cac
    resource: repo://core/data/src/test/java/com/core/data/BaseTest.kt
  - id: openwiki-source-eee561e0d5bee780221fa873
    resource: repo://core/data/src/test/java/com/core/data/download/di/DownloadNetworkModuleTest.kt
  - id: openwiki-source-bb634ffb0abb10ecf525150c
    resource: repo://core/data/src/test/java/com/core/data/network/utils/ImmediateCall.kt
  - id: openwiki-source-f666f3373d6b392197d2fbf6
    resource: repo://core/data/src/test/java/com/core/data/network/utils/Utils.kt
  - id: openwiki-source-fc86a44df0595077f3cd60f7
    resource: repo://core/data/src/test/java/com/core/data/utils/Utils.kt
  - id: openwiki-source-bbb0f73ef5c40bd97bd940fa
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/utils/Extensions.kt
generated: { by: "claude-code", at: "2026-10-26T12:11:14.549Z" }
---

# Testing core/data

All tests are **JVM unit tests** in `core/data/src/test`. Instrumented tests were removed (there's no `androidTest` source set,
even though `build.gradle.kts` still names a `com.core.data.DefaultTestRunner` that doesn't exist). Run them with:

```bash
./gradlew :core:data:testDebugUnitTest
./gradlew :core:data:testDebugUnitTest --tests "com.core.data.cache.CacheManagerTest"
```

## Tooling and Gradle setup

From `CoreDataPlugin` and `core/dependencies/.../utils/Extensions.kt`:
- `testFixturesApi(bundle "test-unit")` = `kotlinx-coroutines-test` (1.11.0), `robolectric` (4.17), `hilt-android-testing`, `mockk-android` (1.14.11).
  Unit tests see the module's test-fixtures dependencies, which is how `src/test` gets them.
- `kspTest(hilt-compiler)` generates Hilt test components.
- `configureTests()`: `unitTests.isIncludeAndroidResources = true` (needed for the manifest `FileProvider` and `provider_paths.xml`) and
  JVM args `--add-opens` for `java.lang`, `java.util`, `java.io`, `java.net`, `java.security`, `java.text`, `jdk.internal.access`,
  `java.awt.font` and `jdk.compiler/com.sun.tools.javac.api` (Robolectric/MockK reflection on JDK 21).
- Feature modules get `testImplementation(testFixtures(project(":core:data")))` through `FeatureDataPlugin`. That provides the libraries but **not**
  `BaseTest` or the helpers, which live in `src/test` and not `src/testFixtures`.

## `BaseTest` (`com.core.data.BaseTest`)

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
abstract class BaseTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope = TestScope(testDispatcher + Job())
    @get:Rule val hiltRule = HiltAndroidRule(this)
    @ApplicationContext @Inject lateinit var context: Context

    @Before fun setup() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()
        ActivityProvider.init(context as Application)
        initDependencies()
    }
    @After fun cleanup() { Dispatchers.resetMain() }

    fun test(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest { block() }
    abstract fun initDependencies()
}
```

Contract for subclasses:
- Annotate with `@HiltAndroidTest`, and `@Inject` the real objects under test (`lateinit var`).
- Override `initDependencies()` for per-test stubbing (it runs **after** injection and `ActivityProvider.init`). Return `Unit` if it's not needed.
- Write tests as `fun x() = test { ... }`. Coroutines run eagerly on `UnconfinedTestDispatcher`, which is also installed as `Dispatchers.Main`.
- `ActivityProvider` is initialized with the `HiltTestApplication`, so `applicationContext` works, and activities built with
  `Robolectric.buildActivity(FragmentActivity::class.java)` become `currentActivity`.

## Replacing dependencies

Three patterns are used:

| Pattern | Example | Effect |
|---|---|---|
| `@UninstallModules(X::class)` + `@BindValue @JvmField val dep: IDep = mockk()` | `CacheManagerTest` (SecurityModule), `PermissionManagerTest` (ActivityResultModule), `NotificationsManagerTest` (PermissionModule), `SSLPinningTest` (EnvironmentModule) | Per-test-class replacement of one module's bindings with a mock. |
| `@TestInstallIn(components = [SingletonComponent::class], replaces = [X::class])` module in `src/test` | `AnalyticsFrameworkModuleTest`, `DownloadNetworkModuleTest` | Global replacement for **all** tests in the module (Firebase and network are never real in tests). |
| Construct the impl directly | `SecurityManagerTest` builds `SecurityManagerImpl(mapOf(AES to mockk()))` | Unit-level checks without Hilt. |

Robolectric helpers in use: `shadowOf(context as Application).grantPermissions(...)`, `Robolectric.buildActivity(...).setup()`,
`shadowOf(getMainLooper()).idleFor(Duration)` (session timer), and `@Config(sdk = [...])` per class or method.

## Test support files

| File | Purpose |
|---|---|
| `android/util/Base64.kt` | `object android.util.Base64` with `@JvmStatic encodeToString(ByteArray?, Int)` / `decode(String?, Int)` backed by `java.util.Base64` (flags ignored). It shadows the Android class on the test classpath so crypto code produces plain Base64 on the JVM. |
| `com/core/data/BaseTest.kt` | Base class (above). |
| `com/core/data/utils/Utils.kt` | `internal fun clearFileProviderCache()` clears `FileProvider`'s private static `sCache` through reflection, so each test re-reads provider paths and authorities. Call it in `initDependencies()` for file/download tests. |
| `com/core/data/network/utils/ImmediateCall.kt` | `internal class ImmediateCall<T : Any>(response: Response<T>? = null, failure: Throwable? = null) : Call<T>`. A synchronous fake Retrofit `Call`: `enqueue` immediately calls `onFailure(failure)` or `onResponse(response)`, `execute` returns or throws, `request()` is `https://example.test/`, and it tracks the executed/canceled flags. |
| `com/core/data/network/utils/NetworkService.kt` | `internal interface NetworkService { fun result(): Call<ApiResult<String>>; fun raw(): Call<String> }`. Its reflective return types are used to test `NetworkResultCallAdapterFactory` type filtering. |
| `com/core/data/network/utils/Utils.kt` | `internal fun <T : Any> executeNetworkResultCall(proxy: Call<T>): ApiResult<T>` wraps the proxy in `NetworkResultCall`, enqueues it, and returns the delivered `ApiResult` (it throws `AssertionError` if `onFailure` is ever called). |
| `com/core/data/network/api/NetworkApiService.kt` | `internal interface NetworkApiService { @GET("response") fun response(): Call<ResponseBody> }`, a minimal service to test `RetrofitFactory` URL resolution and interceptors. |
| `com/core/data/network/model/NetworkPayload.kt` | `@Serializable internal data class NetworkPayload(val value: String)` for converter tests (`ignoreUnknownKeys`). |
| `com/core/data/analytics/model/FirebaseAnalyticsEventTest.kt` | `internal data class FirebaseAnalyticsEventTest(name) : BaseFirebaseTrackEvent(key = name, data = emptyMap())`. |
| `com/core/data/analytics/model/FirebaseAnalyticsViewTest.kt` | `internal data class FirebaseAnalyticsViewTest(name) : BaseFirebaseTrackView(mapOf(SCREEN_NAME to name))`. |
| `com/core/data/analytics/di/AnalyticsFrameworkModuleTest.kt` | `@TestInstallIn` replacing `AnalyticsFrameworkModule` with a relaxed mock framework under `FIREBASE`. |
| `com/core/data/download/di/DownloadNetworkModuleTest.kt` | `@TestInstallIn` replacing `DownloadNetworkModule` with a `mockk()` `DownloadApiService`. |

## Test classes and coverage

| Test class | Package under test | Covers |
|---|---|---|
| `activityProvider/ActivityProviderTest` | `utils` | `init` stores the context. Tracks a `FragmentActivity` (`currentActivity`, `activityFlow.first()`) and drops it after destroy. Ignores plain `Activity`. |
| `activityResult/ActivityResultTest` | `activityResult` | Every extension in `utils/Extensions.kt` against a `mockk<IActivityResultManager>()`: contract types, inputs and null defaults. **`ActivityResultManagerImpl` itself is untested.** |
| `analytics/AnalyticsManagerTest` | `analytics` | `initialize` success and exception passthrough, `trackView`/`trackEvent` delegation. |
| `cache/CacheManagerTest` | `cache` | Both scopes: put/get, delete by scope or all, `deleteScope`, `clearAll`, TTL expiry, `repositoryPattern` hit/miss/bypass. Uses an identity `ISecurityManager`, so real encryption isn't exercised. |
| `download/DownloadManagerTest` | `download` | Existing file shortcut, download + save with the URL extension, fallback extension, failure propagation. |
| `environment/EnvironmentManagerTest` | `environment` | Default `DEV`, set and get for DEV, UAT and PROD. |
| `file/FileManagerTest` | `file` | Save a PDF (extension and contents), find a saved file, `NoContent` when missing. |
| `network/ExtensionsTest` | `network.utils` | `onSuccess`, `onNoContent`, `onError`, `onException`, `map` (including exception wrapping and passthrough), `getOrElse`, `getOrNull`, `getOrThrow`. |
| `network/NetworkTest` | `network` | OkHttp config (BODY logging, 30 s timeouts), trust-all outside PROD, trust managers, Retrofit adapter and converter, `RetrofitFactory` with and without interceptors, `safeApiCall`, `NetworkResultCall`, adapter factory filtering. |
| `network/SSLPinningTest` | `network.ssl` | PROD (`@BindValue` env) enables pinning, and the hostname verifier rejects non-whitelisted hosts. |
| `notifications/NotificationsManagerTest` | `notifications` | Request result passthrough, enabled-state mirroring on API 33, `true` on API 32, cancel by tag. |
| `permission/PermissionManagerTest` | `permission` | `hasPermission`, single and multi requests → `Granted`/`Denied`/`ShowRationale`, and the null-result fallback. |
| `security/SecurityManagerTest` | `security` | Keyed AES and RSA round-trips, device-bound paths through a mocked `ICryptographyType`. |
| `session/SessionManagerTest` | `session` | Initial state, start/invalidate caching, `Warning`→`TimeOut` timing with a looper idle, restart semantics. |

Known gaps: `ActivityResultManagerImpl` internals, AndroidKeyStore paths (not available under Robolectric), real encrypted disk-cache
behaviour, `FirebaseAnalyticsFramework` itself, and `CacheMemoryScope` sizing.

## Template: testing a new manager

```kotlin
package com.core.data.mything

@HiltAndroidTest
@UninstallModules(PermissionModule::class)              // only if you need to fake a collaborator
class MyThingManagerTest : BaseTest() {

    @BindValue @JvmField
    val permissionManager: IPermissionManager = mockk()

    @Inject lateinit var myThingManager: IMyThingManager  // real binding from MyThingModule

    override fun initDependencies() {
        every { permissionManager.hasPermission(any()) } returns true
        coEvery { permissionManager.requestPermission(any()) } returns PermissionStatus.Granted
    }

    @Test
    fun returnsSuccessWhenPermissionGranted() = test {
        val result = myThingManager.doWork()
        assertTrue(result is ApiResult.Success)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S_V2])
    fun behavesDifferentlyBeforeAndroid13() = test { /* ... */ }
}
```

Checklist:
1. Put the test in the matching package under `src/test/java/com/core/data/<package>/`.
2. Prefer the real Hilt graph and fake only the edges you need. Use `@TestInstallIn` only when every test must avoid the real binding
   (external SDKs, the network).
3. Compare `ApiResult.NoContent` with `is`, never `assertEquals`.
4. For file-based code, grant storage permissions with `shadowOf(context as Application)` and call `clearFileProviderCache()`.
5. For timer or looper code, advance `shadowOf(Looper.getMainLooper()).idleFor(...)`.

## Related pages

- [Overview](overview.md) (build setup) · [Network](network.md) · [Recipes](recipes.md)
