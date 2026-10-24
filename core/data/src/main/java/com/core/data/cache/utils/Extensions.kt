package com.core.data.cache.utils

import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheEntry
import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.network.utils.onSuccess
import kotlinx.serialization.InternalSerializationApi
import java.util.Calendar

@OptIn(InternalSerializationApi::class)
internal fun CacheEntry.hasExpired(): Boolean {
    val date = this.timestamp
    val duration = this.duration

    if (duration.isInfinite()) {
        return false
    }

    val now = Calendar.getInstance().time
    return (date + duration.inWholeMilliseconds) < now.time
}

suspend inline fun <reified T : Any> ICacheManager.repositoryPattern(
    scope: CacheScope,
    config: ICacheConfig,
    crossinline source: () -> ApiResult<T>
): ApiResult<T> {
    val executeSourceAndCache = suspend {
        source()
            .onSuccess {
                cacheData(
                    scope = scope,
                    config = config,
                    data = it
                )
            }
    }

    if (config.bypassCache) {
        return executeSourceAndCache()
    }

    val cacheData = getData(
        scope = scope,
        key = config.key,
        kClass = T::class
    ).getOrNull()

    if (cacheData == null) {
        return executeSourceAndCache()
    }

    return ApiResult.Success(
        data = cacheData
    )
}
