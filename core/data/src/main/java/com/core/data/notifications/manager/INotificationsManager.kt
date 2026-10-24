package com.core.data.notifications.manager

import com.core.data.network.model.ApiResult
import com.core.data.permission.model.PermissionStatus

interface INotificationsManager {

    suspend fun enableDeviceNotifications(): ApiResult<PermissionStatus>

    suspend fun areDeviceNotificationsEnabled(): ApiResult<Boolean>

    suspend fun erasedNotificationFromStatusBar(
        id: String
    )
}
