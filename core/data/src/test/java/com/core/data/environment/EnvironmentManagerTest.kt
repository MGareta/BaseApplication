package com.core.data.environment

import com.core.data.BaseTest
import com.core.data.environment.manager.EnvironmentManagerImpl
import com.core.data.environment.manager.IEnvironmentManager
import com.core.data.environment.model.Environment
import org.junit.Assert.assertEquals
import org.junit.Test

class EnvironmentManagerTest : BaseTest() {
    private lateinit var environmentManager: IEnvironmentManager

    override fun initDependencies() {
        environmentManager = EnvironmentManagerImpl()
    }

    @Test
    fun getDefaultEnvironment() {
        test {
            assertEquals(environmentManager.environment, Environment.DEV())
        }
    }

    @Test
    fun setDevEnvironment() {
        test {
            environmentManager.environment = Environment.DEV()
            assertEquals(environmentManager.environment, Environment.DEV())
        }
    }

    @Test
    fun setUatEnvironment() {
        test {
            environmentManager.environment = Environment.UAT()
            assertEquals(environmentManager.environment, Environment.UAT())
        }
    }

    @Test
    fun setProdEnvironment() {
        test {
            environmentManager.environment = Environment.PROD()
            assertEquals(environmentManager.environment, Environment.PROD())
        }
    }
}
