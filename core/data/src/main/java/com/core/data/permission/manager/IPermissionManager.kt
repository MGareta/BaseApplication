package com.core.data.permission.manager

import com.core.data.permission.model.PermissionStatus

interface IPermissionManager {
    /**
     * Checks if the application has the specified permission.
     *
     * This function determines whether the application has been granted a specific
     * permission at runtime. It's important to note that this function only indicates
     * if the permission has been granted, not if it's declared in the manifest.
     *
     * @param permission The name of the permission to check (e.g., "android.permission.CAMERA").
     * @return `true` if the application has the permission, `false` otherwise.
     *
     * @throws IllegalArgumentException if the provided permission string is empty or null
     */
    fun hasPermission(permission: String): Boolean

    /**
     * Requests a specific permission from the user.
     *
     * This function suspends execution until the user has granted or denied the requested permission.
     * It then returns a [PermissionStatus] indicating the outcome of the request.
     *
     * @param permission The permission to request (e.g., android.permission.CAMERA, android.permission.ACCESS_FINE_LOCATION).
     *                   Must be a valid permission string as defined in the AndroidManifest.xml.
     * @return A [PermissionStatus] representing the outcome of the permission request:
     *          - [PermissionStatus.Granted]: The user has granted the permission.
     *          - [PermissionStatus.Denied]: The user has denied the permission.
     *          - [PermissionStatus.ShowRationale]: The permission should display a rationale to the user before it is requested again.
     *
     * @throws SecurityException If the requested permission is not declared in the AndroidManifest.xml.
     * @throws IllegalArgumentException If the provided permission string is invalid or empty.
     */
    suspend fun requestPermission(permission: String): PermissionStatus

    /**
     * Requests the specified permissions from the user.
     *
     * This function suspends until the user has granted or denied all requested permissions.
     * It returns a map where the keys are the requested permissions and the values are their
     * corresponding [PermissionStatus].
     *
     * @param permissions A vararg of permission strings to request (e.g., Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION).
     * @return A map where the key is the permission string and the value is its [PermissionStatus].
     *         The [PermissionStatus] can be one of the following:
     *         - [PermissionStatus.Granted]: The permission has been granted by the user.
     *         - [PermissionStatus.Denied]: The permission has been denied by the user.
     *         - [PermissionStatus.ShowRationale]: The permission should display a rationale to the user before it is requested again.
     *
     * @throws SecurityException if any of the provided permissions are not declared in the AndroidManifest.xml.
     * @throws IllegalArgumentException if the permissions array is empty.
     *
     * Example Usage:
     * ```kotlin
     * val permissionResults = requestPermissions(
     *     Manifest.permission.CAMERA,
     *     Manifest.permission.ACCESS_FINE_LOCATION
     * )
     *
     * if (permissionResults[Manifest.permission.CAMERA] == PermissionStatus.Granted) {
     *     // Camera permission granted, proceed with camera operations
     * } else if (permissionResults[Manifest.permission.CAMERA] == PermissionStatus.Denied){
     *     // camera permission denied.
     * }
     *
     * if (permissionResults[Manifest.permission.ACCESS_FINE_LOCATION] == PermissionStatus.Granted) {
     *      // location permission granted.
     * }else if(permissionResults[Manifest.permission.ACCESS_FINE_LOCATION] == PermissionStatus.ShowRationale){
     *    // location permission show rationale needed.
     * }
     * ```
     *
     */
    suspend fun requestPermissions(vararg permissions: String): Map<String, PermissionStatus>
}
