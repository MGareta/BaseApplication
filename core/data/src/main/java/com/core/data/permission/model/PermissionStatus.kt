package com.core.data.permission.model

sealed class PermissionStatus {
    data object Granted : PermissionStatus()

    data object Denied : PermissionStatus()

    data object ShowRationale : PermissionStatus()
}
