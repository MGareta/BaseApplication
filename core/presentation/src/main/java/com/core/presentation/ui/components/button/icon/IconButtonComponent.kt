package com.core.presentation.ui.components.button.icon

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.components.button.icon.model.rememberIconButtonSize
import com.core.presentation.ui.components.button.icon.model.rememberIconButtonStyle
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityLabel

@Composable
fun IconButtonComponent(
    modifier: Modifier,
    iconButtonStyleType: IconButtonStyleType,
    iconButtonSizeType: IconButtonSizeType,
    @DrawableRes icon: Int,
    enabled: Boolean,
    accessibilityLabel: AccessibilityLabel,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconButtonStyle = rememberIconButtonStyle(
        iconButtonStyleType = iconButtonStyleType,
        enabled = enabled,
        interactionSource = interactionSource
    )

    val iconButtonSize = rememberIconButtonSize(
        iconButtonSizeType = iconButtonSizeType
    )

    OutlinedIconButton(
        modifier = modifier
            .size(
                size = iconButtonSize.size,
            )
            .semantics {
                when (enabled) {
                    true -> {
                        this.text = AnnotatedString(
                            text = accessibilityLabel.enabled
                        )
                    }
                    false -> {
                        this.text = AnnotatedString(
                            text = accessibilityLabel.disabled
                        )

                        disabled()
                    }
                }
            },
        enabled = enabled,
        interactionSource = interactionSource,
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = iconButtonStyle.backgroundColor,
            contentColor = iconButtonStyle.contentColor
        ),
        border = iconButtonStyle.borderStroke,
        onClick = {
            if (enabled) {
                onClick()
            }
        }
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    paddingValues = iconButtonSize.paddingValues
                ),
            painter = painterResource(
                id = icon,
            ),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun IconButtonPrimaryLightPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Primary,
            iconButtonSizeType = IconButtonSizeType.Large,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun IconButtonSecondaryLightPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Secondary,
            iconButtonSizeType = IconButtonSizeType.Small,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun IconButtonTertiaryLightPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Tertiary,
            iconButtonSizeType = IconButtonSizeType.Medium,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun IconButtonPrimaryDarkPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Primary,
            iconButtonSizeType = IconButtonSizeType.Large,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun IconButtonSecondaryDarkPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Secondary,
            iconButtonSizeType = IconButtonSizeType.Small,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun IconButtonTertiaryDarkPreview() {
    Theme {
        IconButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            iconButtonStyleType = IconButtonStyleType.Tertiary,
            iconButtonSizeType = IconButtonSizeType.Medium,
            icon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}
