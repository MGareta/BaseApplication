package com.core.presentation.ui.components.form.fields.select.chip.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.ShadowScope
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette


@Stable
sealed interface ChipStyleType {
    data class Text(
        val text: String
    ) : ChipStyleType

    data class Custom(
        val content: @Composable () -> Unit
    ) : ChipStyleType
}


@Stable
internal data class ChipStyle(
    val background: Color,
    val content: Color,
    val borderStroke: BorderStroke?,
    val shadow: ShadowScope?
)

@Composable
internal fun rememberChipStyle(
    isSelected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource
): ChipStyle {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        isSelected,
        enabled,
        isHovered,
        isFocused,
        palette,
        dimensions
    ) {
        val background = if (isSelected) {
            when {
                enabled -> palette.backgroundPalette.accent
                else -> palette.backgroundPalette.accentLow
            }
        } else {
            when {
                !enabled -> palette.backgroundPalette.disabled
                isFocused || isHovered -> palette.backgroundPalette.overlay
                else -> palette.backgroundPalette.base
            }
        }

        val content = if (isSelected) {
            when {
                enabled -> palette.contentPalette.primary
                else -> palette.contentPalette.disabled
            }
        } else {
            when {
                !enabled -> palette.contentPalette.disabled
                isFocused || isHovered -> palette.contentPalette.onAccent
                else -> palette.contentPalette.primary
            }
        }

        val borderStroke = when {
            isFocused || isHovered -> BorderStroke(
                width = dimensions.dp.border.border1,
                color = palette.borderPalette.onAccent
            )
            !enabled -> BorderStroke(
                width = dimensions.dp.border.border1,
                color = palette.borderPalette.disabled
            )
            isSelected -> BorderStroke(
                width = dimensions.dp.border.border1,
                color = palette.borderPalette.standard
            )
            else -> null
        }

        ChipStyle(
            background = background,
            content = content,
            borderStroke = borderStroke,
            shadow = null
        )
    }
}
