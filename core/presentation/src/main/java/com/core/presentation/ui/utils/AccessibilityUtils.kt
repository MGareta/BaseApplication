package com.core.presentation.ui.utils

import androidx.compose.runtime.Immutable

@Immutable
data class AccessibilityLabel(
    val enabled: String,
    val disabled: String
)