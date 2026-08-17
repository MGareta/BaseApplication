package com.core.presentation.ui.components.badge.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalPalette

@Stable
sealed interface BadgeStyleType {
    data object Alert : BadgeStyleType
    data object Inbox : BadgeStyleType
    data object Info : BadgeStyleType
    data object Notification : BadgeStyleType
    data class Custom(
        val backgroundColor: Color,
        val contentColor: Color
    ) : BadgeStyleType
}

@Stable
internal data class BadgeStyle(
    val backgroundColor: Color,
    val contentColor: Color
)

@Composable
internal fun rememberBadgeStyle(
    badgeStyleType: BadgeStyleType
): BadgeStyle {
    val palette = LocalPalette.current

    return remember(
        key1 = badgeStyleType,
        key2 = palette
    ) {
        when (badgeStyleType) {
            BadgeStyleType.Alert -> BadgeStyle(
                backgroundColor = palette.backgroundPalette.warningLow,
                contentColor = palette.contentPalette.warning,
            )
            BadgeStyleType.Inbox -> BadgeStyle(
                backgroundColor = palette.backgroundPalette.accentLow,
                contentColor = palette.contentPalette.accent,
            )
            BadgeStyleType.Info -> BadgeStyle(
                backgroundColor = palette.backgroundPalette.infoLow,
                contentColor = palette.contentPalette.info,
            )
            BadgeStyleType.Notification -> BadgeStyle(
                backgroundColor = palette.backgroundPalette.accent,
                contentColor = palette.contentPalette.onAccent,
            )
            is BadgeStyleType.Custom -> BadgeStyle(
                backgroundColor = badgeStyleType.backgroundColor,
                contentColor = badgeStyleType.contentColor,
            )
        }
    }
}
