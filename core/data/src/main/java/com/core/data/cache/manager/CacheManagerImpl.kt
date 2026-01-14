package com.core.data.cache.manager

import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.cache.scopes.ICacheScope
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

class CacheManagerImpl(private val caches: Map<@JvmSuppressWildcards CacheScope, @JvmSuppressWildcards ICacheScope>) :
    ICacheManager {
    /**
     * **How it works:**
     * This function attempts to store the given data in the cache associated with the provided
     * [CacheScope]. The operation is asynchronous and performed within a coroutine.
     */
    override suspend fun cacheData(scope: CacheScope, config: ICacheConfig, data: Any) {
        caches[scope]?.putData(config = config, data = data)
    }

    /**
     * **How it works:**
     * This function attempts to retrieve data of a specified type from a designated cache scope.
     * It first checks if the requested cache scope exists. If it does, it delegates the data retrieval
     * to the corresponding cache scope's `getData` method. If the cache scope is not found, it returns
     * an `ApiResult.Exception` indicating the missing scope.
     */
    override suspend fun <T : Any> getData(scope: CacheScope, key: String, kClass: KClass<T>): ApiResult<T> {
        val cacheScope = caches[scope]
            ?: return ApiResult.Exception(NoSuchElementException("CacheScope ${scope.name} does not exist"))

        return cacheScope.getData(key = key, kClass = kClass)
    }

    /**
     * **How it works:**
     * This function allows you to delete cached data identified by a given key.
     * You can specify a `CacheScope` to target a specific cache within the system,
     * or if `scope` is null, the data will be deleted from all caches.
     */
    override suspend fun deleteData(scope: CacheScope?, key: String) {
        if (scope != null) {
            caches[scope]?.deleteData(key = key)
        } else {
            caches.forEach {
                it.value.deleteData(key = key)
            }
        }
    }

    /**
     * **How it works:**
     * This function clears all entries within the cache that are associated with the
     * provided scope. If no cache exists for the given scope, this operation has no effect.
     */
    override suspend fun deleteScope(scope: CacheScope) {
        caches[scope]?.clearAll()
    }

    /**
     * **How it works:**
     * This function iterates through each cache in the `caches` map and calls the `clearAll()`
     * method on each individual cache. This effectively removes all cached data,
     * forcing subsequent requests to fetch fresh data from the source.
     */
    override suspend fun clearAll() {
        caches.forEach {
            it.value.clearAll()
        }
    }
}
