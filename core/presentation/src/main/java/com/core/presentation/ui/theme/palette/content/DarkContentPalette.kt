package com.core.presentation.ui.theme.palette.content

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.blue.BlueColors
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.colors.yellow.YellowColors
import com.core.presentation.ui.theme.palette.border.base.BaseBorderPalette
import com.core.presentation.ui.theme.palette.content.base.BaseContentPalette

@Immutable
sealed class DarkContentPalette(
    override val primary: Color,
    override val secondary: Color,
    override val link: Color,
    override val accent: Color,
    override val disabled: Color,
    override val onAccent: Color,
    override val success: Color,
    override val error: Color,
    override val warning: Color,
    override val info: Color
) : BaseContentPalette {

    data object DefaultContrast : DarkContentPalette(
        primary = GrayColors.gray10,
        secondary = GrayColors.gray40,
        link = BlueColors.blue40,
        accent = GreenColors.green50,
        disabled = GrayColors.gray60,
        onAccent = Color.Black,
        success = GreenColors.green30,
        error = RedColors.red30,
        warning = YellowColors.yellow30,
        info = BlueColors.blue30
    )

    data object MediumContrast : DarkContentPalette(
        primary = GrayColors.gray10,
        secondary = GrayColors.gray20,
        link = BlueColors.blue30,
        accent = GreenColors.green40,
        disabled = GrayColors.gray60,
        onAccent = Color.Black,
        success = GreenColors.green20,
        error = RedColors.red20,
        warning = YellowColors.yellow30,
        info = BlueColors.blue30
    )


    data object HighContrast : DarkContentPalette(
        primary = Color.White,
        secondary = Color.White,
        link = BlueColors.blue10,
        accent = GreenColors.green20,
        disabled = GrayColors.gray60,
        onAccent = Color.Black,
        success = GreenColors.green10,
        error = RedColors.red10,
        warning = YellowColors.yellow30,
        info = BlueColors.blue30
    )
}