package com.core.data.notifications.di

import android.content.Context
import com.core.data.notifications.manager.INotificationsManager
import com.core.data.notifications.manager.NotificationsManagerImpl
import com.core.data.permission.manager.IPermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Singleton
    @Provides
    fun providesNotificationsManager(
        @ApplicationContext context: Context,
        permissionManager: IPermissionManager,
    ): INotificationsManager = NotificationsManagerImpl(
        context = context,
        permissionManager = permissionManager,
    )
}
