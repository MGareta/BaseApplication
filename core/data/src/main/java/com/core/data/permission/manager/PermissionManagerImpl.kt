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

class PermissionManagerImpl @Inject constructor(private val activityResultManager: IActivityResultManager) :
    IPermissionManager {
    /**
     * **How it works:**
     * This function utilizes `ActivityCompat.checkSelfPermission` to determine
     * whether the application currently holds the specified permission. It checks
     * the permission against the application's context, provided by `ActivityProvider.applicationContext`.
     *
     * The function returns `true` if `checkSelfPermission` returns `PERMISSION_GRANTED` and false otherwise.
     */
    override fun hasPermission(permission: String): Boolean = ActivityCompat.checkSelfPermission(
        ActivityProvider.applicationContext,
        permission,
    ) == PERMISSION_GRANTED

    /**
     * **How it works:**
     * This function handles the logic of requesting a permission and determining the resulting
     * [PermissionStatus]. It interacts with an `activityResultManager` to perform the actual
     * permission request and then analyzes the result to provide a granular status.
     */
    override suspend fun requestPermission(permission: String): PermissionStatus {
        val isGranted = activityResultManager.requestPermission(permission)
        return if (isGranted) {
            PermissionStatus.Granted
        } else {
            val shouldShowRationale = ActivityProvider.currentActivity?.let {
                shouldShowRequestPermissionRationale(it, permission)
            }

            if (shouldShowRationale == true) {
                PermissionStatus.ShowRationale
            } else {
                PermissionStatus.Denied
            }
        }
    }

    /**
     * **How it works:**
     * This function uses the `IActivityResultManager` to request permissions and handles the result.
     * If the result is available, it maps the permission names to their statuses based on the result.
     * If the result is not available, it checks if the permissions are already granted and maps them accordingly.
     */
    override suspend fun requestPermissions(vararg permissions: String): Map<String, PermissionStatus> {
        return activityResultManager.requestPermissions(*permissions)?.let { result ->
            permissions.associateWith { permission ->
                if (result[permission] == true) {
                    PermissionStatus.Granted
                } else {
                    val shouldShowRationale = ActivityProvider.currentActivity?.let {
                        shouldShowRequestPermissionRationale(it, permission)
                    }

                    if (shouldShowRationale == true) {
                        PermissionStatus.ShowRationale
                    } else {
                        PermissionStatus.Denied
                    }
                }
            }
        } ?: return permissions.associateWith {
            if (hasPermission(it)) {
                PermissionStatus.Granted
            } else {
                PermissionStatus.Denied
            }
        }
    }
}
