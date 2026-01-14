package com.core.data.cache.di

import android.content.Context
import com.core.data.cache.manager.CacheManagerImpl
import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheScope
import com.core.data.cache.scopes.ICacheScope
import com.core.data.cache.scopes.disk.CacheDiskScope
import com.core.data.cache.scopes.memory.CacheMemoryScope
import com.core.data.security.manager.ISecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    @Provides
    @Singleton
    fun providesJsonConfiguration(): Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        useAlternativeNames = true
        encodeDefaults = true
        explicitNulls = true
        coerceInputValues = true
        allowStructuredMapKeys = true
    }

    @Provides
    @IntoMap
    @CacheScopeKey(CacheScope.MEMORY_CACHE)
    fun providesCacheMemoryScope(@ApplicationContext context: Context, json: Json): ICacheScope = CacheMemoryScope(
        context = context,
        json = json,
    )

    @Provides
    @IntoMap
    @CacheScopeKey(CacheScope.DISK_CACHE)
    fun providesCacheDiskScope(
        @ApplicationContext context: Context,
        json: Json,
        securityManager: ISecurityManager,
    ): ICacheScope = CacheDiskScope(
        context = context,
        json = json,
        securityManager = securityManager,
    )

    @Singleton
    @Provides
    fun providesCacheManager(
        caches: Map<@JvmSuppressWildcards CacheScope, @JvmSuppressWildcards ICacheScope>,
    ): ICacheManager = CacheManagerImpl(
        caches = caches,
    )
}
