package com.core.data.cache.model.config

import kotlin.time.Duration

data class TimeToLiveCacheConfig(
    override val key: String,
    override val bypassCache: Boolean = false,
    internal val duration: Duration
) : ICacheConfig
