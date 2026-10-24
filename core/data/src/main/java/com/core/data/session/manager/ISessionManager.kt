package com.core.data.session.manager

import com.core.data.network.model.ApiResult
import com.core.data.session.model.SessionCommand
import com.core.data.session.model.SessionData
import com.core.data.session.model.SessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
interface ISessionManager {

    var sessionState: SessionState

    val commands: Flow<SessionCommand>

    suspend fun startSession(user: String, token: String)

    suspend fun invalidateSession()

    suspend fun restartSessionTimeout()

    suspend fun getCurrentSession(): ApiResult<SessionData>
}
