package com.core.presentation.ui.theme.palette

import androidx.compose.runtime.Immutable
import com.core.presentation.ui.theme.palette.background.DarkBackgroundPalette
import com.core.presentation.ui.theme.palette.background.LightBackgroundPalette
import com.core.presentation.ui.theme.palette.background.base.BaseBackgroundPalette
import com.core.presentation.ui.theme.palette.border.DarkBorderPalette
import com.core.presentation.ui.theme.palette.border.LightBorderPalette
import com.core.presentation.ui.theme.palette.border.base.BaseBorderPalette
import com.core.presentation.ui.theme.palette.content.DarkContentPalette
import com.core.presentation.ui.theme.palette.content.LightContentPalette
import com.core.presentation.ui.theme.palette.content.base.BaseContentPalette

@Immutable
interface ColorsPalette {
    val backgroundPalette: BaseBackgroundPalette
    val borderPalette: BaseBorderPalette
    val contentPalette: BaseContentPalette
}

@Immutable
sealed class LightsColorsPalette(
    override val backgroundPalette: LightBackgroundPalette,
    override val borderPalette: BaseBorderPalette,
    override val contentPalette: BaseContentPalette
) : ColorsPalette {
    data object DefaultContrast : LightsColorsPalette(
        backgroundPalette = LightBackgroundPalette.DefaultContrast,
        borderPalette = LightBorderPalette.DefaultContrast,
        contentPalette = LightContentPalette.DefaultContrast
    )

    data object MediumContrast : LightsColorsPalette(
        backgroundPalette = LightBackgroundPalette.MediumContrast,
        borderPalette = LightBorderPalette.MediumContrast,
        contentPalette = LightContentPalette.MediumContrast
    )

    data object HighContrast : LightsColorsPalette(
        backgroundPalette = LightBackgroundPalette.HighContrast,
        borderPalette = LightBorderPalette.HighContrast,
        contentPalette = LightContentPalette.HighContrast
    )
}

@Immutable
sealed class DarkColorsPalette(
    override val backgroundPalette: DarkBackgroundPalette,
    override val borderPalette: BaseBorderPalette,
    override val contentPalette: BaseContentPalette
) : ColorsPalette {
    data object DefaultContrast : DarkColorsPalette(
        backgroundPalette = DarkBackgroundPalette.DefaultContrast,
        borderPalette = DarkBorderPalette.DefaultContrast,
        contentPalette = DarkContentPalette.DefaultContrast
    )

    data object MediumContrast : DarkColorsPalette(
        backgroundPalette = DarkBackgroundPalette.MediumContrast,
        borderPalette = DarkBorderPalette.MediumContrast,
        contentPalette = DarkContentPalette.MediumContrast
    )

    data object HighContrast : DarkColorsPalette(
        backgroundPalette = DarkBackgroundPalette.HighContrast,
        borderPalette = DarkBorderPalette.HighContrast,
        contentPalette = DarkContentPalette.HighContrast
    )
}
