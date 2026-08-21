package com.core.presentation.ui.components.alert

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.core.presentation.R
import com.core.presentation.ui.components.alert.model.AlertFamilyType
import com.core.presentation.ui.components.alert.model.AlertStyle
import com.core.presentation.ui.components.alert.model.AlertStyleType
import com.core.presentation.ui.components.alert.model.rememberAlertStyle
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.button.icon.IconButtonComponent
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun AlertComponent(
    modifier: Modifier,
    alertStyleType: AlertStyleType,
    alertFamilyType: AlertFamilyType,
    title: String,
    text: String?,
    content: (@Composable () -> Unit)?
) {
    val alertStyle = rememberAlertStyle(
        alertStyleType = alertStyleType
    )

    when (alertFamilyType) {
        is AlertFamilyType.TopTrigger -> AlertTopTriggerComponent(
            modifier = modifier,
            alertStyle = alertStyle,
            title = title,
            text = text,
            content = content,
            onDismiss = alertFamilyType.onDismiss
        )
        is AlertFamilyType.Message -> AlertMessageComponent(
            modifier = modifier,
            alertStyle = alertStyle,
            title = title,
            text = text,
            content = content,
            leadingComponent = alertFamilyType.trailingComponent
        )
        is AlertFamilyType.Modal -> AlertModalComponent(
            modifier = modifier,
            alertStyle = alertStyle,
            title = title,
            text = text,
            content = content,
            onDismiss = alertFamilyType.onDismiss
        )
    }
}

@Composable
private fun AlertTopTriggerComponent(
    modifier: Modifier,
    alertStyle: AlertStyle,
    title: String,
    text: String?,
    content: (@Composable () -> Unit)?,
    onDismiss: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        content = {
            (LocalView.current.parent as? DialogWindowProvider)?.apply {
                window.setGravity(Gravity.TOP)
            }

            Row(
                modifier = modifier
                    .padding(
                        all = LocalDimensions.current.dp.space.space8
                    )
                    .fillMaxWidth()
                    .background(
                        color = alertStyle.backgroundColor,
                        shape = RoundedCornerShape(
                            size = LocalDimensions.current.dp.radius.radius8
                        )
                    )
                    .semantics {
                        isTraversalGroup = true
                    },
                horizontalArrangement = Arrangement.spacedBy(
                    space = LocalDimensions.current.dp.space.space8
                )
            ) {
                Icon(
                    modifier = Modifier
                        .padding(
                            start = LocalDimensions.current.dp.space.space12,
                            top = LocalDimensions.current.dp.space.space12
                        )
                        .size(
                            size = LocalDimensions.current.dp.target.target24
                        ),
                    painter = painterResource(
                        id = R.drawable.ic_password_show
                    ),
                    tint = alertStyle.contentColor,
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .padding(
                            top = LocalDimensions.current.dp.space.space12,
                            bottom = LocalDimensions.current.dp.space.space12
                        )
                        .weight(
                            weight = 1F
                        )
                        .semantics {
                            isTraversalGroup = true
                        },
                    verticalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.dp.space.space2
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .semantics(
                                mergeDescendants = true
                            ) {}
                    ) {
                        Text(
                            text = title,
                            color = alertStyle.contentColor,
                            style = LocalTypographies.current.title.small.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )

                        text?.let {
                            Text(
                                text = it,
                                color = LocalPalette.current.contentPalette.primary,
                                style = LocalTypographies.current.body.small.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                )
                            )
                        }
                    }

                    content?.invoke()
                }

                IconButtonComponent(
                    modifier = Modifier,
                    iconButtonStyleType = IconButtonStyleType.Tertiary,
                    iconButtonSizeType = IconButtonSizeType.Small,
                    icon = R.drawable.ic_password_hide,
                    enabled = true,
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = ""
                    ),
                    contentDescription = "Close",
                    onClick = onDismiss
                )
            }
        }
    )
}

@Composable
private fun AlertMessageComponent(
    modifier: Modifier,
    alertStyle: AlertStyle,
    title: String,
    text: String?,
    content: (@Composable () -> Unit)?,
    leadingComponent: (@Composable () -> Unit)?,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = alertStyle.backgroundColor,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius8
                )
            )
            .border(
                width = LocalDimensions.current.dp.border.border1,
                color = alertStyle.borderColor,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius8
                )
            )
            .semantics {
                isTraversalGroup = true
            },
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.space.space8
        )
    ) {
        Icon(
            modifier = Modifier
                .padding(
                    start = LocalDimensions.current.dp.space.space16,
                    top = LocalDimensions.current.dp.space.space16
                )
                .size(
                    size = LocalDimensions.current.dp.target.target24
                ),
            painter = painterResource(
                id = R.drawable.ic_password_show
            ),
            tint = alertStyle.contentColor,
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .padding(
                    top = LocalDimensions.current.dp.space.space14,
                    bottom = LocalDimensions.current.dp.space.space16
                )
                .weight(
                    weight = 1F
                )
                .semantics {
                    isTraversalGroup = true
                },
            verticalArrangement = Arrangement.spacedBy(
                space = LocalDimensions.current.dp.space.space2
            )
        ) {
            Column(
                modifier = Modifier
                    .semantics(
                        mergeDescendants = true
                    ) {}
            ) {
                Text(
                    text = title,
                    color = alertStyle.contentColor,
                    style = LocalTypographies.current.title.small.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )

                text?.let {
                    Text(
                        text = it,
                        color = LocalPalette.current.contentPalette.primary,
                        style = LocalTypographies.current.body.medium.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                }
            }

            content?.invoke()
        }

        leadingComponent?.invoke()
    }
}

