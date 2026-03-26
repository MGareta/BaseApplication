package com.core.presentation.ui.theme.typographies.label

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.core.presentation.ui.theme.dimensions.em.EmDimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.base.BaseTypography

@Immutable
class LabelTypographies(
    override val contrastLevel: Float,
    override val spDimensions: SpDimensions,
    override val emDimensions: EmDimensions
) : BaseTypography() {

    private val fontWeight = if (contrastLevel >= 1.0f) {
        FontWeight.Bold
    } else {
        FontWeight.Medium
    }

    val small = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.label.label12,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.5).sp
        } else {
            (0.4).sp
        }
    )

    val medium = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.label.label14,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.3).sp
        } else {
            (0.2).sp
        }
    )

    val large = buildStyle(
        fontWeight = fontWeight,
        fontSize = spDimensions.label.label16,
        lineHeight = emDimensions.tight,
        letterSpacing = if (contrastLevel >= 1.0f) {
            (0.2).sp
        } else {
            (0.1).sp
        }
    )
}
