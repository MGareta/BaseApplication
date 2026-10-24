package com.core.data.cache.manager

import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.cache.scopes.ICacheScope
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

class CacheManagerImpl(
    private val caches: Map<@JvmSuppressWildcards CacheScope, @JvmSuppressWildcards ICacheScope>
) : ICacheManager {

    override suspend fun cacheData(
        scope: CacheScope,
        config: ICacheConfig,
        data: Any
    ) {
        caches[scope]?.putData(config = config, data = data)
    }

    override suspend fun <T : Any> getData(
        scope: CacheScope,
        key: String,
        kClass: KClass<T>
    ): ApiResult<T> {
        val cacheScope = caches[scope]
            ?: return ApiResult.Exception(NoSuchElementException("CacheScope ${scope.name} does not exist"))

        return cacheScope.getData(key = key, kClass = kClass)
    }

    override suspend fun deleteData(
        scope: CacheScope?,
        key: String
    ) {
        if (scope != null) {
            caches[scope]?.deleteData(key = key)
        } else {
            caches.forEach {
                it.value.deleteData(key = key)
            }
        }
    }

    override suspend fun deleteScope(
        scope: CacheScope
    ) {
        caches[scope]?.clearAll()
    }

    override suspend fun clearAll() {
        caches.forEach {
            it.value.clearAll()
        }
    }
}
