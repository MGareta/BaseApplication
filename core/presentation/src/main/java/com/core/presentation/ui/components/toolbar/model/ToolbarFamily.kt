package com.core.presentation.ui.components.toolbar.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
sealed interface ToolbarFamilyType {
    data class Default(
        val content: @Composable () -> Unit
    ) : ToolbarFamilyType

    data class Animate(
        val expandedContent: @Composable () -> Unit,
        val collapsedContent: @Composable () -> Unit
    ) : ToolbarFamilyType
}
