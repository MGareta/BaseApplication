package com.core.presentation.ui.theme.typographies.body

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.base.BaseTypography

@Immutable
class BodyTypographies(
    override val contrastLevel: Float,
    override val spDimensions: SpDimensions,
    override val emDimensions: EmDimensions
) : BaseTypography() {
    private val fontWeight = if (contrastLevel >= 1.0f) {
        FontWeight.Medium
    } else {
        FontWeight.Normal
    }

    val small = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.body.body14,
        lineHeight = emDimensions.relaxed,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.2).sp
        } else {
            (0.1).sp
        }
    )

    val medium = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.body.body16,
        lineHeight = emDimensions.relaxed,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.3).sp
        } else {
            (0.2).sp
        }
    )

    val large = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.body.body18,
        lineHeight = emDimensions.relaxed,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.3).sp
        } else {
            (0.2).sp
        }
    )
}
