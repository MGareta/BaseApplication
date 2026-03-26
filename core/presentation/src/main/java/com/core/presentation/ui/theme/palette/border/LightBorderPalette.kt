package com.core.presentation.ui.theme.palette.border

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.orange.OrangeColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.palette.border.base.BaseBorderPalette

@Immutable
sealed class LightBorderPalette(
    override val subtle: Color,
    override val standard: Color,
    override val onAccent: Color,
    override val active: Color,
    override val error: Color,
    override val disabled: Color
) : BaseBorderPalette {

    data object DefaultContrast : LightBorderPalette(
        subtle = GrayColors.gray20,
        standard = GrayColors.gray40,
        onAccent = OrangeColors.orange50,
        active = GreenColors.green50,
        error = RedColors.red50,
        disabled = GrayColors.gray20
    )

    data object MediumContrast : LightBorderPalette(
        subtle = GrayColors.gray40,
        standard = GrayColors.gray60,
        onAccent = OrangeColors.orange70,
        active = GreenColors.green70,
        error = RedColors.red50,
        disabled = GrayColors.gray20
    )


    data object HighContrast : LightBorderPalette(
        subtle = Color.Black,
        standard = Color.Black,
        onAccent = OrangeColors.orange90,
        active = GreenColors.green90,
        error = RedColors.red90,
        disabled = GrayColors.gray20
    )
}