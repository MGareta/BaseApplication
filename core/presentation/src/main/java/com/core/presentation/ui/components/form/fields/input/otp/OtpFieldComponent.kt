package com.core.presentation.ui.components.form.fields.input.otp

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.maxTextLength
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.button.icon.IconButtonComponent
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.components.form.fields.input.TextFieldState
import com.core.presentation.ui.components.form.fields.input.otp.model.rememberOtpFieldStyle
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun OtpFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    helpText: String? = null,
    successText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val limit = remember(
        key1 = state
    ) {
        state.validators
            .filterIsInstance<Validators.MaxSize<String>>()
            .firstOrNull()
            ?.limit
            ?: error(
                message = "Max size validator is mandatory"
            )
    }

    val isError = remember {
        derivedStateOf {
            return@derivedStateOf state.hasError
                    && state.value?.isNotBlank() == true
        }
    }

    var visualTransformation: VisualTransformation by remember {
        mutableStateOf(
            value = PasswordVisualTransformation()
        )
    }

    val isHovered = interactionSource
        .collectIsHoveredAsState()
        .value

    val isFocused = interactionSource
        .collectIsFocusedAsState()
        .value

    BasicTextField(
        modifier = modifier
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Tab -> {
                            focusManager.moveFocus(
                                focusDirection = if (event.isShiftPressed) {
                                    FocusDirection.Previous
                                } else {
                                    FocusDirection.Next
                                }
                            )

                            true
                        }

                        else -> false
                    }
                } else {
                    event.key == Key.Tab
                }
            }
            .semantics {
                maxTextLength = limit

                if (isError.value && state.errorMessage != null) {
                    error(
                        description = state.errorMessage!!
                    )
                }

                if (!enabled) {
                    disabled()
                }
            },
        value = state.value ?: "",
        enabled = enabled,
        interactionSource = interactionSource,
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.NumberPassword
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardActions.onDone?.invoke(this)
            },
            onGo = {
                focusManager.clearFocus()
                keyboardActions.onGo?.invoke(this)
            },
            onNext = {
                focusManager.moveFocus(
                    focusDirection = FocusDirection.Next,
                )
            },
            onPrevious = {
                focusManager.moveFocus(
                    focusDirection = FocusDirection.Previous,
                )
            },
            onSearch = {
                focusManager.clearFocus()
                keyboardActions.onSearch?.invoke(this)
            },
            onSend = {
                focusManager.clearFocus()
                keyboardActions.onSend?.invoke(this)
            },
        ),
        visualTransformation = visualTransformation,
        decorationBox = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = LocalDimensions.current.dp.space.space4
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.dp.space.space4,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(limit) { index ->
                        DigitComponent(
                            state = state,
                            visualTransformation = visualTransformation,
                            enabled = enabled,
                            isHovered = isHovered,
                            isFocused = isFocused,
                            isError = isError.value,
                            index = index,
                            limit = limit
                        )
                    }

                    IconButtonComponent(
                        modifier = Modifier
                            .semantics {
                                role = Role.Switch
                                toggleableState = if (visualTransformation == VisualTransformation.None) {
                                    ToggleableState.On
                                } else {
                                    ToggleableState.Off
                                }
                            },
                        iconButtonStyleType = IconButtonStyleType.Tertiary,
                        iconButtonSizeType = IconButtonSizeType.Medium,
                        icon = if (visualTransformation == VisualTransformation.None) {
                            R.drawable.ic_password_hide
                        } else {
                            R.drawable.ic_password_show
                        },
                        enabled = true,
                        accessibilityState = AccessibilityState.Click(
                            disabledReason = ""
                        ),
                        contentDescription = stringResource(
                            id = R.string.show_password
                        )
                    ) {
                        visualTransformation = if (visualTransformation == VisualTransformation.None) {
                            PasswordVisualTransformation()
                        } else {
                            VisualTransformation.None
                        }
                    }
                }

                SupportingComponent(
                    modifier = Modifier,
                    state = state,
                    isFocused = isFocused,
                    isError = isError.value,
                    helpText = helpText,
                    successText = successText
                )
            }
        },
        onValueChange = {
            if (it.length <= limit) {
                state.change(
                    value = it.trim()
                )
            }
        }
    )
}

