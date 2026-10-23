package com.core.data.analytics.model

import com.core.data.analytics.frameworks.firebase.model.BaseFirebaseTrackView
import com.google.firebase.analytics.FirebaseAnalytics

internal data class FirebaseAnalyticsViewTest(
    val name: String
) : BaseFirebaseTrackView(
    data = mapOf(FirebaseAnalytics.Param.SCREEN_NAME to name)
)
