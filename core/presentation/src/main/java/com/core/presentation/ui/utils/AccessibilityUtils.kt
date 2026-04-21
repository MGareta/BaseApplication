package com.core.presentation.ui.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AccessibilityState {
    data class Click(
        val disabledReason: String
    )

    data class Select(
        val selected: String,
        val notSelected: String
    ) : AccessibilityState
}