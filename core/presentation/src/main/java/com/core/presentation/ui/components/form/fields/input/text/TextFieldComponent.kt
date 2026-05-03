package com.core.presentation.ui.components.form.fields.input.text

import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.form.fields.input.TextFieldState
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import com.core.presentation.ui.utils.ignoreHorizontalParentPadding

@Composable
fun TextFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused = interactionSource.collectIsFocusedAsState().value

    OutlinedTextField(
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
            },
        value = state.value ?: "",
        enabled = enabled,
        interactionSource = interactionSource,
        isError = state.value?.isNotBlank() == true && !isFocused && state.hasError,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.radius.radius4,
        ),
        label = placeholder?.let {
            {
                PlaceholderComponent(
                    placeholder = it,
                )
            }
        },
        placeholder = null,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            SupportingComponent(
                modifier = Modifier
                    .ignoreHorizontalParentPadding(
                        horizontal = LocalDimensions.current.dp.space.space16
                    ),
                state = state,
                isFocused = isFocused,
                helpText = helpText,
                successText = successText
            )
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
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
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LocalPalette.current.contentPalette.primary,
            unfocusedTextColor = LocalPalette.current.contentPalette.secondary,
            disabledTextColor = LocalPalette.current.contentPalette.disabled,
            errorTextColor = LocalPalette.current.contentPalette.error,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = LocalPalette.current.backgroundPalette.disabled,
            errorContainerColor = LocalPalette.current.backgroundPalette.errorLow,
            cursorColor = LocalPalette.current.contentPalette.primary,
            errorCursorColor = LocalPalette.current.contentPalette.primary,
            selectionColors = null,
            focusedBorderColor = LocalPalette.current.borderPalette.active,
            unfocusedBorderColor = LocalPalette.current.borderPalette.standard,
            disabledBorderColor = LocalPalette.current.borderPalette.disabled,
            errorBorderColor = LocalPalette.current.borderPalette.error,
            focusedLeadingIconColor = LocalPalette.current.contentPalette.primary,
            unfocusedLeadingIconColor = LocalPalette.current.contentPalette.secondary,
            disabledLeadingIconColor = LocalPalette.current.contentPalette.disabled,
            errorLeadingIconColor = LocalPalette.current.contentPalette.error,
            focusedTrailingIconColor = LocalPalette.current.contentPalette.primary,
            unfocusedTrailingIconColor = LocalPalette.current.contentPalette.secondary,
            disabledTrailingIconColor = LocalPalette.current.contentPalette.disabled,
            errorTrailingIconColor = LocalPalette.current.contentPalette.error,
            focusedLabelColor = LocalPalette.current.contentPalette.primary,
            unfocusedLabelColor = LocalPalette.current.contentPalette.secondary,
            disabledLabelColor = LocalPalette.current.contentPalette.disabled,
            errorLabelColor = LocalPalette.current.contentPalette.error,
            focusedPrefixColor = LocalPalette.current.contentPalette.primary,
            unfocusedPrefixColor = LocalPalette.current.contentPalette.secondary,
            disabledPrefixColor = LocalPalette.current.contentPalette.disabled,
            errorPrefixColor = LocalPalette.current.contentPalette.error,
            focusedSuffixColor = LocalPalette.current.contentPalette.primary,
            unfocusedSuffixColor = LocalPalette.current.contentPalette.secondary,
            disabledSuffixColor = LocalPalette.current.contentPalette.disabled,
            errorSuffixColor = LocalPalette.current.contentPalette.error
        ),
        textStyle = LocalTypographies.current.body.medium,
        onValueChange = {
            state.change(
                value = it
            )
        }
    )
}

@Composable
private fun PlaceholderComponent(
    placeholder: String
) {
    Text(
        text = placeholder,
        style = LocalTypographies.current.body.medium
    )
}

@Composable
private fun SupportingComponent(
    modifier: Modifier,
    state: TextFieldState,
    isFocused: Boolean,
    helpText: String?,
    successText: String?
) {
    if (state.value?.isNotBlank() == true) {
        when {
            !isFocused && state.hasError -> ErrorComponent(
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
    } else {
        if (helpText != null) {
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
            modifier = modifier,
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
private fun TextFieldComponentLightPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.trim()
            },
            validators = listOf(
                Validators.Required("Required"),
                Validators.Custom<String>(
                    message = "Test input not valid",
                    function = {
                        false
                    }
                )
            )
        )
    }

    Theme {
        Column(
            modifier = Modifier
                .safeContentPadding()
        ) {
            TextFieldComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Placeholder",
                helpText = "Only accepts numbers",
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.star_on
                        ),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.star_on
                        ),
                        contentDescription = null
                    )
                },
                prefix = {
                    Text(
                        text = "test",
                        style = LocalTypographies.current.body.medium
                    )
                },
                suffix = {
                    Text(
                        text = "test",
                        style = LocalTypographies.current.body.medium
                    )
                },
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
                enabled = true,
                accessibilityState = AccessibilityState.Click(
                    disabledReason = "The test is not completed"
                ),
                onClick = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun TextFieldComponentDarkPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.trim()
            },
            validators = listOf(
                Validators.Required("Required"),
                Validators.Custom<String>(
                    message = "Test input not valid",
                    function = {
                        false
                    }
                )
            )
        )
    }

    Theme {
        Column(
            modifier = Modifier
                .safeContentPadding()
        ) {
            TextFieldComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Placeholder",
                helpText = "Only accepts numbers",
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.star_on
                        ),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = android.R.drawable.star_on
                        ),
                        contentDescription = null
                    )
                },
                prefix = {
                    Text(
                        text = "test",
                        style = LocalTypographies.current.body.medium
                    )
                },
                suffix = {
                    Text(
                        text = "test",
                        style = LocalTypographies.current.body.medium
                    )
                },
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
                enabled = true,
                accessibilityState = AccessibilityState.Click(
                    disabledReason = "The test is not completed"
                ),
                onClick = {}
            )
        }
    }
}

