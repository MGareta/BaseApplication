package com.core.presentation.ui.components.button.block.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalTypographies

sealed interface BlockButtonSizeType {
    data object Small : BlockButtonSizeType
    data object Medium : BlockButtonSizeType
    data object Large : BlockButtonSizeType
    data class Custom(
        val size: DpSize,
        val paddingValues: PaddingValues,
        val space: Dp,
        val textStyle: TextStyle
    ) : BlockButtonSizeType
}

internal data class BlockButtonSize(
    val size: DpSize,
    val paddingValues: PaddingValues,
    val space: Dp,
    val textStyle: TextStyle
)

@Composable
internal fun rememberBlockButtonSize(
    blockButtonStyleType: BlockButtonStyleType,
    blockButtonSizeType: BlockButtonSizeType
): BlockButtonSize {
    val dimensions = LocalDimensions.current
    val typographies = LocalTypographies.current

    return remember(
        blockButtonStyleType,
        blockButtonSizeType,
        dimensions,
        typographies
    ) {
        when (blockButtonSizeType) {
            BlockButtonSizeType.Small -> BlockButtonSize(
                size = DpSize(
                    width = Dp.Unspecified,
                    height = dimensions.dp.target.target40
                ),
                paddingValues = PaddingValues(
                    horizontal = if (blockButtonStyleType == BlockButtonStyleType.Tertiary) {
                        dimensions.dp.space.space0
                    } else {
                        dimensions.dp.space.space12
                    },
                    vertical = dimensions.dp.space.space8
                ),
                space = dimensions.dp.space.space12,
                textStyle = typographies.body.small
            )
            BlockButtonSizeType.Medium -> BlockButtonSize(
                size = DpSize(
                    width = Dp.Unspecified,
                    height = dimensions.dp.target.target48
                ),
                paddingValues = PaddingValues(
                    horizontal = if (blockButtonStyleType == BlockButtonStyleType.Tertiary) {
                        dimensions.dp.space.space0
                    } else {
                        dimensions.dp.space.space16
                    },
                    vertical = dimensions.dp.space.space12
                ),
                space = dimensions.dp.space.space16,
                textStyle = typographies.body.medium
            )
            BlockButtonSizeType.Large -> BlockButtonSize(
                size = DpSize(
                    width = Dp.Unspecified,
                    height = dimensions.dp.target.target56
                ),
                paddingValues = PaddingValues(
                    horizontal = if (blockButtonStyleType == BlockButtonStyleType.Tertiary) {
                        dimensions.dp.space.space0
                    } else {
                        dimensions.dp.space.space16
                    },
                    vertical = dimensions.dp.space.space16
                ),
                space = dimensions.dp.space.space16,
                textStyle = typographies.body.large
            )
            is BlockButtonSizeType.Custom -> BlockButtonSize(
                size = blockButtonSizeType.size,
                paddingValues = blockButtonSizeType.paddingValues,
                space = blockButtonSizeType.space,
                textStyle = blockButtonSizeType.textStyle
            )
        }
    }
}
