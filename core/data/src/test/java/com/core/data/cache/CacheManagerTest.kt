package com.core.data.cache

import com.core.data.HiltRobolectricBaseTest
import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ImmortalCacheConfig
import com.core.data.cache.model.config.TimeToLiveCacheConfig
import com.core.data.cache.utils.repositoryPattern
import com.core.data.network.model.ApiResult
import com.core.data.security.di.SecurityModule
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.model.CipherType
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltAndroidTest
@UninstallModules(SecurityModule::class)
class CacheManagerTest : HiltRobolectricBaseTest() {

    @BindValue
    @JvmField
    val securityManager: ISecurityManager = mockk()

    @Inject
    lateinit var cacheManager: ICacheManager

    private val uniqueKey: String = "cache_${UUID.randomUUID()}"

    override fun initDependencies() {
        coEvery {
            securityManager.encryptData(
                type = CipherType.AES,
                data = any(),
                publicKey = null
            )
        } coAnswers {
            ApiResult.Success(data = secondArg())
        }

        coEvery {
            securityManager.decryptData(
                type = CipherType.AES,
                data = any(),
                publicKey = null
            )
        } coAnswers {
            ApiResult.Success(data = secondArg())
        }
    }

    @Test
    fun cachesAndRetrievesDataFromMemoryAndDiskScopes() = test {
        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            assertTrue(
                cacheManager.getData(
                    scope = scope,
                    key = uniqueKey,
                    kClass = String::class
                ) is ApiResult.NoContent
            )

            cacheManager.cacheData(
                scope = scope,
                config = ImmortalCacheConfig(key = uniqueKey),
                data = "cached value"
            )

            assertEquals(
                ApiResult.Success(data = "cached value"),
                cacheManager.getData(
                    scope = scope,
                    key = uniqueKey,
                    kClass = String::class
                )
            )
        }
    }

    @Test
    fun deleteDataCanTargetOneScopeOrAllScopes() = test {
        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            cacheManager.cacheData(
                scope = scope,
                config = ImmortalCacheConfig(key = uniqueKey),
                data = "cached value"
            )
        }

        cacheManager.deleteData(
            scope = CacheScope.MEMORY_CACHE,
            key = uniqueKey
        )

        assertTrue(
            cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = uniqueKey,
                kClass = String::class
            ) is ApiResult.NoContent
        )

        assertEquals(
            ApiResult.Success(data = "cached value"),
            cacheManager.getData(
                scope = CacheScope.DISK_CACHE,
                key = uniqueKey,
                kClass = String::class
            )
        )

        cacheManager.deleteData(
            scope = null,
            key = uniqueKey
        )

        assertTrue(
            cacheManager.getData(
                scope = CacheScope.DISK_CACHE,
                key = uniqueKey,
                kClass = String::class
            ) is ApiResult.NoContent
        )
    }

    @Test
    fun deleteScopeClearsOnlyTheSelectedScope() = test {
        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            cacheManager.cacheData(
                scope = scope,
                config = ImmortalCacheConfig(key = uniqueKey),
                data = "cached value"
            )
        }

        cacheManager.deleteScope(
            scope = CacheScope.MEMORY_CACHE
        )

        assertTrue(
            cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = uniqueKey,
                kClass = String::class
            ) is ApiResult.NoContent
        )

        assertEquals(
            ApiResult.Success(data = "cached value"),
            cacheManager.getData(
                scope = CacheScope.DISK_CACHE,
                key = uniqueKey,
                kClass = String::class
            )
        )
    }

    @Test
    fun clearAllClearsMemoryAndDiskScopes() = test {
        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            cacheManager.cacheData(
                scope = scope,
                config = ImmortalCacheConfig(key = uniqueKey),
                data = "cached value",
            )
        }

        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            assertTrue(
                cacheManager.getData(
                    scope = scope,
                    key = uniqueKey,
                    kClass = String::class
                ) is ApiResult.NoContent
            )
        }
    }

    @Test
    fun expiredTimeToLiveEntriesAreNotReturned() = test {
        cacheManager.clearAll()

        CacheScope.entries.forEach { scope ->
            cacheManager.cacheData(
                scope = scope,
                config = TimeToLiveCacheConfig(
                    key = uniqueKey,
                    duration = (-1).seconds,
                ),
                data = "expired value"
            )

            assertTrue(
                cacheManager.getData(
                    scope = scope,
                    key = uniqueKey,
                    kClass = String::class
                ) is ApiResult.NoContent
            )
        }
    }

    @Test
    fun repositoryPatternUsesCachedDataAndBypassesCacheWhenConfigured() = test {
        cacheManager.clearAll()

        var sourceCalls = 0
        val cachedConfig = ImmortalCacheConfig(
            key = uniqueKey
        )

        val firstResult = cacheManager.repositoryPattern(
            scope = CacheScope.MEMORY_CACHE,
            config = cachedConfig,
            source = {
                sourceCalls++
                ApiResult.Success(data = "cached value")
            }
        )

        val cachedResult = cacheManager.repositoryPattern(
            scope = CacheScope.MEMORY_CACHE,
            config = cachedConfig,
            source = {
                sourceCalls++
                ApiResult.Success(data = "new source value")
            }
        )

        assertEquals(ApiResult.Success(data = "cached value"), firstResult)
        assertEquals(ApiResult.Success(data = "cached value"), cachedResult)
        assertEquals(1, sourceCalls)

        val bypassResult = cacheManager.repositoryPattern(
            scope = CacheScope.MEMORY_CACHE,
            config = ImmortalCacheConfig(
                key = uniqueKey,
                bypassCache = true
            ),
            source = {
                sourceCalls++
                ApiResult.Success(data = "bypassed value")
            }
        )

        assertEquals(ApiResult.Success(data = "bypassed value"), bypassResult)
        assertEquals(2, sourceCalls)
    }
}
