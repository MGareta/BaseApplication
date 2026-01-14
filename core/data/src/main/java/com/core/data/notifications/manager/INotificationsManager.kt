package com.core.data.notifications.manager

import com.core.data.network.model.ApiResult
import com.core.data.permission.model.PermissionStatus

interface INotificationsManager {
    /**
     * Enables device notifications for the application.
     *
     * This function requests the necessary permissions from the user to allow the app to send
     * push notifications. It handles the different states of the permission, such as granted,
     * denied, or permanently denied, and returns an `ApiResult` indicating the final
     * `PermissionStatus`.
     *
     * The function is a suspend function, meaning it can be safely called within a coroutine.
     *
     * @return An [ApiResult] containing the [PermissionStatus].
     *         - `ApiResult.Success`: Indicates that the notification permission request was
     *           successful. The `data` field will contain the [PermissionStatus].
     *           Possible values of the data are:
     *              - [PermissionStatus.GRANTED]: The user granted the notification permission.
     *              - [PermissionStatus.DENIED]: The user denied the notification permission.
     *              - [PermissionStatus.PERMANENTLY_DENIED]: The user denied the notification permission
     *                and selected "Don't ask again".
     *         - `ApiResult.Failure`: Indicates an error occurred during the permission request
     *           process. The `error` field will contain the details of the error.
     *
     * @see PermissionStatus
     * @see ApiResult
     */
    suspend fun enableDeviceNotifications(): ApiResult<PermissionStatus>

    /**
     * Checks if device notifications are enabled for the application.
     *
     * This function attempts to determine if the user has granted the application permission
     * to display notifications on their device. The exact implementation may vary depending
     * on the platform and operating system version.
     *
     * @return An [ApiResult] wrapping a [Boolean] value.
     *         - [ApiResult.Success] with `true` if notifications are enabled, `false` otherwise.
     *         - [ApiResult.Error] if there was an error checking the notification status. The error
     *           type and message will provide more details.
     *         - [ApiResult.Loading] if the operation is in progress (unlikely for this check, but
     *           possible depending on underlying implementation)
     *
     * @throws Exception if an unexpected error occurs during the check. The exception details
     *         will be included in the [ApiResult.Error].
     */
    suspend fun areDeviceNotificationsEnabled(): ApiResult<Boolean>

    /**
     * Erases a notification from the status bar with the given ID.
     *
     * This function simulates the action of dismissing a notification from the Android status bar.
     * It's designed for use in UI testing or automation where you need to interact with the
     * notification system.
     *
     * Note: This function is likely part of a testing or mock framework, as direct interaction
     * with the system's notification manager would typically require context and specific permissions
     * which are not implied by the function signature alone.  In a real app scenario, you'd typically
     * use NotificationManager to cancel a notification by ID.
     *
     * @param id The unique identifier of the notification to erase. This should correspond to the
     *           ID that was used when the notification was originally created and posted.
     * @throws IllegalArgumentException If the provided `id` is empty or null.
     */
    suspend fun erasedNotificationFromStatusBar(id: String)
}
