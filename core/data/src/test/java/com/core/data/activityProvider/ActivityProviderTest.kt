package com.core.data.activityProvider

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.core.data.BaseTest
import com.core.data.utils.ActivityProvider
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.robolectric.Robolectric

@HiltAndroidTest
class ActivityProviderTest : BaseTest() {

    override fun initDependencies() = Unit

    @Test
    fun initStoresApplicationContext() = test {
        assertSame(context, ActivityProvider.applicationContext)
    }

    @Test
    fun tracksFragmentActivityAndClearsCurrentActivityAfterDestroy() = test {
        val activityController = Robolectric.buildActivity(FragmentActivity::class.java).setup()
        val activity = activityController.get()

        try {
            assertSame(activity, ActivityProvider.currentActivity)
            assertSame(activity, ActivityProvider.activityFlow.first())
        } finally {
            activityController
                .pause()
                .stop()
                .destroy()
        }

        assertNull(ActivityProvider.currentActivity)
    }

    @Test
    fun ignoresActivitiesThatAreNotFragmentActivities() = test {
        val activityController = Robolectric.buildActivity(Activity::class.java).setup()

        try {
            assertNull(ActivityProvider.currentActivity)
        } finally {
            activityController
                .pause()
                .stop()
                .destroy()
        }
    }
}
