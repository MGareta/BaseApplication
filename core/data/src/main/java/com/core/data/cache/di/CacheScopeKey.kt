package com.core.data.cache.di

import com.core.data.cache.model.CacheScope
import dagger.MapKey

@MapKey
annotation class CacheScopeKey(val value: CacheScope)
