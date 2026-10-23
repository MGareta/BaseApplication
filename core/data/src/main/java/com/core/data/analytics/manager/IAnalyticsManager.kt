package com.core.data.analytics.manager

import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.network.model.ApiResult

interface IAnalyticsManager {

    suspend fun initialize(): ApiResult<Unit>

    fun <VIEW : BaseAnalyticsView> trackView(framework: AnalyticsFramework, view: VIEW)

    fun <EVENT : BaseAnalyticsEvent> trackEvent(framework: AnalyticsFramework, event: EVENT)
}
