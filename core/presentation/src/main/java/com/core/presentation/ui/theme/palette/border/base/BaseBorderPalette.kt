package com.core.presentation.ui.theme.palette.border.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
interface BaseBorderPalette {
    val subtle: Color
    val standard: Color
    val onAccent: Color
    val active: Color
    val error: Color
    val disabled: Color
}