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
import kotlinx.serialization.InternalSerializationApi

private const val COUNTDOWN_TIMEOUT = 5 * 1000L
private const val COUNTDOWN_WARNING = 3 * 1000L

@OptIn(InternalSerializationApi::class)
class SessionManagerImpl(
    private val cacheManager: ICacheManager
) : ISessionManager {

    private val _commands: Channel<SessionCommand> = Channel(capacity = Channel.CONFLATED)

    private val countDownTimer: CountDownTimer by lazy {
        object : CountDownTimer(COUNTDOWN_TIMEOUT, COUNTDOWN_WARNING) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished <= COUNTDOWN_WARNING) {
                    _commands.trySend(
                        element = SessionCommand.Warning
                    )
                }
            }

            override fun onFinish() {
                _commands.trySend(
                    element = SessionCommand.TimeOut
                )
            }
        }
    }

    override var sessionState: SessionState = SessionState.Inactive

    override val commands: Flow<SessionCommand>
        get() = _commands.receiveAsFlow()

    override suspend fun startSession(
        user: String,
        token: String
    ) {
        countDownTimer.start()
        sessionState = SessionState.Active
        cacheManager.cacheData(
            scope = CacheScope.MEMORY_CACHE,
            config = ImmortalCacheConfig(
                key = Constants.SESSION_CACHE_KEY
            ),
            data = SessionData(
                user = user,
                token = token
            )
        )
    }

    override suspend fun invalidateSession() {
        countDownTimer.cancel()

        sessionState = SessionState.Inactive

        cacheManager.deleteData(
            scope = CacheScope.MEMORY_CACHE,
            key = Constants.SESSION_CACHE_KEY
        )
    }

    override suspend fun restartSessionTimeout() {
        if (sessionState == SessionState.Inactive) {
            return
        }

        with(countDownTimer) {
            cancel()
            start()
        }
    }

    override suspend fun getCurrentSession(): ApiResult<SessionData> = cacheManager.getData(
        scope = CacheScope.MEMORY_CACHE,
        key = Constants.SESSION_CACHE_KEY,
        kClass = SessionData::class
    )
}