@Composable
private fun RowScope.DigitComponent(
    state: TextFieldState,
    visualTransformation: VisualTransformation,
    enabled: Boolean,
    isHovered: Boolean,
    isFocused: Boolean,
    isError: Boolean,
    index: Int,
    limit: Int
) {
    val dimensions = LocalDimensions.current

    val infiniteTransition = rememberInfiniteTransition()
    val cursorAnimationAlpha by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = 1F,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val otpFieldStyle = rememberOtpFieldStyle(
        enabled = enabled,
        isError = isError,
        isFocused = isFocused,
        isDigitFocused = isFocused && index == (state.value?.length ?: 0)
            .coerceAtMost(
                maximumValue = limit - 1
            ),
        isDigitHovered = isHovered && index == (state.value?.length ?: 0)
            .coerceAtMost(
                maximumValue = limit - 1
            )
    )

    Text(
        modifier = Modifier
            .drawWithContent {
                drawContent()

                if (isFocused && index == (state.value?.length ?: 0)) {
                    drawRect(
                        color = otpFieldStyle.contentColor.copy(
                            alpha = cursorAnimationAlpha
                        ),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x = (size.width - dimensions.dp.space.space2.toPx()) / 2,
                            y = (size.height - dimensions.dp.space.space24.toPx()) / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            width = dimensions.dp.space.space2.toPx(),
                            height = dimensions.dp.space.space24.toPx()
                        )
                    )
                }
            }
            .weight(
                weight = 1F
            )
            .heightIn(
                min = LocalDimensions.current.dp.space.space48
            )
            .background(
                color = otpFieldStyle.backgroundColor,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius4
                )
            )
            .border(
                border = otpFieldStyle.borderStroke,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius4
                )
            )
            .semantics(
                mergeDescendants = true
            ) {
                hideFromAccessibility()
            }
            .wrapContentHeight(
                align = Alignment.CenterVertically
            ),
        text = visualTransformation.filter(
            text = AnnotatedString(
                text = state.value?.getOrNull(index = index)
                    ?.toString() ?: ""
            )
        ).text.text,
        color = otpFieldStyle.contentColor,
        textAlign = TextAlign.Center,
        style = LocalTypographies.current.title.medium
            .copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = visualTransformation == VisualTransformation.None
                )
            )
    )
}

@Composable
private fun SupportingComponent(
    modifier: Modifier,
    state: TextFieldState,
    isFocused: Boolean,
    isError: Boolean,
    helpText: String?,
    successText: String?
) {
    when {
        !isFocused && isError -> ErrorComponent(
            modifier = modifier,
            state = state,
        )

        !isFocused && successText != null -> SuccessComponent(
            modifier = modifier,
            text = successText
        )

        else -> if (helpText != null) {
            HelpComponent(
                modifier = modifier,
                text = helpText
            )
        }
    }
}

@Composable
private fun HelpComponent(
    modifier: Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = LocalPalette.current.contentPalette.secondary,
        style = LocalTypographies.current.label.medium
    )
}

@Composable
private fun ErrorComponent(
    modifier: Modifier,
    state: TextFieldState
) {
    state.errorMessage?.let {
        Text(
            modifier = modifier
                .semantics {
                    hideFromAccessibility()
                },
            text = it,
            color = LocalPalette.current.contentPalette.error,
            style = LocalTypographies.current.label.medium
        )
    }
}

@Composable
private fun SuccessComponent(
    modifier: Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        color = LocalPalette.current.contentPalette.success,
        style = LocalTypographies.current.label.medium
    )
}

@Preview
@Composable
private fun OtpFieldComponentLightPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.trim()
            },
            validators = listOf(
                Validators.Required("Required"),
                Validators.MaxSize(
                    limit = 8,
                    message = {
                        "Invalid value"
                    }
                ),
                Validators.Custom(
                    message = "Test input not valid",
                    function = {
                        it.length < 8
                    }
                )
            )
        )
    }

    Theme {
        Scaffold {
            Column(
                modifier = Modifier
                    .padding(
                        paddingValues = it
                    )
            ) {
                OtpFieldComponent(
                    modifier = Modifier,
                    state = state,
                    helpText = "Only accepts numbers",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions {
                        state.validate()
                    },
                    enabled = true
                )

                BlockButtonComponent(
                    modifier = Modifier,
                    blockButtonStyleType = BlockButtonStyleType.Primary,
                    blockButtonSizeType = BlockButtonSizeType.Small,
                    text = "Test",
                    leadingIcon = android.R.drawable.star_on,
                    trailingIcon = android.R.drawable.star_on,
                    enabled = state.validate(),
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = "The test is not completed"
                    ),
                    onClick = {}
                )
            }
        }
    }
}
