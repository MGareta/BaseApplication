---
type: how-to guide
title: Recipes for feature work on core/data
description: Step-by-step Kotlin recipes for building features on core/data. Covers adding Retrofit services and handling ApiResult, caching with repositoryPattern, analytics frameworks and events, AES/RSA encryption, session lifecycle and commands, permissions, activity results, downloads and files, environment switching, and notification permission and dismissal.
tags: [core-data, recipes, how-to, retrofit, cache, session, permissions]
sources:
  - id: openwiki-source-2d0c3fc71cb26ec23555ef3a
    resource: repo://.claude/agents/feature-coordinator.md
  - id: openwiki-source-ba85436cec977ac5f7cd8188
    resource: repo://.claude/agents/feature-data-test.md
  - id: openwiki-source-ff72d56f0466a98a50ae53ca
    resource: repo://.claude/agents/feature-data.md
  - id: openwiki-source-bf3f24298a1cbf4d07ff2872
    resource: repo://.claude/skills/wiki-context/SKILL.md
  - id: openwiki-source-e38439f5bc06bf873d6444ad
    resource: repo://app/src/main/java/com/baseapplication/MainActivity.kt
  - id: openwiki-source-dbb674f973430ce943e15269
    resource: repo://core/data/src/main/java/com/core/data/download/di/DownloadNetworkModule.kt
  - id: openwiki-source-005fb49b2efec112d26159fc
    resource: repo://core/data/src/main/java/com/core/data/network/di/builder/RetrofitBuilder.kt
  - id: openwiki-source-f657300c43b408062c1260b1
    resource: repo://core/data/src/main/java/com/core/data/utils/ActivityProvider.kt
  - id: openwiki-source-f9a7de034d8728b3e7dd333d
    resource: repo://core/data/src/testFixtures/java/com/core/data/BaseTest.kt
  - id: openwiki-source-ebbe359b8fc397d0e2f617b5
    resource: repo://core/dependencies/src/main/java/com/core/dependencies/plugins/FeatureDataPlugin.kt
  - id: openwiki-source-a8f6f071ebaabf16a777b580
    resource: repo://core/presentation/src/main/java/com/core/presentation/base/activity/BaseActivity.kt
  - id: openwiki-source-c55c28cd02b3694b7cc5ed20
    resource: repo://login/data/src/main/java/com/login/data/api/LoginApiService.kt
  - id: openwiki-source-623161631b21b411e490dc52
    resource: repo://login/data/src/main/java/com/login/data/di/LoginModule.kt
  - id: openwiki-source-bca8019d4930801ea2c9fd1b
    resource: repo://login/data/src/main/java/com/login/data/di/LoginNetworkModule.kt
  - id: openwiki-source-c6af2e92dca1bc3f08670dbe
    resource: repo://login/data/src/main/java/com/login/data/manager/LoginManagerImpl.kt
  - id: openwiki-source-5f1294fe084f0032a88a82c9
    resource: repo://scripts/orchestrator/eval-mechanical.py
generated: { by: "claude-code", at: "2026-11-16T11:21:00.506Z" }
verified:
  - by: openwiki/0.7.2
    at: 2026-11-16T11:21:00.506Z
---

# Recipes for feature work on core/data

Each recipe assumes a feature data module using the `feature.data` plugin (`FeatureDataPlugin`), which brings
`api(project(":core:data"))`, Hilt and KSP. The plugin also configures tests with `configureTests()` and adds Hilt's test compiler through `kspTest(...)`. All core managers are `@Singleton`s in `SingletonComponent`, so inject the interfaces.

> **Prerequisite for anything that needs an activity (permissions, activity results, file/notification permission
> prompts):** `ActivityProvider` tracks **only `FragmentActivity`** instances. In this repo `MainActivity : BaseActivity : ComponentActivity`,
> so `ActivityProvider.currentActivity` stays `null` and activity-based requests return `null`/`Denied` until the host activity
> extends `FragmentActivity` (or `AppCompatActivity`). The app must also extend `CoreApp` (it does: `App : CoreApp()`). See [ActivityProvider](utils.md).

---

## 1. Add a Retrofit API service and consume `ApiResult`

