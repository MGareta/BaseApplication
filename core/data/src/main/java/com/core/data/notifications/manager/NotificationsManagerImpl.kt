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

class NotificationsManagerImpl(private val context: Context, private val permissionManager: IPermissionManager) :
    INotificationsManager {
    /**
     * **How it works:**
     * This function is designed for API level 33 (TIRAMISU) and above, as the
     * `POST_NOTIFICATIONS` permission was introduced in that version.
     *
     * It utilizes the `permissionManager` to request the necessary permission and
     * returns the resulting permission status wrapped in an `ApiResult`.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun enableDeviceNotifications(): ApiResult<PermissionStatus> {
        val permissionStatus = permissionManager.requestPermission(
            permission = Manifest.permission.POST_NOTIFICATIONS,
        )

        return ApiResult.Success(
            data = permissionStatus,
        )
    }

    /**
     * **How it works:**
     * On Android versions Tiramisu (API level 33) and above, it verifies if the
     * `POST_NOTIFICATIONS` permission is granted. On older versions, it assumes
     * notifications are enabled by default, as the permission is not required.
     */
    override suspend fun areDeviceNotificationsEnabled(): ApiResult<Boolean> {
        val pushNotificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.hasPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS,
            )
        } else {
            true
        }

        return ApiResult.Success(
            data = pushNotificationsEnabled,
        )
    }

    /**
     * **How it works:**
     * This function searches for an active notification with the provided tag (id)
     * and then removes it from the status bar.
     */
    override suspend fun erasedNotificationFromStatusBar(id: String) {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE,
        ) as NotificationManager

        val notification = notificationManager.activeNotifications.firstOrNull {
            it.tag == id
        }

        notification?.let {
            NotificationManagerCompat.from(context).cancel(id, it.id)
        }
    }
}
