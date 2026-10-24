package com.core.data.permission.manager

import com.core.data.permission.model.PermissionStatus

interface IPermissionManager {

    fun hasPermission(
        permission: String
    ): Boolean

    suspend fun requestPermission(
        permission: String
    ): PermissionStatus

    suspend fun requestPermissions(
        vararg permissions: String
    ): Map<String, PermissionStatus>
}
