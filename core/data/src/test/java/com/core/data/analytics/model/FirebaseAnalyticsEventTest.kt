package com.core.data.analytics.model

import com.core.data.analytics.frameworks.firebase.model.BaseFirebaseTrackEvent

internal data class FirebaseAnalyticsEventTest(
    val name: String
) : BaseFirebaseTrackEvent(
    key = name,
    data = emptyMap<String, Any>()
)
