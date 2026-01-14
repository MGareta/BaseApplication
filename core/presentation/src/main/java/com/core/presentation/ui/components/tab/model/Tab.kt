package com.core.presentation.ui.components.tab.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class Tab<T>(val data: T, val title: TabTitle, val content: @Composable (T) -> Unit)
