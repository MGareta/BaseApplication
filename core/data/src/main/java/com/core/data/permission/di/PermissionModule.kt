package com.core.data.permission.di

import com.core.data.activityResult.manager.IActivityResultManager
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.manager.PermissionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {
    @Singleton
    @Provides
    fun providesPermissionManager(activityResultManager: IActivityResultManager): IPermissionManager =
        PermissionManagerImpl(activityResultManager)
}
