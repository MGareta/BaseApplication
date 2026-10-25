package com.core.data.analytics

import com.core.data.BaseTest
import com.core.data.analytics.frameworks.IAnalyticsFramework
import com.core.data.analytics.manager.IAnalyticsManager
import com.core.data.analytics.model.AnalyticsFramework
import com.core.data.analytics.model.BaseAnalyticsEvent
import com.core.data.analytics.model.BaseAnalyticsView
import com.core.data.analytics.model.FirebaseAnalyticsEventTest
import com.core.data.analytics.model.FirebaseAnalyticsViewTest
import com.core.data.network.model.ApiResult
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AnalyticsManagerTest : BaseTest() {

    @Inject
    lateinit var analyticsManager: IAnalyticsManager

    @Inject
    lateinit var frameworks: Map<
        @JvmSuppressWildcards AnalyticsFramework,
        @JvmSuppressWildcards IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>
    >

    private val analyticsFramework: IAnalyticsFramework<BaseAnalyticsView, BaseAnalyticsEvent>
        get() = frameworks.getValue(AnalyticsFramework.FIREBASE)

    override fun initDependencies() = Unit

    @Test
    fun initializeReturnsSuccessWhenFrameworkInitializes() = test {
        every {
            analyticsFramework.initialize()
        } returns ApiResult.Success(data = Unit)

        val result = analyticsManager.initialize()

        assertEquals(ApiResult.Success(data = Unit), result)

        verify(exactly = 1) {
            analyticsFramework.initialize()
        }
    }

    @Test
    fun initializeReturnsFrameworkException() = test {
        val exception = IllegalStateException("Analytics initialization failed")
        val expected = ApiResult.Exception<Unit>(exception = exception)

        every {
            analyticsFramework.initialize()
        } returns expected

        val result = analyticsManager.initialize()

        assertEquals(expected, result)

        verify(exactly = 1) {
            analyticsFramework.initialize()
        }
    }

    @Test
    fun trackViewDelegatesToSelectedFramework() {
        val view = FirebaseAnalyticsViewTest(name = "Home")

        analyticsManager.trackView(
            framework = AnalyticsFramework.FIREBASE,
            view = view
        )

        verify(exactly = 1) {
            analyticsFramework.trackView(view = view)
        }
    }

    @Test
    fun trackEventDelegatesToSelectedFramework() {
        val event = FirebaseAnalyticsEventTest(name = "button_clicked")

        analyticsManager.trackEvent(
            framework = AnalyticsFramework.FIREBASE,
            event = event
        )

        verify(exactly = 1) {
            analyticsFramework.trackEvent(event = event)
        }
    }
}
