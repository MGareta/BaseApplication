package com.core.presentation.ui.theme.palette.border

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.turquoise.TurquoiseColors

data class BorderPalette(
    val high: Color,
    val mid: Color,
    val low: Color,
    val accent: Color,
    val inverse: Color,
    val alwaysLight: Color,
    val alwaysDark: Color,
    val stateDisabled: Color,
)

val lightBorderPalette = BorderPalette(
    high = GrayColors.gray100,
    mid = GrayColors.gray40,
    low = GrayColors.gray20,
    accent = TurquoiseColors.turquoise70,
    inverse = Color.White,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    stateDisabled = GrayColors.gray30,
)

val darkBorderPalette = BorderPalette(
    high = Color.White,
    mid = GrayColors.gray60,
    low = GrayColors.gray80,
    accent = TurquoiseColors.turquoise70,
    inverse = GrayColors.gray100,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    stateDisabled = GrayColors.gray70,
)
