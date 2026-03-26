package com.core.presentation.ui.components.button.icon.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalTypographies

sealed interface IconButtonSizeType {
    data object Small : IconButtonSizeType
    data object Medium : IconButtonSizeType
    data object Large : IconButtonSizeType
    data class Custom(
        val size: DpSize,
        val paddingValues: PaddingValues
    ) : IconButtonSizeType
}

internal data class IconButtonSize(
    val size: DpSize,
    val paddingValues: PaddingValues
)

@Composable
internal fun rememberIconButtonSize(
    iconButtonSizeType: IconButtonSizeType
): IconButtonSize {
    val dimensions = LocalDimensions.current
    val typographies = LocalTypographies.current

    return remember(
        key1 = iconButtonSizeType,
        key2 = dimensions,
        key3 = typographies
    ) {
        when (iconButtonSizeType) {
            IconButtonSizeType.Small -> IconButtonSize(
                size = DpSize(
                    width = dimensions.dp.target.target40,
                    height = dimensions.dp.target.target40
                ),
                paddingValues = PaddingValues(
                    all = dimensions.dp.space.space12
                )
            )
            IconButtonSizeType.Medium -> IconButtonSize(
                size = DpSize(
                    width = dimensions.dp.target.target48,
                    height = dimensions.dp.target.target48
                ),
                paddingValues = PaddingValues(
                    all = dimensions.dp.space.space12
                )
            )
            IconButtonSizeType.Large -> IconButtonSize(
                size = DpSize(
                    width = dimensions.dp.target.target56,
                    height = dimensions.dp.target.target56
                ),
                paddingValues = PaddingValues(
                    all = dimensions.dp.space.space16
                )
            )
            is IconButtonSizeType.Custom -> IconButtonSize(
                size = iconButtonSizeType.size,
                paddingValues = iconButtonSizeType.paddingValues
            )
        }
    }
}
