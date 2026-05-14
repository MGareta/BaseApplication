package com.core.presentation.ui.components.form.fields.input.text

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
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
fun TextAreaComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,
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

    val isFocused = interactionSource.collectIsFocusedAsState().value
    val isError = remember {
        derivedStateOf {
            return@derivedStateOf state.hasError
                    && state.value?.isNotBlank() == true
        }
    }


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
            }
            .semantics(
                mergeDescendants = true
            ) {
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
        isError = isError.value && !isFocused,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.radius.radius4,
        ),
        label = {
            PlaceholderComponent(
                placeholder = placeholder
            )
        },
        placeholder = null,
        leadingIcon = null,
        trailingIcon = null,
        prefix = null,
        suffix = null,
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
        visualTransformation = VisualTransformation.None,
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
        minLines = 5,
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
            focusedBorderColor = LocalPalette.current.borderPalette.active,
            unfocusedBorderColor = LocalPalette.current.borderPalette.standard,
            disabledBorderColor = LocalPalette.current.borderPalette.disabled,
            errorBorderColor = LocalPalette.current.borderPalette.error,
            focusedLabelColor = LocalPalette.current.contentPalette.primary,
            unfocusedLabelColor = LocalPalette.current.contentPalette.secondary,
            disabledLabelColor = LocalPalette.current.contentPalette.disabled,
            errorLabelColor = LocalPalette.current.contentPalette.error
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
    val context = LocalContext.current

    val limit = remember(
        key1 = state
    ) {
        state.validators
            .filterIsInstance<Validators.MaxSize<String>>()
            .firstOrNull()
            ?.limit
            ?.toString()
            ?: "\u221E"
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.value?.isNotBlank() == true) {
            when {
                !isFocused && state.hasError -> ErrorComponent(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        ),
                    state = state
                )

                !isFocused && successText != null -> SuccessComponent(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        ),
                    text = successText
                )

                else -> if (helpText != null) {
                    HelpComponent(
                        modifier = Modifier
                            .weight(
                                weight = 1F
                            ),
                        text = helpText
                    )
                }
            }
        } else {
            if (helpText != null) {
                HelpComponent(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        ),
                    text = helpText
                )
            }
        }

        Text(
            modifier = Modifier
                .semantics {
                    if (state.value?.isNotEmpty() == true && state.hasError) {
                        hideFromAccessibility()
                    } else {
                        val currentChars = state.value?.length ?: 0

                        @SuppressLint("LocalContextGetResourceValueCall")
                        text = AnnotatedString(
                            text = context.getString(
                                R.string.max_chars,
                                currentChars.toString(),
                                limit
                            )
                        )
                    }
                },
            text = "${state.value?.length ?: 0}/$limit",
            color = if (state.value?.isNotBlank() == true && state.hasError) {
                LocalPalette.current.contentPalette.error
            } else {
                LocalPalette.current.contentPalette.secondary
            },
            style = LocalTypographies.current.label.medium
        )
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
private fun TextAreaComponentLightPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.trim()
            },
            validators = listOf(
                Validators.Required("Required"),
                Validators.MaxSize(
                    limit = 2,
                    message = {
                        "Max chars. Current ${it.length}. Limit 2"
                    }
                ),
                Validators.Custom(
                    message = "Test input not valid",
                    function = {
                        true
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
            TextAreaComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Placeholder",
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun TextAreaComponentDarkPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.trim()
            },
            validators = listOf(
                Validators.Required("Required"),
                Validators.Custom(
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
            TextAreaComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Placeholder",
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

