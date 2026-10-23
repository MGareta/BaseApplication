package com.core.data.download.utils

import androidx.core.content.FileProvider

internal fun clearFileProviderCache() {
    val cacheField = FileProvider::class.java
        .getDeclaredField("sCache")
        .apply {
            isAccessible = true
        }

    (cacheField.get(null) as MutableMap<*, *>).clear()
}
