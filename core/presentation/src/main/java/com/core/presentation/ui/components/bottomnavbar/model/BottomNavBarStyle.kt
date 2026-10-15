package com.core.presentation.ui.components.bottomnavbar.model

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.utils.BorderIndication

@Stable
internal data class BottomNavBarStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val indication: BorderIndication
)

@Composable
internal fun rememberBottomNavBarStyle(
    isSelected: Boolean
): BottomNavBarStyle {
    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        key1 = isSelected,
        key2 = palette,
        key3 = dimensions
    ) {
        val backgroundColor = when {
            isSelected -> palette.backgroundPalette.accent
            else -> palette.backgroundPalette.base
        }

        BottomNavBarStyle(
            backgroundColor = backgroundColor,
            contentColor = palette.contentPalette.onAccent,
            indication = BorderIndication(
                borderColor = palette.borderPalette.onAccent,
                strokeWidth = dimensions.dp.border.border2,
                shape = CircleShape
            )
        )
    }
}
