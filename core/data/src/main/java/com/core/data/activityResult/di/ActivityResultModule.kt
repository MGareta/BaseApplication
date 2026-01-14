package com.core.data.activityResult.di

import com.core.data.activityResult.manager.ActivityResultManagerImpl
import com.core.data.activityResult.manager.IActivityResultManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityResultModule {
    @Singleton
    @Provides
    fun providesActivityResultManager(): IActivityResultManager = ActivityResultManagerImpl()
}
