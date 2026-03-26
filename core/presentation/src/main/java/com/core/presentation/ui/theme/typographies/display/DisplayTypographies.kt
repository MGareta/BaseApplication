package com.core.presentation.ui.theme.typographies.display

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.base.BaseTypography

@Immutable
class DisplayTypographies(
    override val contrastLevel: Float,
    override val spDimensions: SpDimensions,
    override val emDimensions: EmDimensions
) : BaseTypography() {

    private val fontWeight = if (contrastLevel >= 1.0f) {
        FontWeight.Black
    } else {
        FontWeight.Bold
    }

    val small = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.display.display32,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (-0.1).sp
        } else {
            (-0.2).sp
        }
    )

    val medium = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.display.display40,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (-0.3).sp
        } else {
            (-0.5).sp
        }
    )

    val large = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.display.display48,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (-0.8).sp
        } else {
            (-1).sp
        }
    )
}
