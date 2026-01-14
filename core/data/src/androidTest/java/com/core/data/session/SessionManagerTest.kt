package com.core.data.session

import androidx.fragment.app.FragmentActivity
import androidx.test.core.app.ActivityScenario
import com.core.data.BaseTest
import com.core.data.network.model.ApiResult
import com.core.data.session.manager.ISessionManager
import com.core.data.session.model.SessionData
import com.core.data.session.model.SessionState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.inject.Inject

private const val USER = "Test"
private const val TOKEN = "1234567890"

@HiltAndroidTest
class SessionManagerTest : BaseTest() {
    @Inject
    lateinit var sessionManager: ISessionManager

    @MockK
    lateinit var sessionData: SessionData

    override val hiltRule: HiltAndroidRule
        get() = HiltAndroidRule(this)

    override val mockkRule: MockKRule
        get() = MockKRule(this)

    override fun setup() {
        super.setup()

        every { sessionData.user } returns USER
        every { sessionData.token } returns TOKEN
    }

    @Test
    fun getDefaultSessionState() {
        test {
            assertEquals(sessionManager.sessionState, SessionState.Inactive)
        }
    }

    @Test
    fun startSession() {
        ActivityScenario.launch(FragmentActivity::class.java).onActivity {
            test {
                sessionManager.startSession(
                    user = sessionData.user,
                    token = sessionData.token,
                )

                assertEquals(sessionManager.sessionState, SessionState.Active)
            }
        }
    }

    @Test
    fun invalidateSession() {
        ActivityScenario.launch(FragmentActivity::class.java).onActivity {
            test {
                sessionManager.startSession(
                    user = sessionData.user,
                    token = sessionData.token,
                )

                assertEquals(sessionManager.sessionState, SessionState.Active)

                sessionManager.invalidateSession()

                assertEquals(sessionManager.sessionState, SessionState.Inactive)
            }
        }
    }

    @Test
    fun getCurrentSession() {
        ActivityScenario.launch(FragmentActivity::class.java).onActivity {
            test {
                sessionManager.startSession(
                    user = sessionData.user,
                    token = sessionData.token,
                )

                assertEquals(sessionManager.sessionState, SessionState.Active)

                val currentSession = sessionManager.getCurrentSession()

                assert(currentSession is ApiResult.Success)
                assertEquals((currentSession as ApiResult.Success).data.user, sessionData.user)
                assertEquals(currentSession.data.token, sessionData.token)
            }
        }
    }
}
