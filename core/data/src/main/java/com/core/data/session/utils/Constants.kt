package com.core.data.session.utils

import kotlin.uuid.Uuid

object Constants {
    val SESSION_CACHE_KEY = "SESSION" + Uuid.random().toHexString()
}
