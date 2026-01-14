package com.core.data.session.di

import com.core.data.cache.manager.ICacheManager
import com.core.data.session.manager.ISessionManager
import com.core.data.session.manager.SessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {
    @Singleton
    @Provides
    fun providesSessionManager(cacheManager: ICacheManager): ISessionManager = SessionManagerImpl(
        cacheManager = cacheManager,
    )
}
