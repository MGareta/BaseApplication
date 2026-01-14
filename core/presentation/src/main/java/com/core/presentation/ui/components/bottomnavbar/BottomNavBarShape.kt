package com.core.presentation.ui.components.bottomnavbar

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

internal class BottomNavBarShape(private val fabSize: Dp, private val fabGap: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(getPath(size, density))

    private fun getPath(size: Size, density: Density): Path {
        val cutoutCenterX = size.width / 2
        val cutoutRadius = density.run {
            ((fabSize / 2) + fabGap).toPx()
        }

        return Path().apply {
            val cutoutEdgeOffset = cutoutRadius * 1.8f
            val cutoutLeftX = cutoutCenterX - cutoutEdgeOffset
            val cutoutRightX = cutoutCenterX + cutoutEdgeOffset

            // Set starting point path (0,canvas.height)
            moveTo(x = 0F, y = size.height)

            // Draw line from starting point (0,Y) to (0,0)
            lineTo(0f, 0f)

            // Draw line from (0,0) to (cutoutLeft,0)
            lineTo(cutoutLeftX, 0f)

            // Draw cutout from left to center
            cubicTo(
                x1 = cutoutCenterX - cutoutRadius,
                y1 = 0f,
                x2 = cutoutCenterX - cutoutRadius,
                y2 = cutoutRadius,
                x3 = cutoutCenterX,
                y3 = cutoutRadius,
            )

            // Draw cutout from center to right
            cubicTo(
                x1 = cutoutCenterX + cutoutRadius,
                y1 = cutoutRadius,
                x2 = cutoutCenterX + cutoutRadius,
                y2 = 0f,
                x3 = cutoutRightX,
                y3 = 0f,
            )

            // Draw line from (cutoutRight,0) to (canvas.width,0)
            lineTo(size.width, 0f)

            // Draw line from (canvas.width,0) to end point (canvas.width,canvas.height)
            lineTo(x = size.width, y = size.height)

            // Draw line the path from end point to starting point
            close()
        }
    }
}
