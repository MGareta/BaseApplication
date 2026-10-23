package com.core.data.environment

import com.core.data.HiltRobolectricBaseTest
import com.core.data.environment.manager.IEnvironmentManager
import com.core.data.environment.model.Environment
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class EnvironmentManagerTest : HiltRobolectricBaseTest() {

    @Inject
    lateinit var environmentManager: IEnvironmentManager

    override fun initDependencies() = Unit

    @Test
    fun getDefaultEnvironment() = test {
        assertEquals(Environment.DEV(), environmentManager.environment)
    }

    @Test
    fun setDevEnvironment() = test {
        environmentManager.environment = Environment.DEV()

        assertEquals(Environment.DEV(), environmentManager.environment)
    }

    @Test
    fun setUatEnvironment() = test {
        environmentManager.environment = Environment.UAT()

        assertEquals(Environment.UAT(), environmentManager.environment)
    }

    @Test
    fun setProdEnvironment() = test {
        environmentManager.environment = Environment.PROD()

        assertEquals(Environment.PROD(), environmentManager.environment)
    }
}
