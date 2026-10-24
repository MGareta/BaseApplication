package com.core.data.notifications.manager

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.core.data.network.model.ApiResult
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.model.PermissionStatus

class NotificationsManagerImpl(
    private val context: Context,
    private val permissionManager: IPermissionManager
) : INotificationsManager {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun enableDeviceNotifications(): ApiResult<PermissionStatus> {
        val permissionStatus = permissionManager.requestPermission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
        )

        return ApiResult.Success(
            data = permissionStatus,
        )
    }

    override suspend fun areDeviceNotificationsEnabled(): ApiResult<Boolean> {
        val pushNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.hasPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            true
        }

        return ApiResult.Success(
            data = pushNotificationsEnabled,
        )
    }

    override suspend fun erasedNotificationFromStatusBar(
        id: String
    ) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE,
        ) as NotificationManager

        val notification = notificationManager.activeNotifications
            .firstOrNull {
                it.tag == id
            }

        notification?.let {
            NotificationManagerCompat.from(context).cancel(id, it.id)
        }
    }
}
