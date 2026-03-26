package com.core.presentation.ui.theme.typographies.base

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.core.presentation.R
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions

@Immutable
abstract class BaseTypography {

    abstract val contrastLevel: Float
    abstract val spDimensions: SpDimensions
    abstract val emDimensions: EmDimensions

    private val fontFamily = FontFamily(
        Font(
            resId = R.font.inter,
            weight = FontWeight.Normal
        ),
        Font(
            resId = R.font.inter_medium,
            weight = FontWeight.Medium
        ),
        Font(
            resId = R.font.inter_semibold,
            weight = FontWeight.SemiBold
        ),
        Font(
            resId = R.font.inter_bold,
            weight = FontWeight.Bold),
        Font(
            resId = R.font.inter_extrabold,
            weight = FontWeight.ExtraBold),
        Font(
            resId = R.font.inter_black,
            weight = FontWeight.Black
        )
    )

    protected fun buildStyle(
        fontWeight: FontWeight,
        fontSize: TextUnit,
        lineHeight: TextUnit,
        letterSpacing: TextUnit = TextUnit.Unspecified
    ): TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = fontSize * lineHeight.value,
        letterSpacing = letterSpacing,
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    )
}
