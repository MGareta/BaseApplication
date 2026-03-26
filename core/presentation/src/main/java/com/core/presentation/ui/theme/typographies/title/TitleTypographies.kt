package com.core.presentation.ui.theme.typographies.title

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.base.BaseTypography

@Immutable
class TitleTypographies(
    override val contrastLevel: Float,
    override val spDimensions: SpDimensions,
    override  val emDimensions: EmDimensions
) : BaseTypography() {

    private val fontWeight = when {
        contrastLevel >= 1.0f -> FontWeight.ExtraBold
        contrastLevel >= 0.5f -> FontWeight.Bold
        else -> FontWeight.SemiBold
    }

    val small = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.title.title20,
        lineHeight = emDimensions.normal,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.1).sp
        } else {
            0.sp
        }
    )

    val medium = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.title.title24,
        lineHeight = emDimensions.normal,
        letterSpacing = if (contrastLevel >= 1.0f) {
            0.sp
        } else {
            (-0.1).sp
        }
    )

    val large = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.title.title28,
        lineHeight = emDimensions.normal,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (-0.1).sp
        } else {
            (-0.2).sp
        }
    )
}
