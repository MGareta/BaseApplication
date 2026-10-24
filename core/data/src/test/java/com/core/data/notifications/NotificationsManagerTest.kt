package com.core.data.notifications

import android.Manifest
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.core.data.HiltRobolectricBaseTest
import com.core.data.network.model.ApiResult
import com.core.data.notifications.manager.INotificationsManager
import com.core.data.permission.di.PermissionModule
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.model.PermissionStatus
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(PermissionModule::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class NotificationsManagerTest : HiltRobolectricBaseTest() {

    @BindValue
    @JvmField
    val permissionManager: IPermissionManager = mockk()

    @Inject
    lateinit var notificationsManager: INotificationsManager

    override fun initDependencies() {
        shadowOf(context as Application)
            .grantPermissions(
                Manifest.permission.POST_NOTIFICATIONS
            )
    }

    @Test
    fun enableDeviceNotificationsReturnsRequestedPermissionStatus() = test {
        val permissionStatus = PermissionStatus.Granted

        coEvery {
            permissionManager.requestPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        } returns permissionStatus

        val result = notificationsManager.enableDeviceNotifications()

        assertEquals(ApiResult.Success(data = permissionStatus), result)

        coVerify(exactly = 1) {
            permissionManager.requestPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    @Test
    fun areDeviceNotificationsEnabledReflectsPermissionState() = test {
        every {
            permissionManager.hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        } returnsMany listOf(true, false)

        assertEquals(ApiResult.Success(data = true), notificationsManager.areDeviceNotificationsEnabled())
        assertEquals(ApiResult.Success(data = false), notificationsManager.areDeviceNotificationsEnabled())

        verify(exactly = 2) {
            permissionManager.hasPermission(
                permission = Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.S_V2])
    fun areDeviceNotificationsEnabledDefaultsToTrueBeforeAndroid13() = test {
        val result = notificationsManager.areDeviceNotificationsEnabled()

        assertEquals(ApiResult.Success(data = true), result)

        verify(exactly = 0) {
            permissionManager.hasPermission(
                permission = any()
            )
        }
    }

    @Test
    fun erasesNotificationWithMatchingTag() = test {
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE,
        ) as NotificationManager

        val channelId = "test_channel_${UUID.randomUUID()}"
        val notificationTag = "test_notification_${UUID.randomUUID()}"
        val notificationId = 1

        with(
            receiver = notificationManager
        ) {
            createNotificationChannel(
                NotificationChannel(
                    channelId,
                    "Test notifications",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            )

            notify(
                notificationTag,
                notificationId,
                Notification.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build()
            )
        }

        assertTrue(notificationManager.activeNotifications.any {
            it.tag == notificationTag
        })

        notificationsManager.erasedNotificationFromStatusBar(
            id = notificationTag
        )

        assertTrue(notificationManager.activeNotifications.none {
            it.tag == notificationTag
        })
    }
}
