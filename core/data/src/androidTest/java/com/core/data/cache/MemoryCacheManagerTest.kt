package com.core.data.cache

import com.core.data.BaseTest
import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ImmortalCacheConfig
import com.core.data.cache.utils.repositoryPattern
import com.core.data.network.model.ApiResult
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.junit4.MockKRule
import junit.framework.TestCase.assertEquals
import org.junit.Test
import javax.inject.Inject

private const val DATA = "TEST"
private const val KEY = "MEMORY_KEY"

@HiltAndroidTest
class MemoryCacheManagerTest : BaseTest() {
    @Inject
    lateinit var cacheManager: ICacheManager

    override val hiltRule: HiltAndroidRule
        get() = HiltAndroidRule(this)

    override val mockkRule: MockKRule
        get() = MockKRule(this)

    @Test
    fun cacheData() {
        test {
            cacheManager.cacheData(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                data = DATA,
            )
        }
    }

    @Test
    fun getData() {
        test {
            val noDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(noDataResult is ApiResult.NoContent)

            cacheManager.cacheData(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                data = DATA,
            )

            val cacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(cacheDataResult is ApiResult.Success)
            assertEquals((cacheDataResult as ApiResult.Success).data, DATA)
        }
    }

    @Test
    fun deleteData() {
        test {
            cacheManager.cacheData(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                data = DATA,
            )

            val cacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(cacheDataResult is ApiResult.Success)
            assertEquals((cacheDataResult as ApiResult.Success).data, DATA)

            cacheManager.deleteData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
            )

            val deleteCacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(deleteCacheDataResult is ApiResult.NoContent)
        }
    }

    @Test
    fun deleteScope() {
        test {
            cacheManager.cacheData(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                data = DATA,
            )

            val cacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(cacheDataResult is ApiResult.Success)
            assertEquals((cacheDataResult as ApiResult.Success).data, DATA)

            cacheManager.deleteScope(
                scope = CacheScope.MEMORY_CACHE,
            )

            val deleteCacheScopeResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(deleteCacheScopeResult is ApiResult.NoContent)
        }
    }

    @Test
    fun clearAll() {
        test {
            cacheManager.cacheData(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                data = DATA,
            )

            val cacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(cacheDataResult is ApiResult.Success)
            assertEquals((cacheDataResult as ApiResult.Success).data, DATA)

            cacheManager.clearAll()

            val deleteCacheScopeResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(deleteCacheScopeResult is ApiResult.NoContent)
        }
    }

    @Test
    fun repositoryPattern() {
        test {
            val repositoryPatternResult = cacheManager.repositoryPattern(
                scope = CacheScope.MEMORY_CACHE,
                config = ImmortalCacheConfig(
                    key = KEY,
                ),
                source = {
                    ApiResult.Success(data = DATA)
                },
            )

            assert(repositoryPatternResult is ApiResult.Success)
            assertEquals((repositoryPatternResult as ApiResult.Success).data, DATA)

            val cacheDataResult = cacheManager.getData(
                scope = CacheScope.MEMORY_CACHE,
                key = KEY,
                kClass = String::class,
            )

            assert(cacheDataResult is ApiResult.Success)
            assertEquals((cacheDataResult as ApiResult.Success).data, DATA)
        }
    }
}
