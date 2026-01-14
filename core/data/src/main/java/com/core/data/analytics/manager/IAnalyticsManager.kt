package com.core.data.analytics.manager

import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.network.model.ApiResult

interface IAnalyticsManager {
    /**
     * Initializes all analytics frameworks.
     *
     * @return An [ApiResult] indicating the success or failure of the initialization process.
     *         - [ApiResult.Success]: If all frameworks initialize successfully, this result contains [Unit] as data.
     *         - [ApiResult.Exception]: If any framework's initialization fails, this result contains the corresponding exception.
     */
    suspend fun initialize(): ApiResult<Unit>

    /**
     * Tracks a view event within the specified analytics framework.
     *
     * This function is a generic wrapper for tracking view events. It takes an [AnalyticsFramework]
     * instance and a [BaseAnalyticsView] (or a subclass) that represents the view to be tracked.
     * It leverages the framework's capabilities to record the view's visibility to the user.
     *
     * @param framework The analytics framework instance to use for tracking.
     * @param view The [BaseAnalyticsView] representing the view being displayed. This object should
     *             contain the relevant data about the view (e.g., view name, screen type, any associated data)
     *             that you want to capture in your analytics.
     * @param VIEW The specific type of view being tracked, which must be a subclass of [BaseAnalyticsView].
     *
     * @throws IllegalArgumentException if the provided event is not supported by the Framework
     *                  This means that the framework does not exist as a key in the `frameworks` map.
     * @throws IllegalStateException if the framework is not initialized correctly.
     *
     * @see BaseAnalyticsView
     * @see AnalyticsFramework
     *
     * Example:
     * ```kotlin
     * // Assuming you have a MyAnalyticsView class that extends BaseAnalyticsView
     * val myView = MyAnalyticsView("HomeScreen", "Main")
     * val firebaseFramework = FirebaseAnalyticsFramework() // Assuming you have a Firebase implementation of AnalyticsFramework
     * trackView(firebaseFramework, myView)
     * ```
     *
     * Usage Notes:
     * - Ensure that the `framework` has been properly initialized and configured before calling this function.
     * - `view` should be created and populated with necessary data before being passed to this function.
     * - The underlying implementation within the framework is responsible for properly formatting and sending the data.
     * - It is recommended to call this function whenever a new view becomes visible to the user.
     * - Consider centralizing the invocation of this function inside a custom navigation framework or presenter for maintainability.
     */
    fun <VIEW : BaseAnalyticsView> trackView(framework: AnalyticsFramework, view: VIEW)

    /**
     * Tracks an analytics event using the specified analytics framework.
     *
     * This function is a generic method for sending analytics events to different
     * analytics providers. It takes an `AnalyticsFramework` instance and an
     * event object that implements the `BaseAnalyticsEvent` interface.  The
     * specific implementation of how the event is tracked is handled by the
     * provided `AnalyticsFramework`.
     *
     * @param framework The analytics framework to use for tracking the event.
     *                  This determines where and how the event is sent.
     * @param event     The analytics event to track. This object contains the
     *                  data associated with the event, such as the event name,
     *                  properties, and any other relevant information. It must
     *                  implement the [BaseAnalyticsEvent] interface.
     * @param EVENT     The type of the analytics event. This must be a subclass
     *                  of [BaseAnalyticsEvent].
     *
     * @throws IllegalArgumentException if the provided event is not supported by the Framework
     *                  This means that the framework does not exist as a key in the `frameworks` map.
     * @throws IllegalStateException if the framework is not initialized correctly.
     *
     * @see BaseAnalyticsEvent
     * @see AnalyticsFramework
     *
     * Example:
     * ```kotlin
     * // Assuming you have a MyAnalyticsView class that extends BaseAnalyticsView
     * val myEvent = MyAnalyticsEvent("HomeScreen", "Main")
     * val firebaseFramework = FirebaseAnalyticsFramework() // Assuming you have a Firebase implementation of AnalyticsFramework
     * trackEvent(firebaseFramework, myEvent)
     * ```
     */
    fun <EVENT : BaseAnalyticsEvent> trackEvent(framework: AnalyticsFramework, event: EVENT)
}
