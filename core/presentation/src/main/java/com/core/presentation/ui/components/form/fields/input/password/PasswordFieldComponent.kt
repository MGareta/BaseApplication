package com.core.presentation.ui.components.form.fields.input.password

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.button.icon.IconButtonComponent
import com.core.presentation.ui.components.button.icon.model.IconButtonSizeType
import com.core.presentation.ui.components.button.icon.model.IconButtonStyleType
import com.core.presentation.ui.components.form.fields.input.TextFieldState
import com.core.presentation.ui.components.form.fields.input.text.TextFieldComponent
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun PasswordFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    placeholder: String,
    helpText: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean
) {
    var passwordVisible by remember {
        mutableStateOf(
            value = false
        )
    }

    TextFieldComponent(
        modifier = modifier,
        state = state,
        placeholder = placeholder,
        helpText = helpText,
        trailingIcon = {
            IconButtonComponent(
                modifier = Modifier,
                iconButtonStyleType = IconButtonStyleType.Tertiary,
                iconButtonSizeType = IconButtonSizeType.Medium,
                icon = if (passwordVisible) {
                    R.drawable.ic_password_hide
                } else {
                    R.drawable.ic_password_show
                },
                enabled = true,
                accessibilityState = AccessibilityState.Click(
                    disabledReason = ""
                ),
                contentDescription = if (passwordVisible) {
                    stringResource(
                        id = R.string.hide_password
                    )
                } else {
                    stringResource(
                        id = R.string.show_password
                    )
                }
            ) {
                passwordVisible = !passwordVisible
            }
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = keyboardActions,
        enabled = enabled
    )
}


@Preview
@Composable
private fun PasswordFieldComponentLightPreview() {
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
            PasswordFieldComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Password",
                helpText = null,
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
private fun PasswordFieldComponentDarkPreview() {
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
            PasswordFieldComponent(
                modifier = Modifier,
                state = state,
                placeholder = "Password",
                helpText = null,
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
