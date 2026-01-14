package com.core.data.permission

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.core.data.BaseTest
import com.core.data.permission.manager.IPermissionManager
import com.core.data.permission.model.PermissionStatus
import com.core.data.utils.ActivityProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class PermissionManagerTest : BaseTest() {
    @MockK
    lateinit var permissionManager: IPermissionManager

    @ApplicationContext
    @Inject
    lateinit var context: Context

    override val hiltRule: HiltAndroidRule
        get() = HiltAndroidRule(this)

    override val mockkRule: MockKRule
        get() = MockKRule(this)

    override fun setup() {
        super.setup()

        coEvery {
            permissionManager.hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } returns true

        coEvery {
            permissionManager.requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } returns PermissionStatus.Granted

        coEvery {
            permissionManager.hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } returns false

        coEvery {
            permissionManager.requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } returns PermissionStatus.Denied

        ActivityProvider.init(context as Application)
    }

    @Test
    fun checkPermission() {
        test {
            val result = permissionManager.hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            assert(result)
        }
    }

    @Test
    fun requestPermissionGranted() {
        ActivityScenario.launch(FragmentActivity::class.java).use {
            test {
                val requestPermissionResult = permissionManager.requestPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                assertEquals(requestPermissionResult, PermissionStatus.Granted)

                val hasPermissionResult = permissionManager.hasPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                assert(hasPermissionResult)
            }
        }
    }

    @Test
    fun requestPermissionDenied() {
        ActivityScenario.launch(FragmentActivity::class.java).use {
            test {
                val requestPermissionResult = permissionManager.requestPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                )
                assertNotEquals(requestPermissionResult, PermissionStatus.Granted)

                val hasPermissionResult = permissionManager.hasPermission(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                )
                assert(!hasPermissionResult)
            }
        }
    }
}
