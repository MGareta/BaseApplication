package com.core.presentation.ui.components.alert.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
sealed interface AlertFamilyType {
    data class TopTrigger(
        val onDismiss: () -> Unit
    ) : AlertFamilyType

    data class Message(
        val trailingComponent: (@Composable () -> Unit)?
    ) : AlertFamilyType

    data class Modal(
        val onDismiss: () -> Unit
    ) : AlertFamilyType
}
