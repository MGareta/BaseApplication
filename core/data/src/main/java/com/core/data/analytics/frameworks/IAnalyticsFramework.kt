package com.core.data.analytics.frameworks

import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.network.model.ApiResult

interface IAnalyticsFramework<VIEW : BaseAnalyticsView, EVENT : BaseAnalyticsEvent> {

    fun initialize(): ApiResult<Unit>

    fun trackView(view: VIEW)

    fun trackEvent(event: EVENT)
}
