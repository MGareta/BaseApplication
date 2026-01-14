package com.core.data.session.model

sealed interface SessionCommand {
    data object Warning : SessionCommand

    data object TimeOut : SessionCommand
}
