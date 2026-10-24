package com.core.data.session

import android.os.Looper
import com.core.data.HiltRobolectricBaseTest
import com.core.data.network.model.ApiResult
import com.core.data.session.manager.ISessionManager
import com.core.data.session.model.SessionCommand
import com.core.data.session.model.SessionData
import com.core.data.session.model.SessionState
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val USER = "Test user"
private const val TOKEN = "test-token"

@OptIn(InternalSerializationApi::class)
@HiltAndroidTest
class SessionManagerTest : HiltRobolectricBaseTest() {

    @Inject
    lateinit var sessionManager: ISessionManager

    override fun initDependencies() = Unit

    @Test
    fun startsInactiveWithoutCachedSession() = test {
        sessionManager.invalidateSession()

        assertEquals(SessionState.Inactive, sessionManager.sessionState)
        assertTrue(sessionManager.getCurrentSession() is ApiResult.NoContent)
    }

    @Test
    fun startSessionActivatesAndCachesSessionData() = test {
        sessionManager.invalidateSession()

        try {
            sessionManager.startSession(
                user = USER,
                token = TOKEN
            )

            assertEquals(SessionState.Active, sessionManager.sessionState)
            assertEquals(
                ApiResult.Success(data = SessionData(user = USER, token = TOKEN)),
                sessionManager.getCurrentSession()
            )
        } finally {
            sessionManager.invalidateSession()
        }
    }

    @Test
    fun invalidateSessionDeactivatesAndRemovesCachedData() = test {
        sessionManager.invalidateSession()
        sessionManager.startSession(
            user = USER,
            token = TOKEN
        )

        sessionManager.invalidateSession()

        assertEquals(SessionState.Inactive, sessionManager.sessionState)
        assertTrue(sessionManager.getCurrentSession() is ApiResult.NoContent)
    }

    @Test
    fun sessionTimerEmitsWarningAndTimeoutCommands() = test {
        sessionManager.invalidateSession()
        sessionManager.startSession(
            user = USER,
            token = TOKEN
        )

        val commands = mutableListOf<SessionCommand>()
        val commandCollector = launch(start = CoroutineStart.UNDISPATCHED) {
            sessionManager.commands
                .take(count = 2)
                .toList(destination = commands)
        }

        try {
            shadowOf(Looper.getMainLooper())
                .idleFor(
                    3,
                    TimeUnit.SECONDS
                )

            assertEquals(listOf(SessionCommand.Warning), commands)

            shadowOf(Looper.getMainLooper())
                .idleFor(
                    2,
                    TimeUnit.SECONDS
                )

            commandCollector.join()

            assertEquals(
                listOf(SessionCommand.Warning, SessionCommand.TimeOut),
                commands
            )
        } finally {
            commandCollector.cancel()
            sessionManager.invalidateSession()
        }
    }

    @Test
    fun restartSessionTimeoutIsNoOpWhenInactiveAndResetsActiveTimer() = test {
        sessionManager.invalidateSession()
        sessionManager.restartSessionTimeout()

        assertEquals(SessionState.Inactive, sessionManager.sessionState)

        sessionManager.startSession(
            user = USER,
            token = TOKEN
        )

        val commands = mutableListOf<SessionCommand>()
        val commandCollector = launch(start = CoroutineStart.UNDISPATCHED) {
            sessionManager.commands
                .take(count = 2)
                .toList(destination = commands)
        }

        try {
            shadowOf(Looper.getMainLooper())
                .idleFor(
                    2,
                    TimeUnit.SECONDS
                )

            sessionManager.restartSessionTimeout()

            shadowOf(Looper.getMainLooper())
                .idleFor(
                    2999,
                    TimeUnit.MILLISECONDS
                )

            assertTrue(commands.isEmpty())

            shadowOf(Looper.getMainLooper())
                .idleFor(
                    1,
                    TimeUnit.MILLISECONDS
                )

            assertEquals(listOf(SessionCommand.Warning), commands)

            shadowOf(Looper.getMainLooper())
                .idleFor(
                    2,
                    TimeUnit.SECONDS
                )

            commandCollector.join()

            assertEquals(
                listOf(SessionCommand.Warning, SessionCommand.TimeOut),
                commands,
            )
        } finally {
            commandCollector.cancel()

            sessionManager.invalidateSession()
        }
    }
}
