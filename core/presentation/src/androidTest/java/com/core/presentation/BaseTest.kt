package com.core.presentation

import dagger.hilt.android.testing.HiltAndroidRule
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

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {
    @get:Rule
    abstract val hiltRule: HiltAndroidRule

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope: TestScope = TestScope(context = testDispatcher + Job())

    @Before
    fun setup() {
        hiltRule.inject()

        Dispatchers.setMain(dispatcher = testDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    fun test(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest {
        block()
    }
}
