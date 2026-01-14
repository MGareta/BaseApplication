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
    /**
     * Initializes the Crashlytics service.
     *
     * This function enables Crashlytics collection for the application.
     * It sets the `isCrashlyticsCollectionEnabled` property of Firebase Crashlytics to `true`.
     *
     * @return An [ApiResult] indicating the success or failure of the initialization process.
     *   - [ApiResult.Success] with [Unit] data if initialization was successful.
     *   - [ApiResult.Exception] if an exception occurred during initialization.
     *
     * @throws Exception If any exception occurs during enabling the Crashlytics collection.
     */
    override fun initialize(): ApiResult<Unit> = try {
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = true

        ApiResult.Success(data = Unit)
    } catch (exception: Exception) {
        ApiResult.Exception(exception = exception)
    }

    /**
     * Tracks a view event in Firebase Analytics.
     *
     * This function is responsible for sending a view event to Firebase Analytics,
     * providing information about the screen or UI element that the user is currently viewing.
     * The specific details of the view are encapsulated within the [BaseFirebaseTrackView] object.
     *
     * **Important Notes:**
     *   - Ensure that Firebase Analytics is properly initialized in your application before calling this function.
     *   - Errors encountered during the event logging process will not be thrown, but will log to the console for debug purpose.
     *
     * @param view An object of type [BaseFirebaseTrackView] containing the details of the view
     *             to be tracked. This object should be properly configured with relevant
     *             information such as the view's name or identifier, any associated content,
     *             and other relevant metadata.
     *
     * Note: This function is part of a larger system for tracking user interactions
     * and requires proper setup and configuration of Firebase Analytics.
     *
     * @see BaseFirebaseTrackView
     * @see com.google.firebase.analytics.FirebaseAnalytics
     */
    override fun trackView(view: BaseFirebaseTrackView) {
        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(*view.data.toList().toTypedArray()),
        )
    }

    /**
     * Tracks a custom event to Firebase Analytics.
     *
     * This function takes a `BaseFirebaseTrackEvent` object as input, which encapsulates
     * the event name and its associated parameters. It then logs this event to Firebase
     * Analytics for tracking and analysis.
     *
     * **Important Notes:**
     *   - Ensure that Firebase Analytics is properly initialized in your application before calling this function.
     *   - The `event` object should be properly constructed with the desired event name and parameters.
     *   - Event names should adhere to Firebase Analytics naming conventions (alphanumeric characters and underscores, max 40 characters).
     *   - Parameter names should also follow Firebase Analytics conventions (alphanumeric characters and underscores, max 40 characters).
     *   - Parameter values have restrictions, including the number of parameters per event (up to 25) and size limits. Refer to the official Firebase Analytics documentation for full details.
     *   - For best practices, use predefined event names from the Firebase documentation when applicable.
     *   - Errors encountered during the event logging process will not be thrown, but will log to the console for debug purpose.
     *
     * @param event The `BaseFirebaseTrackEvent` object containing the event details to be logged.
     *
     * @see com.google.firebase.analytics.FirebaseAnalytics
     * @see BaseFirebaseTrackEvent
     */
    override fun trackEvent(event: BaseFirebaseTrackEvent) {
        Firebase.analytics.logEvent(
            event.key,
            bundleOf(*event.data.toList().toTypedArray()),
        )
    }
}
