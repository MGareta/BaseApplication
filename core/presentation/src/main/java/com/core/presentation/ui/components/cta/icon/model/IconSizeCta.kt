package com.core.presentation.ui.components.cta.icon.model

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.core.presentation.ui.theme.dimensions.Dimensions

sealed class IconSizeCta(open val modifier: Modifier, open val iconSize: Dp) {
    data object Small : IconSizeCta(
        modifier = Modifier
            .size(
                size = Dimensions().dp.dp40,
            ),
        iconSize = Dimensions().dp.dp20,
    )

    data object Medium : IconSizeCta(
        modifier = Modifier
            .size(
                size = Dimensions().dp.dp48,
            ),
        iconSize = Dimensions().dp.dp24,
    )

    data object Large : IconSizeCta(
        modifier = Modifier
            .size(
                size = Dimensions().dp.dp56,
            ),
        iconSize = Dimensions().dp.dp24,
    )

    data class Custom(val width: Dp, val height: Dp, override val iconSize: Dp) :
        IconSizeCta(
            modifier = Modifier
                .size(
                    width = width,
                    height = height,
                ),
            iconSize = iconSize,
        )
}
