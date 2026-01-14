package com.core.data.session.manager

import com.core.data.network.model.ApiResult
import com.core.data.session.model.SessionCommand
import com.core.data.session.model.SessionData
import com.core.data.session.model.SessionState
import kotlinx.coroutines.flow.Flow

interface ISessionManager {
    /**
     * Represents the current state of the user's session.
     *
     * This property holds the current state of the session, which can be one of the following:
     * - [SessionState.Active]: The user is logged in and has an active session.
     * - [SessionState.Inactive]: The user is not logged in.
     *
     * @see SessionState
     */
    var sessionState: SessionState

    /**
     * A flow of [SessionCommand]s.
     *
     * This property emits commands that should be executed by the session.
     * These commands can represent various actions, such as starting, stopping,
     * pausing, or resuming a session, or any other session-related operations.
     *
     * The flow can be observed to receive and react to these commands as they are
     * emitted. Each element emitted is a [SessionCommand] instance.
     *
     * Example Usage:
     * ```kotlin
     * lifecycleScope.launch {
     *   mySessionManager.commands.collect { command ->
     *     when (command) {
     *       is SessionCommand.Warning -> show session is about to expire
     *       is SessionCommand.TimeOut -> show session has expired
     *     }
     *   }
     * }
     * ```
     *
     * @see SessionCommand
     */
    val commands: Flow<SessionCommand>

    /**
     * Starts a new user session.
     *
     * This function initiates a session for a specific user using the provided credentials.
     * It is a suspending function, meaning it can be paused and resumed without blocking the main thread.
     * This is typically used for network operations or other long-running tasks.
     *
     * @param user The username or identifier of the user attempting to start a session.
     *             This is usually a unique identifier within your application's user base.
     * @param token The authentication token associated with the user.
     *              This token is typically obtained after successful user login and is used
     *              to authorize subsequent requests.
     */
    suspend fun startSession(user: String, token: String)

    /**
     * Invalidates the current user's session.
     *
     * This function performs the necessary actions to log the user out and clear any stored session data.
     * After calling this function, the user will typically be required to log in again to access protected resources.
     *
     * This function is a suspending function and must be called within a coroutine or another suspending function.
     *
     * Common actions performed by this function may include:
     *  - Clearing authentication tokens (e.g., JWT).
     *  - Removing session data from persistent storage (e.g., SharedPreferences, database).
     *  - Invalidating server-side sessions if applicable.
     *  - Navigating the user to a login screen.
     */
    suspend fun invalidateSession()

    /**
     * Restarts the session timeout counter.
     *
     * This function is responsible for resetting the timer that tracks the duration
     * of the user's session. It's typically called after any user activity, such as
     * navigating to a new screen, interacting with a button, or sending a network request.
     *
     * By restarting the timeout, we ensure that the user's session remains active for
     * the designated period after their last activity. If the timeout period elapses
     * without a restart, the session will typically be considered expired, and the
     * user may be logged out or required to re-authenticate.
     *
     * This function is designed to be called in a suspending context, implying it might
     * perform asynchronous operations, such as updating an internal state or interacting
     * with a background service.
     *
     * Example usage:
     *
     * ```kotlin
     * lifecycleScope.launch {
     *     restartSessionTimeout()
     * }
     * ```
     *
     * Note: The specific behavior when the session expires (e.g., logging the user
     * out, prompting for re-authentication) is not handled by this function but rather
     * by separate logic that monitors the timeout status.
     */
    suspend fun restartSessionTimeout()

    /**
     * Retrieves the current user session data.
     *
     * This function fetches the session information associated with the currently
     * logged-in user (if any). It performs a network request to obtain the
     * session details.
     *
     * @return An [ApiResult] object containing either:
     *         - [ApiResult.Success]: If the session data was successfully retrieved.
     *           The `data` property within will hold the [SessionData] object.
     *         - [ApiResult.Error]: If an error occurred during the retrieval process.
     *           The `exception` property within will hold the exception that caused the failure.
     *
     * @see SessionData
     * @see ApiResult
     */
    suspend fun getCurrentSession(): ApiResult<SessionData>
}
