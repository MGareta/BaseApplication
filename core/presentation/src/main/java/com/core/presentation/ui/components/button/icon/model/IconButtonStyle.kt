package com.core.presentation.ui.components.button.icon.model

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

sealed interface IconButtonStyleType {
    data object Primary : IconButtonStyleType
    data object Secondary : IconButtonStyleType
    data object Tertiary : IconButtonStyleType
}

internal data class IconButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderStroke: BorderStroke?,
    val shadow: ShadowScope?,
)

@Composable
internal fun rememberIconButtonStyle(
    iconButtonStyleType: IconButtonStyleType,
    enabled: Boolean,
    interactionSource: InteractionSource
): IconButtonStyle {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        iconButtonStyleType,
        enabled,
        isHovered,
        isFocused,
        isPressed,
        palette,
        dimensions
    ) {
        when (iconButtonStyleType) {
            IconButtonStyleType.Primary -> when {
                !enabled -> IconButtonStyle(
                    backgroundColor = palette.backgroundPalette.disabled,
                    contentColor = palette.contentPalette.disabled,
                    borderStroke = null,
                    shadow = null
                )
                isHovered -> IconButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isFocused -> IconButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isPressed -> IconButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = null,
                    shadow = null
                )
                else -> IconButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = null,
                    shadow = null
                )
            }
            IconButtonStyleType.Secondary -> when {
                !enabled -> IconButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.disabled,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.disabled
                    ),
                    shadow = null
                )
                isHovered -> IconButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isFocused -> IconButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isPressed -> IconButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.active
                    ),
                    shadow = null
                )
                else -> IconButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.active
                    ),
                    shadow = null
                )
            }
            IconButtonStyleType.Tertiary -> {
                when {
                    !enabled -> IconButtonStyle(
                        backgroundColor = palette.backgroundPalette.disabled,
                        contentColor = palette.contentPalette.disabled,
                        borderStroke = null,
                        shadow = null
                    )
                    isHovered -> IconButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = BorderStroke(
                            width = dimensions.dp.border.border1,
                            color = palette.borderPalette.onAccent
                        ),
                        shadow = null
                    )
                    isFocused -> IconButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = BorderStroke(
                            width = dimensions.dp.border.border1,
                            color = palette.borderPalette.onAccent
                        ),
                        shadow = null
                    )
                    isPressed -> IconButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = null,
                        shadow = null
                    )
                    else -> IconButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = null,
                        shadow = null
                    )
                }
            }
        }
    }
}
