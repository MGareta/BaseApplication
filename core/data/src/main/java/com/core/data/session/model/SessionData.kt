package com.core.data.session.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class SessionData(
    val user: String, val token: String
)