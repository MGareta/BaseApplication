package com.core.presentation.ui.components.list.utils

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

fun LazyListScope.Loading() {
    item {
        CircularProgressIndicator()
    }
}

fun LazyListScope.Error(message: String) {
    item {
        Text(
            text = message,
            color = LocalPalette.current.contentPalette.mid,
            style = LocalTypographies.current.mid.l,
        )
    }
}
