package com.core.presentation.ui.theme.palette.semantic

import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors

data class SemanticBackgroundPalette(
    val success: Color,
    val successLow: Color,
    val danger: Color,
    val dangerLow: Color,
    val info: Color,
    val infoLow: Color,
    val warning: Color,
    val warningLow: Color,
)

val lightSemanticBackgroundPalette = SemanticBackgroundPalette(
    success = GreenColors.green80,
    successLow = GreenColors.green10,
    danger = RedColors.red70,
    dangerLow = RedColors.red10,
    info = BlueColors.blue70,
    infoLow = BlueColors.blue10,
    warning = YellowColors.yellow70,
    warningLow = YellowColors.yellow10,
)

val darkSemanticBackgroundPalette = SemanticBackgroundPalette(
    success = GreenColors.green70,
    successLow = GreenColors.green100,
    danger = RedColors.red70,
    dangerLow = RedColors.red100,
    info = BlueColors.blue70,
    infoLow = BlueColors.blue100,
    warning = YellowColors.yellow70,
    warningLow = YellowColors.yellow90,
)