1. Define DTOs with `@Serializable` (kotlinx.serialization is exported by core/data).
2. Declare the service with `suspend` methods returning `ApiResult<T>`. The core call adapter wraps every outcome, so the call never throws.
3. Provide it with `RetrofitFactory` in a feature Hilt module.
4. Map `ApiResult` in the repository or use case, and hand it to `CoroutinesUseCaseRunner.withUseCaseScopeLazily` in presentation.

### Apply the feature data structure selectively

Treat the approved OpenSpec design's **Data API surface** as the component contract. Before implementation, mark each row `Required` with its planned paths and public types/signatures (and DTO fields), or `N/A` with a reason. Implement the Required components as designed; omit N/A components without empty or placeholder files. If a needed component is missing or incorrectly marked N/A, request a spec change rather than inventing an API or binding.

**Keep everything `internal` that can be.** In a feature data module only `I<Feature>Manager`, the `model/` domain classes (not `model/dto/`) and the `<Feature>Module` object are public; the API service, DTOs, the manager implementation and other Hilt modules (such as `<Feature>NetworkModule`) are `internal`, and so are the `@Provides` functions of the public module. Test-source Hilt replacement modules and helpers are `internal` too; only JUnit test classes stay public (see [Data testing](testing.md)).

| Component | Include when the design requires it |
|---|---|
| API service | A backend API is needed: `api/<Feature>ApiService.kt`, a Retrofit interface with `suspend` methods returning `ApiResult<T>`. |
| Network module | An API service is needed: `di/<Feature>NetworkModule.kt` provides it through `RetrofitFactory`. |
| Request/response DTOs | Each endpoint has a non-empty request or response body: add the corresponding `model/dto/<Operation>RequestDto.kt` or `<Operation>ResponseDto.kt`; omit the DTO for a direction with no payload. |
| App-facing models | Data crosses the data-layer boundary: define plain models under `model/` with fields the app needs, map response DTOs, and do not expose DTOs or mirror every wire field automatically. Omit models for void/no-content-only outcomes. |
| Manager interface and implementation | The feature exposes a data operation to presentation/app: add `manager/I<Feature>Manager.kt` and `manager/<Feature>ManagerImpl.kt`. |
| Manager Hilt module | A manager is required: add `di/<Feature>Module.kt` to provide its interface binding. |
| Repository/cache | A separate data-source or cache boundary is part of the design; neither a repository class nor directory is mandatory otherwise. Follow the [cache recipe](cache.md) when caching applies. |

The code examples below illustrate individual operations, not a feature-wide implementation or test template. Choose components and test cases from the current feature's approved OpenSpec design and the relevant `core` guidance.

```kotlin
@Serializable internal data class OrderDto(val id: String, val total: Double)
@Serializable internal data class OrdersDto(val items: List<OrderDto>)

internal interface OrdersApiService {
    @GET("orders") suspend fun getOrders(): ApiResult<OrdersDto>
    @GET("orders/{id}") suspend fun getOrder(@Path("id") id: String): ApiResult<OrderDto>
}

@Module @InstallIn(SingletonComponent::class)
internal object OrdersNetworkModule {
    @Singleton @Provides
    fun providesOrdersApiService(factory: RetrofitFactory, env: IEnvironmentManager): OrdersApiService =
        factory.create().build(
            kClass = OrdersApiService::class.java,
            environment = env.environment,
            interceptors = listOf(AuthInterceptor()),    // optional, per-service
        )
}

internal class OrdersRepository @Inject constructor(private val api: OrdersApiService) {
    suspend fun firstOrder(): ApiResult<OrderDto> =
        api.getOrders().map { orders ->
            orders.items.firstOrNull()?.let { api.getOrder(it.id) } ?: ApiResult.NoContent()
        }
}
```

Handle all four outcomes: `Success(data)`, `Error(httpCode, response)`, `Exception(exception)` and `NoContent` (check `NoContent` with `is`).
Details: [network](network.md).

## 2. Cache responses (TTL vs. immortal, memory vs. disk)

| Need | Scope | Config |
|---|---|---|
| Fast, per-process, cleared on kill | `CacheScope.MEMORY_CACHE` | |
| Survive restarts, encrypted | `CacheScope.DISK_CACHE` | |
| Refresh after N minutes | | `TimeToLiveCacheConfig(key, duration = 10.minutes)` |
| Keep until deleted | | `ImmortalCacheConfig(key)` |
| Force refresh | | either config with `bypassCache = true` |

