package com.core.presentation.ui.theme.palette.semantic

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class SemanticBorderPalette(val success: Color, val danger: Color, val info: Color, val warning: Color)

val lightSemanticBorderPalette = SemanticBorderPalette(
    success = GreenColors.green70,
    danger = RedColors.red70,
    info = BlueColors.blue70,
    warning = YellowColors.yellow60,
)

val darkSemanticBorderPalette = SemanticBorderPalette(
    success = GreenColors.green70,
    danger = RedColors.red70,
    info = BlueColors.blue70,
    warning = YellowColors.yellow60,
)
