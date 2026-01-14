package com.core.presentation.ui.theme.palette.content

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.turquoise.TurquoiseColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class ContentPalette(
    val high: Color,
    val mid: Color,
    val low: Color,
    val accent: Color,
    val contrast: Color,
    val inverse: Color,
    val alwaysLight: Color,
    val alwaysDark: Color,
    val stateDisabled: Color,
)

val lightContentPalette = ContentPalette(
    high = GrayColors.gray100,
    mid = GrayColors.gray80,
    low = GrayColors.gray60,
    accent = TurquoiseColors.turquoise70,
    contrast = YellowColors.yellow70,
    inverse = Color.White,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    stateDisabled = GrayColors.gray40,
)

val darkContentPalette = ContentPalette(
    high = Color.White,
    mid = GrayColors.gray20,
    low = GrayColors.gray30,
    accent = TurquoiseColors.turquoise60,
    contrast = YellowColors.yellow50,
    inverse = GrayColors.gray100,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    stateDisabled = GrayColors.gray60,
)
