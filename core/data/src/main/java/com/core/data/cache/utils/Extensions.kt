package com.core.data.cache.utils

import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheEntry
import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.network.utils.onSuccess
import java.util.Calendar

/**
 * Checks if the CacheEntry has expired based on its timestamp and duration.
 *
 * A CacheEntry is considered expired if the current time is past the point
 * defined by the entry's timestamp plus its duration.
 *
 * If the duration is infinite, the CacheEntry will never expire.
 *
 * @return `true` if the CacheEntry has expired, `false` otherwise.
 *
 * Example Usage:
 * ```
 * val now = Calendar.getInstance().time.time
 * val entry1 = CacheEntry(now, 5.seconds)
 * val entry2 = CacheEntry(now - 10000, 10.seconds) //Created 10 seconds ago, so will be expired
 * val entry3 = CacheEntry(now, Duration.INFINITE)
 *
 * println(entry1.hasExpired()) // Output: false (if executed within 5 seconds) or true (if after 5 seconds)
 * println(entry2.hasExpired()) // Output: true (expired)
 * println(entry3.hasExpired()) // Output: false (never expires)
 * ```
 *
 * @see CacheEntry
 */
fun CacheEntry.hasExpired(): Boolean {
    val date = this.timestamp
    val duration = this.duration

    if (duration.isInfinite()) {
        return false
    }

    val now = Calendar.getInstance().time
    return (date + duration.inWholeMilliseconds) >= now.time
}

/**
 * Implements a repository pattern with caching. This function attempts to retrieve data from the cache first.
 * If the data is not found in the cache, or if the cache is bypassed, it fetches the data from the source,
 * caches it, and then returns the data.
 *
 * @param T The type of the data to be retrieved and cached.
 * @param scope The caching scope to use. Defines the context for caching, e.g., global, user-specific.
 * @param config The cache configuration, defining details like the key and cache bypass behavior.
 * @param source A suspend lambda that fetches the data from the original source (e.g., an API). It returns an [ApiResult]
 *               encapsulating either the successful data or an error.
 * @return An [ApiResult] representing the outcome of the operation. If successful, it contains the cached or newly fetched data.
 *         If an error occurs during source fetching, it will contain the error.
 *
 * **Caching Behavior:**
 * - If `config.bypassCache` is true, the cache is always bypassed. The function fetches data from the source, caches it, and returns it.
 * - If `config.bypassCache` is false (default behavior):
 *   - It first attempts to retrieve data from the cache using `getData(scope, config.key, T::class)`.
 *   - If the data is found in the cache, it returns `ApiResult.Success(data)` with the cached data.
 *   - If the data is not found in the cache (cache miss), it executes the `source` lambda, caches the result if successful, and then returns the `ApiResult`.
 *
 * **Error Handling:**
 * - Errors during the source data fetching are propagated through the [ApiResult].
 */
suspend inline fun <reified T : Any> ICacheManager.repositoryPattern(
    scope: CacheScope,
    config: ICacheConfig,
    crossinline source: () -> ApiResult<T>,
): ApiResult<T> {
    val executeSourceAndCache = suspend {
        source()
            .onSuccess {
                cacheData(
                    scope = scope,
                    config = config,
                    data = it,
                )
            }
    }

    if (config.bypassCache) {
        return executeSourceAndCache()
    }

    val cacheData = getData(
        scope = scope,
        key = config.key,
        kClass = T::class,
    ).getOrNull()

    if (cacheData == null) {
        return executeSourceAndCache()
    }

    return ApiResult.Success(data = cacheData)
}
