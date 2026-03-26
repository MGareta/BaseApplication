package com.core.presentation.ui.components.button.block.model

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

sealed interface BlockButtonStyleType {
    data object Primary : BlockButtonStyleType
    data object Secondary : BlockButtonStyleType
    data object Tertiary : BlockButtonStyleType
}

internal data class BlockButtonStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderStroke: BorderStroke?,
    val shadow: ShadowScope?
)

@Composable
internal fun rememberBlockButtonStyle(
    blockButtonStyleType: BlockButtonStyleType,
    enabled: Boolean,
    interactionSource: InteractionSource
): BlockButtonStyle {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        blockButtonStyleType,
        enabled,
        isHovered,
        isFocused,
        isPressed,
        palette,
        dimensions
    ) {
        when (blockButtonStyleType) {
            BlockButtonStyleType.Primary -> when {
                !enabled -> BlockButtonStyle(
                    backgroundColor = palette.backgroundPalette.disabled,
                    contentColor = palette.contentPalette.disabled,
                    borderStroke = null,
                    shadow = null
                )
                isHovered -> BlockButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isFocused -> BlockButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isPressed -> BlockButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = null,
                    shadow = null
                )
                else -> BlockButtonStyle(
                    backgroundColor = palette.backgroundPalette.accent,
                    contentColor = palette.contentPalette.onAccent,
                    borderStroke = null,
                    shadow = null
                )
            }
            BlockButtonStyleType.Secondary -> when {
                !enabled -> BlockButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.disabled,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.disabled
                    ),
                    shadow = null
                )
                isHovered -> BlockButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isFocused -> BlockButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.onAccent
                    ),
                    shadow = null
                )
                isPressed -> BlockButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.active
                    ),
                    shadow = null
                )
                else -> BlockButtonStyle(
                    backgroundColor = Color.Transparent,
                    contentColor = palette.contentPalette.accent,
                    borderStroke = BorderStroke(
                        width = dimensions.dp.border.border1,
                        color = palette.borderPalette.active
                    ),
                    shadow = null
                )
            }
            BlockButtonStyleType.Tertiary -> {
                when {
                    !enabled -> BlockButtonStyle(
                        backgroundColor = palette.backgroundPalette.disabled,
                        contentColor = palette.contentPalette.disabled,
                        borderStroke = null,
                        shadow = null
                    )
                    isHovered -> BlockButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = BorderStroke(
                            width = dimensions.dp.border.border1,
                            color = palette.borderPalette.onAccent
                        ),
                        shadow = null
                    )
                    isFocused -> BlockButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = BorderStroke(
                            width = dimensions.dp.border.border1,
                            color = palette.borderPalette.onAccent
                        ),
                        shadow = null
                    )
                    isPressed -> BlockButtonStyle(
                        backgroundColor = Color.Transparent,
                        contentColor = palette.contentPalette.accent,
                        borderStroke = null,
                        shadow = null
                    )
                    else -> BlockButtonStyle(
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