@Composable
private fun AlertModalComponent(
    modifier: Modifier,
    alertStyle: AlertStyle,
    title: String,
    text: String?,
    content: (@Composable () -> Unit)?,
    onDismiss: () -> Unit
) {
    @OptIn(ExperimentalMaterial3Api::class)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        content = {
            (LocalView.current.parent as? DialogWindowProvider)?.apply {
                window.setGravity(Gravity.BOTTOM)
            }

            Column(
                modifier = modifier
                    .padding(
                        all = LocalDimensions.current.dp.space.space8
                    )
                    .fillMaxWidth()
                    .background(
                        color = alertStyle.backgroundColor,
                        shape = RoundedCornerShape(
                            size = LocalDimensions.current.dp.radius.radius8
                        )
                    )
                    .semantics {
                        isTraversalGroup = true
                    },
                verticalArrangement = Arrangement.spacedBy(
                    space = LocalDimensions.current.dp.space.space8
                )
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.dp.space.space8
                    ),
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space16,
                                start = LocalDimensions.current.dp.space.space16
                            )
                            .size(
                                size = LocalDimensions.current.dp.target.target24
                            ),
                        painter = painterResource(
                            id = R.drawable.ic_password_show
                        ),
                        tint = alertStyle.contentColor,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space14
                            )
                            .weight(
                                weight = 1F
                            ),
                        text = title,
                        color = alertStyle.contentColor,
                        style = LocalTypographies.current.title.medium.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )

                    IconButtonComponent(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space4,
                                end = LocalDimensions.current.dp.space.space4
                            )
                            .semantics {
                                traversalIndex = 1F
                            },
                        iconButtonStyleType = IconButtonStyleType.Tertiary,
                        iconButtonSizeType = IconButtonSizeType.Medium,
                        icon = R.drawable.ic_password_hide,
                        enabled = true,
                        accessibilityState = AccessibilityState.Click(
                            disabledReason = ""
                        ),
                        contentDescription = "Close",
                        onClick = onDismiss
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(
                            start = LocalDimensions.current.dp.space.space16,
                            end = LocalDimensions.current.dp.space.space16,
                            bottom = LocalDimensions.current.dp.space.space16
                        )
                        .semantics {
                            isTraversalGroup = true
                        }
                ) {
                    text?.let {
                        Text(
                            text = it,
                            color = LocalPalette.current.contentPalette.primary,
                            style = LocalTypographies.current.body.medium.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }

                    content?.invoke()
                }
            }
        }
    )
}

@Preview
@Composable
private fun AlertComponentPreview() {
    Theme {
        Column(
            modifier = Modifier
                .statusBarsPadding()
        ) {
            AlertComponent(
                modifier = Modifier,
                alertStyleType = AlertStyleType.Info,
                alertFamilyType = AlertFamilyType.TopTrigger(
                    onDismiss = {}
                ),
                title = "Top trigger",
                text = "This is a top trigger",
                content = {
                    BlockButtonComponent(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space8
                            ),
                        blockButtonStyleType = BlockButtonStyleType.Primary,
                        blockButtonSizeType = BlockButtonSizeType.Small,
                        text = "Action",
                        leadingIcon = null,
                        trailingIcon = null,
                        enabled = true,
                        accessibilityState = AccessibilityState.Click(
                            disabledReason = "The test is not completed"
                        ),
                        onClick = {}
                    )
                }
            )

            AlertComponent(
                modifier = Modifier,
                alertStyleType = AlertStyleType.Error,
                alertFamilyType = AlertFamilyType.Message(
                    trailingComponent = {
                        IconButtonComponent(
                            modifier = Modifier
                                .padding(
                                    top = LocalDimensions.current.dp.space.space4,
                                    end = LocalDimensions.current.dp.space.space4
                                ),
                            iconButtonStyleType = IconButtonStyleType.Tertiary,
                            iconButtonSizeType = IconButtonSizeType.Medium,
                            icon = R.drawable.ic_password_hide,
                            enabled = true,
                            accessibilityState = AccessibilityState.Click(
                                disabledReason = ""
                            ),
                            contentDescription = "Close",
                            onClick = {}
                        )
                    }
                ),
                title = "Message",
                text = "This is a message",
                content = {
                    BlockButtonComponent(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space8
                            ),
                        blockButtonStyleType = BlockButtonStyleType.Primary,
                        blockButtonSizeType = BlockButtonSizeType.Small,
                        text = "Action",
                        leadingIcon = null,
                        trailingIcon = null,
                        enabled = true,
                        accessibilityState = AccessibilityState.Click(
                            disabledReason = "The test is not completed"
                        ),
                        onClick = {}
                    )
                }
            )

            AlertComponent(
                modifier = Modifier,
                alertStyleType = AlertStyleType.Warning,
                alertFamilyType = AlertFamilyType.Modal(
                    onDismiss = {}
                ),
                title = "Modal",
                text = "This is a modal",
                content = {
                    BlockButtonComponent(
                        modifier = Modifier
                            .padding(
                                top = LocalDimensions.current.dp.space.space8
                            ),
                        blockButtonStyleType = BlockButtonStyleType.Primary,
                        blockButtonSizeType = BlockButtonSizeType.Small,
                        text = "Action",
                        leadingIcon = null,
                        trailingIcon = null,
                        enabled = true,
                        accessibilityState = AccessibilityState.Click(
                            disabledReason = "The test is not completed"
                        ),
                        onClick = {}
                    )
                }
            )
        }
    }
}