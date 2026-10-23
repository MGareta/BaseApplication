package com.core.data.download.di

import com.core.data.download.api.DownloadApiService
import com.core.data.download.manager.DownloadManagerImpl
import com.core.data.download.manager.IDownloadManager
import com.core.data.download.repository.DownloadRepositoryImpl
import com.core.data.download.repository.IDownloadRepository
import com.core.data.file.manager.IFileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadModule {
    @Singleton
    @Provides
    fun providesDownloadManager(
        repository: IDownloadRepository,
        fileManager: IFileManager
    ): IDownloadManager = DownloadManagerImpl(
        repository = repository,
        fileManager = fileManager
    )

    @Provides
    @Singleton
    fun providesDownloadRepository(
        apiService: DownloadApiService
    ): IDownloadRepository = DownloadRepositoryImpl(
        apiService = apiService
    )
}
