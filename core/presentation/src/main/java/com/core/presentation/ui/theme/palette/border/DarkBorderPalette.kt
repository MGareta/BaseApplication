package com.core.presentation.ui.theme.palette.border

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.colors.gray.GrayColors
import com.core.presentation.ui.theme.colors.green.GreenColors
import com.core.presentation.ui.theme.colors.orange.OrangeColors
import com.core.presentation.ui.theme.colors.red.RedColors
import com.core.presentation.ui.theme.palette.border.base.BaseBorderPalette

@Immutable
sealed class DarkBorderPalette(
    override val subtle: Color,
    override val standard: Color,
    override val onAccent: Color,
    override val active: Color,
    override val error: Color,
    override val disabled: Color
) : BaseBorderPalette {

    data object DefaultContrast : DarkBorderPalette(
        subtle = GrayColors.gray90,
        standard = GrayColors.gray70,
        onAccent = OrangeColors.orange50,
        active = GreenColors.green50,
        error = RedColors.red40,
        disabled = GrayColors.gray80
    )

    data object MediumContrast : DarkBorderPalette(
        subtle = GrayColors.gray70,
        standard = GrayColors.gray40,
        onAccent = OrangeColors.orange30,
        active = GreenColors.green30,
        error = RedColors.red40,
        disabled = GrayColors.gray80
    )


    data object HighContrast : DarkBorderPalette(
        subtle = Color.White,
        standard = Color.White,
        onAccent = OrangeColors.orange20,
        active = GreenColors.green20,
        error = RedColors.red20,
        disabled = GrayColors.gray80
    )
}