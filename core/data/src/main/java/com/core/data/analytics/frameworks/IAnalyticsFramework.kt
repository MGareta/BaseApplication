package com.core.data.analytics.frameworks

import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.network.model.ApiResult

interface IAnalyticsFramework<VIEW : BaseAnalyticsView, EVENT : BaseAnalyticsEvent> {
    /**
     * Initializes the underlying analytics system that this API interacts with.
     *
     * This function should be called before any other API functions are invoked.
     * It performs any necessary setup, configuration, or connection steps required
     * to make the analytics framework operational.
     *
     * Calling this function multiple times is generally safe and should not cause issues,
     * but may result in redundant work.
     *
     * @return An [ApiResult] indicating the outcome of the initialization process.
     *         - [ApiResult.Success] if initialization was successful.
     *         - [ApiResult.Failure] if initialization failed, with details about the error.
     *         - [ApiResult.Error] if initialization resulted in an unexpected error.
     *         - The [ApiResult] may contain a [Unit] in the success case, as no data is returned in that scenario
     *
     * @throws Exception if a severe, unrecoverable error occurs during initialization. (depending on the implementation this may be more specific)
     */
    fun initialize(): ApiResult<Unit>

    /**
     * Tracks a view event.
     *
     * This function is used to record that a specific view has been displayed to the user.
     * This information can be used for analytics, user behavior tracking, and performance monitoring.
     *
     * @param view The view that was displayed. The type of this parameter (VIEW) should be a
     *             representation of a screen or component in your application. This could be
     *             a custom enum, a class, or a string, depending on your implementation.
     *
     * Example usage:
     * ```kotlin
     * enum class AppView {
     *     HOME,
     *     PROFILE,
     *     SETTINGS
     * }
     *
     * trackView(AppView.HOME) // Tracks that the Home view was displayed.
     * ```
     *
     * Note: The actual implementation of tracking may involve sending data to a remote server,
     * storing it locally, or updating in-memory statistics. This function signature only
     * defines the input (the view being tracked).
     */
    fun trackView(view: VIEW)

    /**
     * Tracks a specific event within the application.
     *
     * This function is responsible for logging or reporting the occurrence of an event.
     * The exact behavior of this function (e.g., where the event is logged, if it's sent
     * to an analytics service, etc.) depends on the underlying implementation.
     *
     * @param event The [EVENT] to be tracked. This likely represents a specific action,
     *              interaction, or state change within the application.
     *              The type [EVENT] should be defined elsewhere in the code, likely as an enum or a sealed class.
     *
     * Example Usage:
     * ```kotlin
     * enum class MyEvents {
     *     BUTTON_CLICKED,
     *     SCREEN_VIEWED,
     *     USER_LOGGED_IN
     * }
     *
     * fun someFunction() {
     *     // ... some logic ...
     *     trackEvent(MyEvents.BUTTON_CLICKED)
     *     // ... more logic ...
     *     trackEvent(MyEvents.SCREEN_VIEWED)
     * }
     * ```
     */
    fun trackEvent(event: EVENT)
}