```kotlin
class CatalogRepository @Inject constructor(
    private val api: CatalogApiService,
    private val cacheManager: ICacheManager,
) {
    suspend fun catalog(forceRefresh: Boolean = false): ApiResult<CatalogDto> {
        val config = TimeToLiveCacheConfig(key = "catalog", bypassCache = forceRefresh, duration = 15.minutes)

        if (!forceRefresh) {
            cacheManager.getData(CacheScope.DISK_CACHE, config.key, CatalogDto::class)
                .getOrNull()?.let { return ApiResult.Success(it) }
        }
        return api.getCatalog().onSuccess { cacheManager.cacheData(CacheScope.DISK_CACHE, config, it) }
    }
}
```

The manual form above is equivalent to `cacheManager.repositoryPattern(scope, config) { ... }`. Use `repositoryPattern` when the
source lambda doesn't need to call suspend functions: its `source` parameter is a non-suspend `crossinline` lambda (see [cache](cache.md)).
Rules: cache `@Serializable` classes (wrap lists), and remember that disk writes of an existing key can leave older encrypted entries behind, so call
`cacheManager.deleteData(CacheScope.DISK_CACHE, key)` before re-caching frequently changing disk data. Invalidate with `deleteData(scope = null, key)`,
`deleteScope(scope)` or `clearAll()` (for example on logout).

## 3. Add an analytics framework, events and views

**New event or view for Firebase:**
```kotlin
class CheckoutCompletedEvent(total: Double) : BaseFirebaseTrackEvent(
    key = "checkout_completed", data = mapOf("value" to total, "currency" to "EUR"),
)
class CheckoutView : BaseFirebaseTrackView(data = mapOf(FirebaseAnalytics.Param.SCREEN_NAME to "checkout"))

analyticsManager.trackView(AnalyticsFramework.FIREBASE, CheckoutView())
analyticsManager.trackEvent(AnalyticsFramework.FIREBASE, CheckoutCompletedEvent(42.0))
```
Always pair `FIREBASE` with `BaseFirebaseTrack*` subclasses. Other types crash with `ClassCastException`.

**New framework:** add an `AnalyticsFramework` entry, base models implementing `BaseAnalyticsView`/`BaseAnalyticsEvent`, an
`IAnalyticsFramework<V, E>` implementation, and an `@IntoMap @AnalyticsFrameworkKey(...)` provider in `AnalyticsFrameworkModule`
(with the same unchecked cast as Firebase). Then add a mock binding to `AnalyticsFrameworkModuleTest`. Call `analyticsManager.initialize()` once at start-up.
Details: [analytics](analytics.md).

## 4. Encrypt and decrypt data (AES vs. RSA)

| Goal | Call |
|---|---|
| Protect local data with a device-bound key | `securityManager.encryptData(CipherType.AES, data, publicKey = null)` / `decryptData(CipherType.AES, cipherText, null)` |
| Encrypt for a server that holds the private key | `encryptData(CipherType.RSA, data, publicKey = serverX509Base64)` |
| Decrypt with a provided PKCS#8 private key | `decryptData(CipherType.RSA, cipherText, publicKey = privateKeyPkcs8Base64)` (the parameter is named `publicKey`) |
| Device-bound RSA key pair | `encryptData(CipherType.RSA, data, null)` / `decryptData(CipherType.RSA, cipherText, null)` |

```kotlin
val token = securityManager.encryptData(CipherType.AES, data = rawToken, publicKey = null).getOrNull() ?: return
val plain = securityManager.decryptData(CipherType.AES, data = token, publicKey = null).getOrNull()
```
Keystore and cipher exceptions are thrown, not wrapped. Wrap calls in `runCatching` if inputs may be corrupt. RSA can only encrypt
small payloads (about 190 bytes for 2048-bit OAEP-SHA256). Don't use the AES overload with a caller-supplied key except for interoperability.
Details: [security](security.md).

## 5. Start, observe and invalidate a session

```kotlin
// After login succeeds
sessionManager.startSession(user = userId, token = accessToken)

// Read the current session (e.g. in an auth interceptor or repository)
val token: String? = sessionManager.getCurrentSession().getOrNull()?.token

// Logout
sessionManager.invalidateSession()
cacheManager.clearAll()   // optional: drop user data
```

