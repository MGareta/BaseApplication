package com.core.data.download

import com.core.data.download.api.DownloadApiService
import com.core.data.download.di.DownloadNetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DownloadNetworkModule::class]
)
object DownloadNetworkModuleTest {

    @Provides
    @Singleton
    fun providesDownloadApiService(): DownloadApiService = mockk()
}
