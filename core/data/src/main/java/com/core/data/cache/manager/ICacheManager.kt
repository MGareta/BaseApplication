package com.core.data.cache.manager

import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

interface ICacheManager {

    suspend fun cacheData(
        scope: CacheScope,
        config: ICacheConfig,
        data: Any
    )

    suspend fun <T : Any> getData(
        scope: CacheScope,
        key: String,
        kClass: KClass<T>
    ): ApiResult<T>

    suspend fun deleteData(
        scope: CacheScope?,
        key: String
    )

    suspend fun deleteScope(
        scope: CacheScope
    )

    suspend fun clearAll()
}
