package com.core.presentation.ui.components.button.block

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.button.block.model.rememberBlockButtonSize
import com.core.presentation.ui.components.button.block.model.rememberBlockButtonStyle
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityLabel

@Composable
fun BlockButtonComponent(
    modifier: Modifier,
    blockButtonStyleType: BlockButtonStyleType,
    blockButtonSizeType: BlockButtonSizeType,
    text: String,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    accessibilityLabel: AccessibilityLabel,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val blockButtonStyle = rememberBlockButtonStyle(
        blockButtonStyleType = blockButtonStyleType,
        enabled = enabled,
        interactionSource = interactionSource
    )

    val blockButtonSize = rememberBlockButtonSize(
        blockButtonStyleType = blockButtonStyleType,
        blockButtonSizeType = blockButtonSizeType
    )

    OutlinedButton(
        modifier = modifier
            .then(
                other = Modifier
                    .size(
                        size = blockButtonSize.size
                    )
            )
            .width(
                intrinsicSize = IntrinsicSize.Min
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
        enabled = true,
        interactionSource = interactionSource,
        contentPadding = blockButtonSize.paddingValues,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = blockButtonStyle.backgroundColor,
            contentColor = blockButtonStyle.contentColor
        ),
        elevation = null,
        border = blockButtonStyle.borderStroke,
        onClick = {
            if (enabled) {
                onClick()
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .size(
                        size = blockButtonSize.textStyle.fontSize.value.dp
                    )
            ) {
                leadingIcon?.let {
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight(),
                        painter = painterResource(
                            id = it
                        ),
                        contentDescription = null
                    )
                }
            }

            Text(
                modifier = Modifier
                    .padding(
                        horizontal = blockButtonSize.space
                    )
                    .weight(
                        weight = 1F
                    )
                    .semantics {
                        hideFromAccessibility()
                    },
                text = text,
                textAlign = TextAlign.Center,
                style = blockButtonSize.textStyle
            )

            Box(
                modifier = Modifier
                    .size(
                        size = blockButtonSize.textStyle.fontSize.value.dp
                    )
            ) {
                trailingIcon?.let {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        painter = painterResource(
                            id = it
                        ),
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun BlockButtonComponentPrimaryLightPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Primary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
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
private fun BlockButtonComponentSecondaryLightPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Secondary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
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
private fun BlockButtonComponentTertiaryLightPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Tertiary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
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
private fun BlockButtonComponentPrimaryDarkPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Primary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
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
private fun BlockButtonComponentSecondaryDarkPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Secondary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
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
private fun BlockButtonComponentTertiaryDarkPreview() {
    Theme {
        BlockButtonComponent(
            modifier = Modifier
                .safeContentPadding(),
            blockButtonStyleType = BlockButtonStyleType.Tertiary,
            blockButtonSizeType = BlockButtonSizeType.Small,
            text = "Test",
            leadingIcon = android.R.drawable.star_on,
            trailingIcon = android.R.drawable.star_on,
            enabled = true,
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a test",
                disabled = "The test is not completed"
            ),
            onClick = {}
        )
    }
}
