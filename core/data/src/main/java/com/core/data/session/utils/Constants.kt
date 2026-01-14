package com.core.data.session.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object Constants {
    val SESSION_CACHE_KEY = "SESSION" + Uuid.random().toHexString()
}
