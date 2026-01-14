package com.core.data.cache.model.config

/**
 * Interface for configuring cache behavior for a specific operation or data set.
 *
 * Implementations of this interface define the parameters that control how data is stored and retrieved
 * from the cache. This includes a unique key used to identify the cached data and a flag to control
 * whether the cache should be bypassed entirely.
 */
interface ICacheConfig {
    val key: String
    val bypassCache: Boolean
}
