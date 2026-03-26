package com.core.presentation.ui.components.button.hyperlink.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import com.core.presentation.ui.theme.LocalTypographies

sealed interface HyperlinkButtonSizeType {
    data object Small : HyperlinkButtonSizeType
    data object Medium : HyperlinkButtonSizeType
    data class Custom(
        val textStyle: TextStyle
    ) : HyperlinkButtonSizeType
}

internal data class HyperlinkButtonSize(
    val textStyle: TextStyle
)

@Composable
internal fun rememberHyperlinkButtonSize(
    hyperlinkButtonSizeType: HyperlinkButtonSizeType
): HyperlinkButtonSize {
    val typographies = LocalTypographies.current

    return remember(
        key1 = hyperlinkButtonSizeType,
        key2 = typographies
    ) {
        when (hyperlinkButtonSizeType) {
            HyperlinkButtonSizeType.Small -> HyperlinkButtonSize(
                textStyle = typographies.body.small
            )
            HyperlinkButtonSizeType.Medium -> HyperlinkButtonSize(
                textStyle = typographies.body.medium
            )
            is HyperlinkButtonSizeType.Custom -> HyperlinkButtonSize(
                textStyle = hyperlinkButtonSizeType.textStyle
            )
        }
    }
}
