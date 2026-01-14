package com.core.data

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

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest {
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testCoroutineScope: TestScope = TestScope(context = testDispatcher + Job())

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher = testDispatcher)

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
