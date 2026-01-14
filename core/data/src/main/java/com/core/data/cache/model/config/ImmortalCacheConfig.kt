package com.core.data.cache.model.config

/**
 * Configuration class for an Immortal Cache entry.
 *
 * This class defines the settings for a specific entry within an immortal cache.
 * It allows specifying a unique key for the entry and whether the cache should be bypassed for this entry.
 *
 * @property key A unique string identifier for the cache entry. This key is used to retrieve and manage the cached data.
 * @property bypassCache A boolean flag indicating whether to bypass the cache for this entry.
 *                       - `true`: The cache will be bypassed, and data will always be fetched from the source.
 *                       - `false` (default): The cache will be used normally.
 * @see ICacheConfig
 */
data class ImmortalCacheConfig(override val key: String, override val bypassCache: Boolean = false) : ICacheConfig
