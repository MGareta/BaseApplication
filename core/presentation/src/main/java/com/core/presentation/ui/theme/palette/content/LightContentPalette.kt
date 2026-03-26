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
sealed class LightContentPalette(
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

    data object DefaultContrast : LightContentPalette(
        primary = GrayColors.gray100,
        secondary = GrayColors.gray70,
        link = BlueColors.blue50,
        accent = GreenColors.green50,
        disabled = GrayColors.gray40,
        onAccent = Color.Black,
        success = GreenColors.green80,
        error = RedColors.red80,
        warning = YellowColors.yellow90,
        info = BlueColors.blue80
    )

    data object MediumContrast : LightContentPalette(
        primary = GrayColors.gray100,
        secondary = GrayColors.gray90,
        link = BlueColors.blue70,
        accent = GreenColors.green60,
        disabled = GrayColors.gray40,
        onAccent = Color.Black,
        success = GreenColors.green90,
        error = RedColors.red90,
        warning = YellowColors.yellow90,
        info = BlueColors.blue80
    )


    data object HighContrast : LightContentPalette(
        primary = Color.Black,
        secondary = Color.Black,
        link = BlueColors.blue100,
        accent = GreenColors.green80,
        disabled = GrayColors.gray40,
        onAccent = Color.White,
        success = GreenColors.green100,
        error = RedColors.red100,
        warning = YellowColors.yellow90,
        info = BlueColors.blue80
    )
}