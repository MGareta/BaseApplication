package com.core.data.cache.scopes.memory

import android.app.ActivityManager
import android.content.Context
import android.util.LruCache
import com.core.data.cache.model.CacheEntry
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.cache.model.config.ImmortalCacheConfig
import com.core.data.cache.model.config.TimeToLiveCacheConfig
import com.core.data.cache.scopes.ICacheScope
import com.core.data.cache.utils.DynamicLookupSerializer
import com.core.data.cache.utils.hasExpired
import com.core.data.network.model.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.time.Duration

@OptIn(InternalSerializationApi::class)
class CacheMemoryScope(
    @ApplicationContext context: Context,
    private val json: Json
) : ICacheScope {

    private val cache: LruCache<String, CacheEntry>

    init {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val sizeInBytes = activityManager.memoryClass * 1024 * 1024

        cache = LruCache(sizeInBytes / 8)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun putData(
        config: ICacheConfig,
        data: Any
    ) {
        if (cache[config.key] != null) {
            cache.remove(config.key)
        }

        val duration = when (config) {
            is ImmortalCacheConfig -> Duration.INFINITE
            is TimeToLiveCacheConfig -> config.duration
            else -> Duration.INFINITE
        }

        val cacheEntry = CacheEntry(
            value = json.encodeToString(
                serializer = DynamicLookupSerializer(),
                value = data
            ),
            duration = duration
        )

        cache.put(
            config.key,
            cacheEntry
        )
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> getData(
        key: String,
        kClass: KClass<T>
    ): ApiResult<T> {
        val cacheEntry = cache[key] ?: return ApiResult.NoContent()

        return if (cacheEntry.hasExpired()) {
            ApiResult.NoContent()
        } else {
            ApiResult.Success(
                data = json.decodeFromString(
                    deserializer = kClass.serializer(),
                    string = cacheEntry.value
                )
            )
        }
    }

    override suspend fun deleteData(
        key: String
    ) {
        cache.remove(key)
    }

    override suspend fun clearAll() {
        cache.evictAll()
    }
}
