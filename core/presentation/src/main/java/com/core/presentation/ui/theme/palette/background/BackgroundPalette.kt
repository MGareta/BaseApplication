package com.core.presentation.ui.theme.palette.background

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.turquoise.TurquoiseColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class BackgroundPalette(
    val base: Color,
    val secondary: Color,
    val tertiary: Color,
    val accent: Color,
    val accentLow: Color,
    val contrast: Color,
    val contrastLow: Color,
    val inverse: Color,
    val alwaysLight: Color,
    val alwaysDark: Color,
    val overlay: Color,
    val stateDisabled: Color,
)

val lightBackgroundPalette = BackgroundPalette(
    base = Color.White,
    secondary = GrayColors.gray10,
    tertiary = GrayColors.gray20,
    accent = TurquoiseColors.turquoise70,
    accentLow = TurquoiseColors.turquoise10,
    contrast = YellowColors.yellow70,
    contrastLow = YellowColors.yellow10,
    inverse = GrayColors.gray100,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    overlay = Color(
        red = GrayColors.gray100.red,
        green = GrayColors.gray100.green,
        blue = GrayColors.gray100.blue,
        alpha = .35F,
    ),
    stateDisabled = GrayColors.gray10,
)

val darkBackgroundPalette = BackgroundPalette(
    base = GrayColors.gray100,
    secondary = GrayColors.gray90,
    tertiary = GrayColors.gray80,
    accent = TurquoiseColors.turquoise70,
    accentLow = TurquoiseColors.turquoise100,
    contrast = YellowColors.yellow70,
    contrastLow = YellowColors.yellow90,
    inverse = Color.White,
    alwaysLight = Color.White,
    alwaysDark = GrayColors.gray100,
    overlay = Color(
        red = GrayColors.gray80.red,
        green = GrayColors.gray80.green,
        blue = GrayColors.gray80.blue,
        alpha = .5F,
    ),
    stateDisabled = GrayColors.gray90,
)
