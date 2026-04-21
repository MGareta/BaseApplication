package com.core.presentation.ui.utils

import androidx.compose.runtime.Immutable

@Immutable
data class AccessibilityLabel(
    val enabled: String?,
    val disabled: String
)

@Immutable
data class AccessibilityStateDescription(
    val selected: String,
    val notSelected: String
)

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