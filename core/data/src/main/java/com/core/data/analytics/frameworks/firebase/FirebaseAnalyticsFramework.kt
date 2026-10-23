package com.core.data.analytics.frameworks.firebase

import androidx.core.os.bundleOf
import com.core.data.analytics.frameworks.IAnalyticsFramework
import com.core.data.analytics.frameworks.firebase.model.BaseFirebaseTrackEvent
import com.core.data.analytics.frameworks.firebase.model.BaseFirebaseTrackView
import com.core.data.network.model.ApiResult
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics

class FirebaseAnalyticsFramework : IAnalyticsFramework<BaseFirebaseTrackView, BaseFirebaseTrackEvent> {

    override fun initialize(): ApiResult<Unit> = try {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true

        ApiResult.Success(data = Unit)
    } catch (exception: Exception) {
        ApiResult.Exception(exception = exception)
    }

    override fun trackView(view: BaseFirebaseTrackView) = Firebase.analytics
        .logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(*view.data.toList().toTypedArray()),
        )

    override fun trackEvent(event: BaseFirebaseTrackEvent) = Firebase.analytics
        .logEvent(
            event.key,
            bundleOf(*event.data.toList().toTypedArray()),
        )
}