**Reacting to `SessionCommand`.** Commands can't be sent by callers. They're emitted by the internal inactivity timer
(`Warning` about 3 s before timeout, `TimeOut` after 5 s). `BaseActivity` already collects them:
```kotlin
class MainActivity : BaseActivity() {
    override fun onSessionWarning() { showSessionWarningDialog() }
    override fun onSessionTimeout() {           // default: invalidateSession()
        super.onSessionTimeout()
        navigateToLogin()
    }
}
```
`BaseActivity.onUserInteraction()` calls `restartSessionTimeout()`. Call it yourself for non-touch activity (for example a successful API call).
The commands flow is backed by a single-consumer `Channel`, so don't collect it from more than one place. Details: [session](session.md).

## 6. Request permissions

```kotlin
when (permissionManager.requestPermission(Manifest.permission.CAMERA)) {
    PermissionStatus.Granted -> openCamera()
    PermissionStatus.ShowRationale -> showRationale()
    PermissionStatus.Denied -> offerAppSettings()
}
val result: Map<String, PermissionStatus> = permissionManager.requestPermissions(
    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
)
```
Declare the permission in the manifest. Use `hasPermission` for silent checks. See [permission](permission.md).

## 7. Launch for an activity result

```kotlin
val imageUri: Uri? = activityResultManager.getContent(mimeType = "image/*")
val picked: List<Uri> = activityResultManager.openMultipleDocuments(arrayOf("application/pdf"))
val saved: Boolean = activityResultManager.takePicture(destination = fileUri)

// Any contract
val result: ActivityResult? = activityResultManager.requestResult(
    contract = ActivityResultContracts.StartActivityForResult(),
    input = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)),
)
```
`null` means no activity was available or the user cancelled (contract-dependent). Run one request at a time. See [activity-result](activity-result.md).

## 8. Download a file and save it with FileManager

```kotlin
val result = downloadManager.requestDownload(
    fileName = "terms_v3",                         // also the local cache key
    url = "https://cdn.example.com/legal/terms_v3.pdf",
    headers = mapOf("Authorization" to "Bearer $token"),
)
result.onSuccess { file -> openWithViewer(file.uri, file.mimeType) }

// Save bytes you already have
fileManager.saveFile(data = bytes, fileName = "export_2026_10", mimeType = "application/pdf")
fileManager.findFile(fileName = "export_2026_10", mimeType = "application/pdf")   // Success / NoContent
```
A file with the same base name is returned without re-downloading, so version your file names. PDFs go to `filesDir/documents`.
See [download](download.md) and [file](file.md).

## 9. Switch environments

```kotlin
@HiltAndroidApp
class App : CoreApp() {
    @Inject lateinit var environmentManager: IEnvironmentManager
    override fun onCreate() {
        super.onCreate()
        environmentManager.environment = when (BuildConfig.FLAVOR) {
            "uat" -> Environment.UAT()
            "prod" -> Environment.PROD()
            else -> Environment.DEV()
        }
    }
}
```
Set it **before** anything injects `OkHttpClient` or API services. They capture the URL and pinning choice once. PROD pinning
currently has no certificates or whitelist configured, so PROD TLS calls fail until `NetworkTrustManager` is populated. See [environment](environment.md) and [network](network.md).

## 10. Notifications: permission and dismissal

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
    notificationsManager.areDeviceNotificationsEnabled().getOrNull() != true
) {
    notificationsManager.enableDeviceNotifications()          // ApiResult<PermissionStatus>
}

// Posting is NOT provided by core/data; do it yourself with a tag:
NotificationManagerCompat.from(context).notify("order_42", 42, notification)
// Later, dismiss by tag:
notificationsManager.erasedNotificationFromStatusBar(id = "order_42")
```
See [notifications](notifications.md).

---

## Writing the tests for any of the above

Extend the shared `com.core.data.BaseTest` (Robolectric + Hilt), annotate with `@HiltAndroidTest`, replace collaborators with
`@UninstallModules(XModule::class)` + `@BindValue` mocks, or `@TestInstallIn` modules, and wrap bodies in `test { }`.
`FeatureDataPlugin` adds `testFixtures(project(":core:data"))`, so feature-data tests inherit this fixture and should not copy the base class.
See [testing](testing.md).

## Related pages

- [Overview](overview.md) · [Network](network.md) · [Cache](cache.md) · [Session](session.md) · [Testing](testing.md)
