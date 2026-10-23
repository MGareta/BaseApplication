package com.core.data.analytics.di

import com.core.data.analytics.frameworks.IAnalyticsFramework
import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dagger.multibindings.IntoMap
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AnalyticsFrameworkModule::class],
)
object AnalyticsFrameworkModuleTest {

    @Singleton
    @Provides
    @IntoMap
    @AnalyticsFrameworkKey(AnalyticsFramework.FIREBASE)
    fun providesAnalyticsFramework(): IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent> =
        mockk(relaxed = true)
}
