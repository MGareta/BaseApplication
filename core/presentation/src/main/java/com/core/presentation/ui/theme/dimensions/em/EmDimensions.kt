package com.core.presentation.ui.theme.dimensions.em

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em

@Immutable
data class EmDimensions(
    val tight: TextUnit = 1.2.em,
    val normal: TextUnit = 1.4.em,
    val relaxed: TextUnit = 1.5.em
)
