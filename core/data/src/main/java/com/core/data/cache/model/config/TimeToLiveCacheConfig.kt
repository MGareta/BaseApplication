package com.core.data.cache.model.config

import kotlin.time.Duration

/**
 * Configuration for a time-to-live (TTL) cache entry.
 *
 * This data class defines the settings for how long a cached item should be considered valid
 * before it expires and needs to be refreshed. It extends [ICacheConfig] to provide
 * a common interface for different cache configurations.
 *
 * @property key The unique identifier for the cache entry. This is used to retrieve
 *               and invalidate the cached item.
 * @property bypassCache If true, the cache will be bypassed, and the underlying
 *                       data source will be queried directly. Defaults to false.
 * @property duration The duration of time, that the cached item should remain valid.
 *
 * Example Usage:
 * ```kotlin
 * // Configure a cache entry with a key "user_profile_123" that expires after 10 minutes.
 * val userProfileCacheConfig = TimeToLiveCacheConfig(
 *     key = "user_profile_123",
 *     duration = 10.toDuration(unit = DurationUnit.MINUTES)
 * )
 *
 * // Configure a cache entry with a key "product_list" that is valid for 1 hour and can be bypassed.
 * val productListCacheConfig = TimeToLiveCacheConfig(
 *     key = "product_list",
 *     bypassCache = true,
 *     duration = 1.toDuration(unit = DurationUnit.HOURS)
 * )
 * ```
 */
data class TimeToLiveCacheConfig(
    override val key: String,
    override val bypassCache: Boolean = false,
    internal val duration: Duration,
) : ICacheConfig
