package com.core.presentation.ui.theme.palette.semantic

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class SemanticContentPalette(val success: Color, val danger: Color, val info: Color, val warning: Color)

val lightSemanticContentPalette = SemanticContentPalette(
    success = GreenColors.green80,
    danger = RedColors.red70,
    info = BlueColors.blue70,
    warning = YellowColors.yellow70,
)

val darkSemanticContentPalette = SemanticContentPalette(
    success = GreenColors.green60,
    danger = RedColors.red50,
    info = BlueColors.blue60,
    warning = YellowColors.yellow50,
)
