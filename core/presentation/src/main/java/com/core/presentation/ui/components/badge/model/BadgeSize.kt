package com.core.presentation.ui.components.badge.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import com.core.presentation.ui.theme.LocalTypographies

@Stable
sealed interface BadgeSizeType {
    data object Small : BadgeSizeType
    data object Medium : BadgeSizeType
    data object Large : BadgeSizeType
    data class Custom(
        val textStyle: TextStyle
    ) : BadgeSizeType
}

@Stable
internal data class BadgeSize(
    val textStyle: TextStyle
)

@Composable
internal fun rememberBadgeSize(
    badgeSizeType: BadgeSizeType
): BadgeSize {
    val typographies = LocalTypographies.current

    return remember(
        key1 = badgeSizeType,
        key2 = typographies
    ) {
        when (badgeSizeType) {
            BadgeSizeType.Small -> BadgeSize(
                textStyle = typographies.label.small
            )
            BadgeSizeType.Medium -> BadgeSize(
                textStyle = typographies.label.medium
            )
            BadgeSizeType.Large -> BadgeSize(
                textStyle = typographies.label.large
            )
            is BadgeSizeType.Custom -> BadgeSize(
                textStyle = badgeSizeType.textStyle
            )
        }
    }
}
