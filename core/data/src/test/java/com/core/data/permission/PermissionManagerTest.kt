package com.core.data.permission

import android.Manifest
import android.app.Application
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.core.data.BaseTest
import com.core.data.activityResult.di.ActivityResultModule
import com.core.data.activityResult.manager.IActivityResultManager
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.model.PermissionStatus
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(ActivityResultModule::class)
class PermissionManagerTest : BaseTest() {

    @BindValue
    @JvmField
    val activityResultManager: IActivityResultManager = mockk()

    @Inject
    lateinit var permissionManager: IPermissionManager

    override fun initDependencies() = Unit

    @Test
    fun hasPermissionReturnsTrueWhenGranted() = test {
        shadowOf(context as Application)
            .grantPermissions(
                Manifest.permission.CAMERA
            )

        assertTrue(permissionManager.hasPermission(permission = Manifest.permission.CAMERA))
    }

    @Test
    fun hasPermissionReturnsFalseWhenDenied() = test {
        assertFalse(permissionManager.hasPermission(permission = Manifest.permission.CAMERA))
    }

    @Test
    fun requestPermissionReturnsGrantedStatus() = test {
        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = Manifest.permission.CAMERA
            )
        } returns true

        val result = permissionManager.requestPermission(
            permission = Manifest.permission.CAMERA
        )

        assertEquals(PermissionStatus.Granted, result)

        coVerify(exactly = 1) {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = Manifest.permission.CAMERA
            )
        }
    }

    @Test
    fun requestPermissionReturnsDeniedWhenRequestIsRejected() = test {
        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = Manifest.permission.CAMERA
            )
        } returns false

        val result = permissionManager.requestPermission(
            permission = Manifest.permission.CAMERA
        )

        assertEquals(PermissionStatus.Denied, result)
    }

    @Test
    fun requestPermissionReturnsShowRationaleWhenNeeded() = test {
        val permission = Manifest.permission.CAMERA

        shadowOf(context.packageManager)
            .setShouldShowRequestPermissionRationale(
                permission,
                true
            )

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestPermission>(),
                input = permission
            )
        } returns false

        val activityController = Robolectric.buildActivity(
            FragmentActivity::class.java
        ).setup()

        try {
            val result = permissionManager.requestPermission(
                permission = permission
            )

            assertEquals(PermissionStatus.ShowRationale, result)
        } finally {
            activityController
                .pause()
                .stop()
                .destroy()
        }
    }

    @Test
    fun requestPermissionsReturnsShowRationaleWhenNeeded() = test {
        val rationalePermission = Manifest.permission.CAMERA
        val deniedPermission = Manifest.permission.RECORD_AUDIO
        val permissions = arrayOf(rationalePermission, deniedPermission)

        shadowOf(context.packageManager)
            .setShouldShowRequestPermissionRationale(
                rationalePermission,
                true
            )

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestMultiplePermissions>(),
                input = match {
                    it.contentEquals(
                        other = permissions
                    )
                }
            )
        } returns mapOf(
            rationalePermission to false,
            deniedPermission to false
        )

        val activityController = Robolectric.buildActivity(
            FragmentActivity::class.java
        ).setup()

        try {
            val result = permissionManager.requestPermissions(*permissions)

            assertEquals(
                mapOf(
                    rationalePermission to PermissionStatus.ShowRationale,
                    deniedPermission to PermissionStatus.Denied,
                ),
                result
            )
        } finally {
            activityController
                .pause()
                .stop()
                .destroy()
        }
    }

    @Test
    fun requestPermissionsMapsGrantedAndDeniedStatuses() = test {
        val grantedPermission = Manifest.permission.CAMERA
        val deniedPermission = Manifest.permission.RECORD_AUDIO
        val permissions = arrayOf(grantedPermission, deniedPermission)

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestMultiplePermissions>(),
                input = match {
                    it.contentEquals(
                        other = permissions
                    )
                }
            )
        } returns mapOf(
            grantedPermission to true,
            deniedPermission to false
        )

        val result = permissionManager.requestPermissions(
             *permissions
        )

        assertEquals(
            mapOf(
                grantedPermission to PermissionStatus.Granted,
                deniedPermission to PermissionStatus.Denied,
            ),
            result
        )

        coVerify(exactly = 1) {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestMultiplePermissions>(),
                input = match {
                    it.contentEquals(
                        other = permissions
                    )
                }
            )
        }
    }

    @Test
    fun requestPermissionsUsesCurrentGrantsWhenRequestReturnsNull() = test {
        val grantedPermission = Manifest.permission.CAMERA
        val deniedPermission = Manifest.permission.RECORD_AUDIO
        val permissions = arrayOf(grantedPermission, deniedPermission)

        shadowOf(context as Application)
            .grantPermissions(
                grantedPermission
            )

        coEvery {
            activityResultManager.requestResult(
                contract = any<ActivityResultContracts.RequestMultiplePermissions>(),
                input = match {
                    it.contentEquals(
                        other = permissions
                    )
                }
            )
        } returns null

        val result = permissionManager.requestPermissions(*permissions)

        assertEquals(
            mapOf(
                grantedPermission to PermissionStatus.Granted,
                deniedPermission to PermissionStatus.Denied,
            ),
            result
        )
    }
}
