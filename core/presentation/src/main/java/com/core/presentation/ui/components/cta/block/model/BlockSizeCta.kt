package com.core.presentation.ui.components.cta.block.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.core.presentation.ui.theme.dimensions.Dimensions
import com.core.presentation.ui.theme.dimensions.sp.SpDimensions
import com.core.presentation.ui.theme.typographies.Typographies

sealed class BlockSizeCta(
    open val modifier: Modifier,
    open val contentPadding: PaddingValues,
    open val textStyle: TextStyle,
) {
    data object Small : BlockSizeCta(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                height = Dimensions().dp.dp40,
            ),
        contentPadding = PaddingValues(
            horizontal = Dimensions().dp.dp12,
            vertical = Dimensions().dp.dp8,
        ),
        textStyle = Typographies(dimensions = SpDimensions()).mid.mEmphasis,
    )

    data object Medium : BlockSizeCta(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                height = Dimensions().dp.dp48,
            ),
        contentPadding = PaddingValues(
            horizontal = Dimensions().dp.dp16,
            vertical = Dimensions().dp.dp12,
        ),
        textStyle = Typographies(dimensions = SpDimensions()).mid.mEmphasis,
    )

    data object Large : BlockSizeCta(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                height = Dimensions().dp.dp56,
            ),
        contentPadding = PaddingValues(
            all = Dimensions().dp.dp16,
        ),
        textStyle = Typographies(dimensions = SpDimensions()).mid.sEmphasis,
    )

    data class Custom(
        val width: Dp,
        val height: Dp,
        val paddingValues: PaddingValues,
        override val textStyle: TextStyle,
    ) : BlockSizeCta(
        modifier = Modifier
            .size(
                width = width,
                height = height,
            ),
        contentPadding = paddingValues,
        textStyle = textStyle,
    )
}
