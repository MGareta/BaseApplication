package com.core.data.session.manager

import android.os.CountDownTimer
import com.core.data.cache.manager.ICacheManager
import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ImmortalCacheConfig
import com.core.data.network.model.ApiResult
import com.core.data.session.model.SessionCommand
import com.core.data.session.model.SessionData
import com.core.data.session.model.SessionState
import com.core.data.session.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

private const val COUNTDOWN_TIMEOUT = 5 * 1000L
private const val COUNTDOWN_WARNING = 3 * 1000L

class SessionManagerImpl(private val cacheManager: ICacheManager) : ISessionManager {
    /**
     * A channel for sending commands to the session.
     *
     * This is a conflated channel, meaning that if a new command is sent
     * before the previous one is consumed, the previous command will be
     * replaced by the new one. This ensures that only the latest command
     * is processed, which is useful for situations where only the most
     * recent instruction is relevant.
     */
    private val _commands: Channel<SessionCommand> = Channel(capacity = Channel.CONFLATED)

    /**
     * A CountDownTimer that triggers events based on the remaining time.
     *
     * This timer is responsible for:
     * - Sending a [SessionCommand.Warning] when the remaining time is less than or equal to [COUNTDOWN_WARNING].
     * - Sending a [SessionCommand.TimeOut] when the timer finishes (reaches 0).
     *
     * It's initialized lazily, meaning the timer object is created only when it's first accessed.
     *
     * The timer is configured with:
     *  - `COUNTDOWN_TIMEOUT`: The total duration of the countdown in milliseconds.
     *  - `COUNTDOWN_WARNING`: The threshold in milliseconds at which the warning command should be triggered.
     *
     * The `onTick` callback is invoked at each tick interval (defined by the `COUNTDOWN_WARNING` parameter when the timer is created).
     * Inside `onTick` it checks if the remaining time is under the warning threshold.
     * The `onFinish` callback is invoked when the timer completes.
     */
    private val countDownTimer: CountDownTimer by lazy {
        object : CountDownTimer(COUNTDOWN_TIMEOUT, COUNTDOWN_WARNING) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished <= COUNTDOWN_WARNING) {
                    _commands.trySend(
                        element = SessionCommand.Warning,
                    )
                }
            }

            override fun onFinish() {
                _commands.trySend(
                    element = SessionCommand.TimeOut,
                )
            }
        }
    }

    override var sessionState: SessionState = SessionState.Inactive

    override val commands: Flow<SessionCommand>
        get() = _commands.receiveAsFlow()

    /**
     * **How it works:**
     * This function performs the following actions:
     * 1. Starts the internal countdown timer.
     * 2. Sets the session state to `SessionState.Active`.
     * 3. Caches the user's session data (username and token) in the memory cache using [ImmortalCacheConfig].
     *
     * @see SessionState
     * @see ImmortalCacheConfig
     * @see CacheScope
     * @see SessionData
     * @see countDownTimer
     * @see cacheManager
     */
    override suspend fun startSession(user: String, token: String) {
        countDownTimer.start()
        sessionState = SessionState.Active
        cacheManager.cacheData(
            scope = CacheScope.MEMORY_CACHE,
            config = ImmortalCacheConfig(
                key = Constants.SESSION_CACHE_KEY,
            ),
            data = SessionData(
                user = user,
                token = token,
            ),
        )
    }

    /**
     * **How it works:**
     * This function performs the following actions:
     * 1. **Cancels the countdown timer:**  If a countdown timer is active (e.g., for session expiration), it will be stopped.
     * 2. **Sets the session state to Inactive:** This indicates that there is no valid user session.
     * 3. **Deletes the session data from the cache:** This removes any cached data associated with the current session from the in-memory cache, identified by the `Constants.SESSION_CACHE_KEY`.
     *
     * This function is typically called when a user logs out, a session expires, or a significant change in authentication status occurs.
     *
     * Note: This is a suspend function, meaning it can be safely called from a coroutine.
     *
     * @see ICacheManager.deleteData
     * @see CacheScope.MEMORY_CACHE
     * @see SessionState.Inactive
     * @see Constants.SESSION_CACHE_KEY
     */
    override suspend fun invalidateSession() {
        countDownTimer.cancel()
        sessionState = SessionState.Inactive
        cacheManager.deleteData(
            scope = CacheScope.MEMORY_CACHE,
            key = Constants.SESSION_CACHE_KEY,
        )
    }

    /**
     * **How it works:**
     * This function is responsible for resetting the countdown timer that governs the
     * session's active duration. It first checks if the session is currently in an
     * inactive state. If it is, the function returns immediately, as there's no
     * need to restart the timeout for an inactive session.
     *
     * If the session is active, the function proceeds to interact with the
     * `countDownTimer` object. It first cancels any ongoing countdown and then
     * initiates a new countdown from the beginning. This effectively resets the
     * session timeout, extending its active duration.
     */
    override suspend fun restartSessionTimeout() {
        if (sessionState == SessionState.Inactive) {
            return
        }

        with(countDownTimer) {
            cancel()
            start()
        }
    }

    /**
     * **How it works:**
     * This function fetches the session data associated with the [Constants.SESSION_CACHE_KEY] from the cache.
     * It prioritizes the memory cache ([CacheScope.MEMORY_CACHE]) for fast retrieval.
     */
    override suspend fun getCurrentSession(): ApiResult<SessionData> = cacheManager.getData(
        scope = CacheScope.MEMORY_CACHE,
        key = Constants.SESSION_CACHE_KEY,
        kClass = SessionData::class,
    )
}
