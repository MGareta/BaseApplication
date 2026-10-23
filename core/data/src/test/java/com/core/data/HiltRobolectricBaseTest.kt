package com.core.data

import android.app.Application
import android.content.Context
import com.core.data.utils.ActivityProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
abstract class HiltRobolectricBaseTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope: TestScope = TestScope(
        context = testDispatcher + Job()
    )

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @ApplicationContext
    @Inject
    lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(
            dispatcher = testDispatcher
        )

        hiltRule.inject()
        ActivityProvider.init(context as Application)

        initDependencies()
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    fun test(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest {
        block()
    }

    abstract fun initDependencies()
}
