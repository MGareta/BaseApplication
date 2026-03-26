package com.core.presentation.ui.theme.palette.background

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors
import com.core.presentation.ui.theme.palette.background.base.BaseBackgroundPalette

@Immutable
sealed class LightBackgroundPalette(
    override val base: Color,
    override val surface: Color,
    override val accent: Color,
    override val accentLow: Color,
    override val successLow: Color,
    override val errorLow: Color,
    override val warningLow: Color,
    override val infoLow: Color,
    override val overlay: Color,
    override val disabled: Color
) : BaseBackgroundPalette {

    data object DefaultContrast : LightBackgroundPalette(
        base = Color.White,
        surface = GrayColors.gray10,
        accent = GreenColors.green50,
        accentLow = GreenColors.green10,
        successLow = GreenColors.green10,
        errorLow = RedColors.red10,
        warningLow = YellowColors.yellow10,
        infoLow = BlueColors.blue10,
        overlay = Color.Black.copy(alpha = 0.35f),
        disabled = GrayColors.gray20
    )

    data object MediumContrast : LightBackgroundPalette(
        base = Color.White,
        surface = Color.White,
        accent = GreenColors.green60,
        accentLow = GreenColors.green10,
        successLow = GreenColors.green10,
        errorLow = RedColors.red10,
        warningLow = YellowColors.yellow10,
        infoLow = BlueColors.blue10,
        overlay = Color.Black.copy(alpha = 0.35f),
        disabled = GrayColors.gray30
    )


    data object HighContrast : LightBackgroundPalette(
        base = Color.White,
        surface = Color.White,
        accent = GreenColors.green80,
        accentLow = GreenColors.green10,
        successLow = GreenColors.green10,
        errorLow = RedColors.red10,
        warningLow = YellowColors.yellow10,
        infoLow = BlueColors.blue10,
        overlay = Color.Black.copy(alpha = 0.35f),
        disabled = GrayColors.gray40
    )
}