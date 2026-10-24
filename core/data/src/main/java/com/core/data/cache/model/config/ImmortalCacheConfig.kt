package com.core.data.cache.model.config

data class ImmortalCacheConfig(
    override val key: String,
    override val bypassCache: Boolean = false
) : ICacheConfig
