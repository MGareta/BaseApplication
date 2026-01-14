package com.core.data.analytics.di

import com.core.data.analytics.frameworks.IAnalyticsFramework
import com.core.data.analytics.frameworks.firebase.FirebaseAnalyticsFramework
import com.core.data.analytics.manager.AnalyticsManagerImpl
import com.core.data.analytics.manager.IAnalyticsManager
import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Suppress("UNCHECKED_CAST")
    @Provides
    @IntoMap
    @AnalyticsFrameworkKey(AnalyticsFramework.FIREBASE)
    fun providesFirebaseAnalyticsFramework(): IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent> =
        FirebaseAnalyticsFramework() as IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>

    @Singleton
    @Provides
    fun providesAnalyticsManager(
        frameworks:
        Map<@JvmSuppressWildcards AnalyticsFramework, @JvmSuppressWildcards IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>>,
    ): IAnalyticsManager = AnalyticsManagerImpl(
        frameworks = frameworks,
    )
}
