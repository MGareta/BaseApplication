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
    private val frameworks:
    Map<@JvmSuppressWildcards AnalyticsFramework, @JvmSuppressWildcards IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>>,
) : IAnalyticsManager {
    /**
     * **How it works:**
     * This function iterates through a collection of analytics frameworks and initializes each one.
     * It uses coroutines to perform the initialization concurrently, improving efficiency.
     * If any framework's initialization fails (returns an ApiResult.Exception),
     * this function immediately returns the first encountered exception.
     * Otherwise, if all frameworks initialize successfully, it returns an ApiResult.Success.
     */
    override suspend fun initialize(): ApiResult<Unit> {
        val initAnalytics = coroutineScope {
            frameworks
                .map {
                    async { it.value.initialize() }
                }.awaitAll()
        }

        initAnalytics.firstOrNull { it is ApiResult.Exception }?.let {
            return it as ApiResult.Exception
        }

        return ApiResult.Success(data = Unit)
    }

    /**
     * **How it works:**
     * This function allows tracking of a specific view within a supported analytics framework.
     * It delegates the tracking to the appropriate framework implementation if it is registered.
     */
    override fun <VIEW : BaseAnalyticsView> trackView(framework: AnalyticsFramework, view: VIEW) {
        frameworks[framework]?.trackView(view = view)
    }

    /**
     * **How it works:**
     * This function takes an analytics framework and an event as input and delegates
     * the actual tracking to the corresponding framework's implementation.
     */
    override fun <EVENT : BaseAnalyticsEvent> trackEvent(framework: AnalyticsFramework, event: EVENT) {
        frameworks[framework]?.trackEvent(event = event)
    }
}
