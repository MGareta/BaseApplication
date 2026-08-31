package com.core.presentation.ui.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.bottomsheet.model.BottomSheetStyleType
import com.core.presentation.ui.components.bottomsheet.model.rememberBottomSheetStyle
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.button.icon.IconButtonComponent
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetComponent(
    modifier: Modifier,
    bottomSheetStyleType: BottomSheetStyleType,
    title: String,
    text: String?,
    content: (@Composable (SheetState) -> Unit)?,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val bottomSheetStyle = rememberBottomSheetStyle(
        bottomSheetStyleType = bottomSheetStyleType
    )

    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = {
            if (it == SheetValue.Hidden) {
                onDismiss()
            }

            true
        }
    )

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = bottomSheetStyle.backgroundColor,
        contentColor = bottomSheetStyle.contentColor,
        contentWindowInsets = {
            BottomSheetDefaults.windowInsets.add(
                insets = WindowInsets(
                    left = LocalDimensions.current.dp.space.space16,
                    right = LocalDimensions.current.dp.space.space16,
                )
            )
        },
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .semantics {
                    isTraversalGroup = true
                }
        ) {
            IconButtonComponent(
                modifier = Modifier
                    .padding(
                        vertical = LocalDimensions.current.dp.space.space16
                    )
                    .align(
                        alignment = Alignment.End
                    )
                    .semantics {
                        traversalIndex = 1F
                    },
                iconButtonStyleType = IconButtonStyleType.Secondary,
                iconButtonSizeType = IconButtonSizeType.Small,
                icon = R.drawable.ic_close,
                enabled = true,
                accessibilityState = AccessibilityState.Click(
                    disabledReason = ""
                ),
                contentDescription = stringResource(
                    id = R.string.accessibility_close
                ),
                onClick = {
                    scope.launch {
                        sheetState.hide()
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        isTraversalGroup = true
                    },
                horizontalArrangement = Arrangement.spacedBy(
                    space = LocalDimensions.current.dp.space.space8
                )
            ) {
                Icon(
                    modifier = Modifier
                        .size(
                            size = LocalDimensions.current.dp.target.target24
                        ),
                    painter = painterResource(
                        id = bottomSheetStyle.icon
                    ),
                    tint = bottomSheetStyle.contentColor,
                    contentDescription = null
                )

                Text(
                    text = title,
                    color = bottomSheetStyle.contentColor,
                    style = LocalTypographies.current.title.medium.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

            text?.let {
                Text(
                    text = it,
                    color = bottomSheetStyle.contentColor,
                    style = LocalTypographies.current.body.medium.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }

            content?.invoke(sheetState)
        }
    }
}


@Preview
@Composable
private fun BottomSheetComponentPreview() {
    Theme {
        val scope = rememberCoroutineScope()

        @OptIn(ExperimentalMaterial3Api::class)
        BottomSheetComponent(
            modifier = Modifier,
            bottomSheetStyleType = BottomSheetStyleType.Success,
            title = "Bottom sheet",
            text = "This is a bottom sheet",
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
                    onClick = {
                        scope.launch {
                            it.hide()
                        }
                    }
                )
            },
            onDismiss = {
            }
        )
    }
}
