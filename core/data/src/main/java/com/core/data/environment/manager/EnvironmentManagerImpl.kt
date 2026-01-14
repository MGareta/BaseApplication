package com.core.data.environment.manager

import com.core.data.environment.model.Environment

class EnvironmentManagerImpl : IEnvironmentManager {
    override var environment: Environment = Environment.DEV()
}
