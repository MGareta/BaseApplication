package com.core.data.analytics.manager

import com.core.data.analytics.frameworks.IAnalyticsFramework
import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.network.model.ApiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AnalyticsManagerImpl(
    private val frameworks: Map<
            @JvmSuppressWildcards AnalyticsFramework,
            @JvmSuppressWildcards IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>
        >,
) : IAnalyticsManager {

    override suspend fun initialize(): ApiResult<Unit> {
        val initAnalytics = coroutineScope {
            frameworks.map {
                async { it.value.initialize() }
            }.awaitAll()
        }

        initAnalytics.firstOrNull {
            it is ApiResult.Exception
        }?.let {
            return it as ApiResult.Exception
        }

        return ApiResult.Success(data = Unit)
    }

    override fun <VIEW : BaseAnalyticsView> trackView(
        framework: AnalyticsFramework,
        view: VIEW
    ) {
        frameworks[framework]?.trackView(view = view)
    }

    override fun <EVENT : BaseAnalyticsEvent> trackEvent(
        framework: AnalyticsFramework,
        event: EVENT
    ) {
        frameworks[framework]?.trackEvent(event = event)
    }
}
