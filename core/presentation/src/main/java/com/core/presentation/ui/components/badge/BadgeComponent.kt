package com.core.presentation.ui.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.badge.model.BadgeSizeType
import com.core.presentation.ui.components.badge.model.BadgeStyleType
import com.core.presentation.ui.components.badge.model.rememberBadgeSize
import com.core.presentation.ui.components.badge.model.rememberBadgeStyle
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun BadgeComponent(
    modifier: Modifier,
    badgeStyleType: BadgeStyleType,
    badgeSizeType: BadgeSizeType,
    text: String?,
    accessibilityState: AccessibilityState.Description
) {
    val badgeSize = rememberBadgeSize(
        badgeSizeType = badgeSizeType
    )

    val badgeStyle = rememberBadgeStyle(
        badgeStyleType = badgeStyleType
    )

    Text(
        modifier = modifier
            .background(
                color = badgeStyle.backgroundColor,
                shape = CircleShape
            )
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    constraints = constraints
                )

                val currentHeight = placeable.height
                val currentWidth = placeable.width

                val newWidth = maxOf(
                    a = currentHeight,
                    b = currentWidth
                )

                layout(
                    width = newWidth,
                    height = currentHeight
                ) {
                    placeable.placeRelative(
                        x = (newWidth - currentWidth) / 2,
                        y = 0
                    )
                }
            }
            .semantics {
                this.text = AnnotatedString(
                    text = accessibilityState.description
                )
            }
            .padding(
                horizontal = LocalDimensions.current.dp.space.space4
            ),
        text = text ?: "",
        color = badgeStyle.contentColor,
        style = badgeSize.textStyle
    )
}

@Preview
@Composable
private fun BadgeComponentPreview() {
    Theme {
        BadgeComponent(
            modifier = Modifier
                .safeContentPadding(),
            badgeStyleType = BadgeStyleType.Notification,
            badgeSizeType = BadgeSizeType.Small,
            text = "1",
            accessibilityState = AccessibilityState.Description(
                description = "100 left"
            )
        )
    }
}
