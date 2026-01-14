package com.core.presentation.ui.components.tab.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class TabTitle(val text: String, @param:DrawableRes val icon: Int?)
