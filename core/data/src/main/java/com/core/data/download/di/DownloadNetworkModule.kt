package com.core.data.download.di

import com.core.data.download.api.DownloadApiService
import com.core.data.environment.manager.IEnvironmentManager
import com.core.data.network.di.factory.RetrofitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DownloadNetworkModule {

    @Provides
    @Singleton
    fun providesDownloadApiService(
        retrofitFactory: RetrofitFactory,
        environmentManager: IEnvironmentManager,
    ): DownloadApiService = retrofitFactory
        .create()
        .build(
            kClass = DownloadApiService::class.java,
            url = environmentManager.environment.apiUrl,
            interceptors = emptyList(),
        )
}
