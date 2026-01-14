package com.core.data.session.model

sealed interface SessionState {
    data object Active : SessionState

    data object Inactive : SessionState
}
