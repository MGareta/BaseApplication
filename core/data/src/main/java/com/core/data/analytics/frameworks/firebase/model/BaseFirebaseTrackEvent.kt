package com.core.data.analytics.frameworks.firebase.model

import com.core.data.analytics.model.BaseAnalyticsEvent

abstract class BaseFirebaseTrackEvent(val key: String, val data: Map<String, *>) : BaseAnalyticsEvent
