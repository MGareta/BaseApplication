package com.core.presentation.ui.theme.palette.background.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
interface BaseBackgroundPalette {
    val base: Color
    val surface: Color
    val accent: Color
    val accentLow: Color
    val successLow: Color
    val errorLow: Color
    val warningLow: Color
    val infoLow: Color
    val overlay: Color
    val disabled: Color
}