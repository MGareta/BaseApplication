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

/**
 * `CacheMemoryScope` is an implementation of `ICacheScope` that utilizes an in-memory LRU (Least Recently Used) cache.
 * It stores and retrieves data based on a given key, with support for different cache configurations such as
 * time-to-live and immortal caches. The data is serialized to JSON for storage and deserialized upon retrieval.
 *
 * @property context The Android application context, used to determine available memory.
 * @property json The `Json` instance for serializing and deserializing data.
 * @constructor Creates a `CacheMemoryScope` with the provided context and JSON instance.
 *
 * The cache size is determined dynamically based on the available memory of the device, allocated to 1/8 of the available memory.
 */
class CacheMemoryScope(@ApplicationContext context: Context, private val json: Json) : ICacheScope {
    private val cache: LruCache<String, CacheEntry>

    init {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val sizeInBytes = activityManager.memoryClass * 1024 * 1024

        cache = LruCache(sizeInBytes / 8)
    }

    /**
     * **How it works:**
     * This function handles adding or updating data in the cache. It first checks if
     * a cache entry already exists for the given key. If it does, the existing entry
     * is removed. Then, it determines the appropriate duration for the cache entry
     * based on the provided `ICacheConfig`. The data is then serialized using
     * `DynamicLookupSerializer` and stored in the cache as a `CacheEntry` along with
     * the calculated duration.
     */
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun putData(config: ICacheConfig, data: Any) {
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
                value = data,
            ),
            duration = duration,
        )

        cache.put(config.key, cacheEntry)
    }

    /**
     * **How it works:**
     * This function attempts to retrieve a cached entry based on the provided `key`. If the entry exists and hasn't
     * expired, it deserializes the stored string value into an object of type `T` using the provided `KClass`'s
     * serializer. If no entry exists for the key or the entry has expired, it returns `ApiResult.NoContent`.
     */
    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> getData(key: String, kClass: KClass<T>): ApiResult<T> {
        val cacheEntry = cache[key] ?: return ApiResult.NoContent()

        return if (cacheEntry.hasExpired()) {
            ApiResult.NoContent()
        } else {
            ApiResult.Success(
                data = json.decodeFromString(
                    deserializer = kClass.serializer(),
                    string = cacheEntry.value,
                ),
            )
        }
    }

    /**
     * **How it works:**
     * This function removes the entry identified by `key` from the underlying cache.
     * If no data is associated with the specified key, this function has no effect.
     */
    override suspend fun deleteData(key: String) {
        cache.remove(key)
    }

    /**
     * **How it works:**
     * This function removes all key-value pairs stored within the underlying cache.
     * After calling this function, the cache will be empty. Subsequent read operations
     * will result in cache misses until new entries are added.
     */
    override suspend fun clearAll() {
        cache.evictAll()
    }
}
