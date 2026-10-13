package com.core.presentation.ui.utils

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

class BorderIndication(
    private val borderColor: Color,
    private val strokeWidth: Dp,
    private val shape: Shape
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return BorderIndicationNode(
            interactionSource = interactionSource,
            borderColor = borderColor,
            strokeWidth = strokeWidth,
            shape = shape
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }

        if (other !is BorderIndication) {
            return false
        }

        return borderColor == other.borderColor &&
                strokeWidth == other.strokeWidth &&
                shape == other.shape
    }

    override fun hashCode(): Int {
        var result = borderColor.hashCode()
        result = 31 * result + strokeWidth.hashCode()
        result = 31 * result + shape.hashCode()

        return result
    }
}

private class BorderIndicationNode(
    private val interactionSource: InteractionSource,
    private val borderColor: Color,
    private val strokeWidth: Dp,
    private val shape: Shape
) : Modifier.Node(), DrawModifierNode {

    private var isPressed = false
    private var isHovered = false
    private var isFocused = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> isPressed = true
                    is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
                    is HoverInteraction.Enter -> isHovered = true
                    is HoverInteraction.Exit -> isHovered = false
                    is FocusInteraction.Focus -> isFocused = true
                    is FocusInteraction.Unfocus -> isFocused = false
                }

                invalidateDraw()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        if (isPressed) {
            drawRect(
                color = Color.Black.copy(
                    alpha = 0.3F
                ),
                size = size
            )
        } else if (isHovered || isFocused) {
            val strokeWidthPx = strokeWidth.toPx()
            val outline = shape.createOutline(
                size = size,
                layoutDirection = layoutDirection,
                density = this
            )

            drawOutline(
                outline = outline,
                color = borderColor,
                style = Stroke(
                    width = strokeWidthPx
                )
            )

            drawRect(
                color = Color.Black.copy(
                    alpha = 0.1F
                ),
                size = size
            )
        }
    }
}
