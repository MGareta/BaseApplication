package com.core.presentation.ui.components.form.fields.select.toggle.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.ShadowScope
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

data class ToggleStyle(
    val switchThumbColor: Color,
    val switchTrackColor: Color,
    val switchBorderColor: Color,
    val background: Color,
    val borderStroke: BorderStroke?,
    val shadow: ShadowScope?,
)

@Composable
internal fun rememberToggleStyle(
    isSelected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource
): ToggleStyle {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        isSelected,
        enabled,
        isHovered,
        isFocused,
        isPressed,
        palette,
        dimensions
    ) {
        val backgroundColor = when {
            isFocused || isHovered -> palette.backgroundPalette.overlay
            else -> Color.Unspecified
        }

        val borderStroke = when {
            isFocused || isHovered -> BorderStroke(
                width = dimensions.dp.border.border1,
                color = palette.borderPalette.onAccent
            )
            else -> null
        }

        val (switchThumbColor, switchTrackColor, switchBorderColor) = when {
            enabled && isSelected -> arrayOf(
                palette.contentPalette.primary,
                palette.backgroundPalette.accent,
                palette.borderPalette.standard
            )
            enabled && !isSelected -> arrayOf(
                palette.contentPalette.accent,
                palette.backgroundPalette.surface,
                palette.borderPalette.standard
            )
            else -> arrayOf(
                palette.contentPalette.disabled,
                palette.backgroundPalette.disabled,
                palette.borderPalette.disabled
            )
        }

        ToggleStyle(
            switchThumbColor = switchThumbColor,
            switchTrackColor = switchTrackColor,
            switchBorderColor = switchBorderColor,
            background = backgroundColor,
            borderStroke = borderStroke,
            shadow = null
        )
    }
}
