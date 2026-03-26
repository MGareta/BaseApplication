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
sealed class DarkBackgroundPalette(
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

    data object DefaultContrast : DarkBackgroundPalette(
        base = GrayColors.gray100,
        surface = GrayColors.gray90,
        accent = GreenColors.green50,
        accentLow = GreenColors.green90,
        successLow = GreenColors.green90,
        errorLow = RedColors.red90,
        warningLow = YellowColors.yellow90,
        infoLow = BlueColors.blue90,
        overlay = Color.Black.copy(alpha = 0.6f),
        disabled = GrayColors.gray90
    )

    data object MediumContrast : DarkBackgroundPalette(
        base = GrayColors.gray100,
        surface = GrayColors.gray100,
        accent = GreenColors.green40,
        accentLow = GreenColors.green90,
        successLow = GreenColors.green90,
        errorLow = RedColors.red90,
        warningLow = YellowColors.yellow90,
        infoLow = BlueColors.blue90,
        overlay = Color.Black.copy(alpha = 0.6f),
        disabled = GrayColors.gray80
    )


    data object HighContrast : DarkBackgroundPalette(
        base = Color.Black,
        surface = Color.Black,
        accent = GreenColors.green20,
        accentLow = Color.Black,
        successLow = GreenColors.green90,
        errorLow = RedColors.red90,
        warningLow = YellowColors.yellow90,
        infoLow = BlueColors.blue90,
        overlay = Color.Black.copy(alpha = 0.6f),
        disabled = GrayColors.gray70
    )
}
