package com.core.data.cache.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import java.util.Calendar
import kotlin.time.Duration

@OptIn(InternalSerializationApi::class)
@Serializable
data class CacheEntry(val value: String, val duration: Duration) {
    val timestamp: Long = Calendar.getInstance().time.time
}
