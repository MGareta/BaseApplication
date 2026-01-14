package com.core.data

import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.junit4.MockKRule
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
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {
    @get:Rule
    abstract val hiltRule: HiltAndroidRule

    @get:Rule
    abstract val mockkRule: MockKRule

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope: TestScope = TestScope(context = testDispatcher + Job())

    @Before
    open fun setup() {
        hiltRule.inject()

        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @After
    open fun cleanup() {
        Dispatchers.resetMain()
    }

    fun test(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest(timeout = Duration.INFINITE) {
        block()
    }
}
