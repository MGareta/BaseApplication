package com.core.data.file.di

import android.content.Context
import com.core.data.file.manager.FileManagerImpl
import com.core.data.file.manager.IFileManager
import com.core.data.permission.manager.IPermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileModule {
    @Singleton
    @Provides
    fun providesFileManager(@ApplicationContext context: Context, permissionManager: IPermissionManager): IFileManager =
        FileManagerImpl(
            context = context,
            permissionManager = permissionManager,
        )
}
