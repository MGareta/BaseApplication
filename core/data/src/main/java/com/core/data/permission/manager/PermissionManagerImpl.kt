package com.core.data.permission.manager

import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.core.data.activityResult.manager.IActivityResultManager
import com.core.data.activityResult.utils.requestPermission
import com.core.data.activityResult.utils.requestPermissions
import com.core.data.permission.model.PermissionStatus
import com.core.data.utils.ActivityProvider
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(
    private val activityResultManager: IActivityResultManager
) : IPermissionManager {

    override fun hasPermission(
        permission: String
    ): Boolean = ActivityCompat.checkSelfPermission(
        ActivityProvider.applicationContext,
        permission
    ) == PERMISSION_GRANTED

    override suspend fun requestPermission(
        permission: String
    ): PermissionStatus {
        val isGranted = activityResultManager.requestPermission(
            permission = permission
        )

        return if (isGranted) {
            PermissionStatus.Granted
        } else {
            val shouldShowRationale = ActivityProvider.currentActivity
                ?.let {
                    shouldShowRequestPermissionRationale(
                        it,
                        permission
                    )
                }

            if (shouldShowRationale == true) {
                PermissionStatus.ShowRationale
            } else {
                PermissionStatus.Denied
            }
        }
    }

    override suspend fun requestPermissions(
        vararg permissions: String
    ): Map<String, PermissionStatus> {
        return activityResultManager.requestPermissions(
             *permissions
        )?.let { result ->
            permissions.associateWith { permission ->
                if (result[permission] == true) {
                    PermissionStatus.Granted
                } else {
                    val shouldShowRationale = ActivityProvider.currentActivity
                        ?.let {
                            shouldShowRequestPermissionRationale(it, permission)
                        }

                    if (shouldShowRationale == true) {
                        PermissionStatus.ShowRationale
                    } else {
                        PermissionStatus.Denied
                    }
                }
            }
        } ?: permissions.associateWith {
            if (hasPermission(it)) {
                PermissionStatus.Granted
            } else {
                PermissionStatus.Denied
            }
        }
    }
}
