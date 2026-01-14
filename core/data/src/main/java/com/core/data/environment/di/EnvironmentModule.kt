package com.core.data.environment.di

import com.core.data.environment.manager.EnvironmentManagerImpl
import com.core.data.environment.manager.IEnvironmentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {
    @Singleton
    @Provides
    fun providesEnvironmentManager(): IEnvironmentManager = EnvironmentManagerImpl()
}
