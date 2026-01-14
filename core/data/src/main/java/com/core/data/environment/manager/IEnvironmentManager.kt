package com.core.data.environment.manager

import com.core.data.environment.model.Environment

interface IEnvironmentManager {
    /**
     * The environment in which the application is currently running.
     * This property determines which configuration and resources are used.
     * By default, it is set to the development environment ([Environment.DEV]).
     *
     * Possible environment values are:
     * - [Environment.DEV]: Development environment, typically used during local development.
     * - [Environment.UAT]: Testing environment, for automated or manual testing.
     * - [Environment.PROD]: Production environment, the live environment for end-users.
     */
    var environment: Environment
}
