package com.core.presentation.ui.components.form.fields.select.checkbox.model

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
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonSizeType
import com.core.presentation.ui.components.button.hyperlink.model.HyperlinkButtonType
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Stable
sealed interface CheckboxStyleType {
    data class Text(
        val text: String,
        val description: String
    ) : CheckboxStyleType

    data class Hyperlink(
        val text: String,
        val hyperlinks: Map<String, HyperlinkButtonType>,
        val hyperlinkButtonSizeType: HyperlinkButtonSizeType
    ) : CheckboxStyleType

    data class Custom(
        val content: @Composable () -> Unit
    ) : CheckboxStyleType
}

@Stable
internal data class CheckboxStyle(
    val checkboxMarkColor: Color,
    val checkboxBackgroundColor: Color,
    val checkboxBorderColor: Color,
    val background: Color,
    val borderStroke: BorderStroke?,
    val shadow: ShadowScope?,
)

@Composable
internal fun rememberCheckboxStyle(
    isSelected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource
): CheckboxStyle {
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

        val (checkboxMarkColor, checkboxBackgroundColor, checkboxBorderColor) = when {
            enabled && isSelected -> arrayOf(
                palette.contentPalette.primary,
                palette.backgroundPalette.accent,
                Color.Unspecified
            )
            enabled && !isSelected -> arrayOf(
                Color.Unspecified,
                Color.Unspecified,
                palette.borderPalette.active
            )
            else -> arrayOf(
                palette.contentPalette.disabled,
                palette.backgroundPalette.disabled,
                palette.borderPalette.disabled
            )
        }

        CheckboxStyle(
            checkboxMarkColor = checkboxMarkColor,
            checkboxBackgroundColor = checkboxBackgroundColor,
            checkboxBorderColor = checkboxBorderColor,
            background = backgroundColor,
            borderStroke = borderStroke,
            shadow = null
        )
    }
}
