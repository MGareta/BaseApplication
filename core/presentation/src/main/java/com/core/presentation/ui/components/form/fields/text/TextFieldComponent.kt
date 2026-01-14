package com.core.presentation.ui.components.form.fields.text

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.core.presentation.ui.components.form.fields.text.model.TextFieldLabelData
import com.core.presentation.ui.components.form.fields.text.model.TextFieldSuccessData
import com.core.presentation.ui.components.form.fields.text.model.TextFieldSupportingData
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun TextFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    enabled: Boolean = true,
    labelData: TextFieldLabelData? = null,
    placeholder: String? = null,
    supportingData: TextFieldSupportingData? = null,
    successData: TextFieldSuccessData? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val focusManager = LocalFocusManager.current

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.dp4,
        ),
    ) {
        if (labelData != null) {
            LabelComponent(
                labelData = labelData,
                enabled = enabled,
            )
        }

        OutlinedTextField(
            modifier = modifier,
            value = state.getData() ?: "",
            enabled = enabled,
            interactionSource = interactionSource,
            isError = state.getData()?.isNotBlank() == true && !isFocused && state.hasError,
            shape = RoundedCornerShape(
                size = LocalDimensions.current.dp.dp4,
            ),
            label = null,
            placeholder = placeholder?.let {
                {
                    PlaceholderComponent(
                        placeholder = it,
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = null,
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = LocalPalette.current.contentPalette.high,
                unfocusedTextColor = LocalPalette.current.contentPalette.high,
                disabledTextColor = LocalPalette.current.contentPalette.stateDisabled,
                errorTextColor = LocalPalette.current.contentPalette.high,
                focusedContainerColor = LocalPalette.current.backgroundPalette.base,
                unfocusedContainerColor = LocalPalette.current.backgroundPalette.base,
                disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
                errorContainerColor = LocalPalette.current.backgroundPalette.base,
                cursorColor = LocalPalette.current.contentPalette.high,
                errorCursorColor = LocalPalette.current.contentPalette.high,
                selectionColors = null,
                focusedBorderColor = LocalPalette.current.borderPalette.mid,
                unfocusedBorderColor = LocalPalette.current.borderPalette.mid,
                disabledBorderColor = LocalPalette.current.borderPalette.stateDisabled,
                errorBorderColor = LocalPalette.current.semanticBorderPalette.danger,
                focusedLeadingIconColor = LocalPalette.current.contentPalette.accent,
                unfocusedLeadingIconColor = LocalPalette.current.contentPalette.accent,
                disabledLeadingIconColor = LocalPalette.current.contentPalette.stateDisabled,
                errorLeadingIconColor = LocalPalette.current.contentPalette.accent,
                focusedTrailingIconColor = LocalPalette.current.contentPalette.accent,
                unfocusedTrailingIconColor = LocalPalette.current.contentPalette.accent,
                disabledTrailingIconColor = LocalPalette.current.contentPalette.stateDisabled,
                errorTrailingIconColor = LocalPalette.current.contentPalette.accent,
                focusedPlaceholderColor = LocalPalette.current.contentPalette.low,
                unfocusedPlaceholderColor = LocalPalette.current.contentPalette.low,
                disabledPlaceholderColor = LocalPalette.current.contentPalette.stateDisabled,
                errorPlaceholderColor = LocalPalette.current.contentPalette.low,
                focusedPrefixColor = LocalPalette.current.contentPalette.low,
                unfocusedPrefixColor = LocalPalette.current.contentPalette.low,
                disabledPrefixColor = LocalPalette.current.contentPalette.stateDisabled,
                errorPrefixColor = LocalPalette.current.contentPalette.low,
                focusedSuffixColor = LocalPalette.current.contentPalette.low,
                unfocusedSuffixColor = LocalPalette.current.contentPalette.low,
                disabledSuffixColor = LocalPalette.current.contentPalette.stateDisabled,
                errorSuffixColor = LocalPalette.current.contentPalette.low,
            ),
            textStyle = LocalTypographies.current.mid.m,
            onValueChange = {
                state.change(update = it)
            },
        )

        if (state.getData()?.isNotBlank() == true) {
            when {
                (!isFocused && state.hasError) -> ErrorComponent(
                    state = state,
                )

                !isFocused && successData != null -> SuccessComponent(
                    successData = successData,
                )

                else -> if (supportingData != null) {
                    SupportingComponent(
                        supportingData = supportingData,
                    )
                }
            }
        } else {
            if (supportingData != null) {
                SupportingComponent(
                    supportingData = supportingData,
                )
            }
        }
    }
}

@Composable
private fun LabelComponent(labelData: TextFieldLabelData, enabled: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.dp8,
        ),
    ) {
        Text(
            modifier = Modifier
                .weight(
                    weight = 1F,
                ),
            text = labelData.label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (enabled) {
                LocalPalette.current.contentPalette.mid
            } else {
                LocalPalette.current.contentPalette.stateDisabled
            },
            style = LocalTypographies.current.mid.s,
        )

        labelData.icon?.let {
            IconButton(
                modifier = Modifier
                    .size(
                        size = LocalDimensions.current.dp.dp16,
                    ),
                colors = IconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = LocalPalette.current.contentPalette.accent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
                ),
                onClick = labelData.icon.onClick,
            ) {
                Icon(
                    painter = painterResource(
                        id = labelData.icon.icon,
                    ),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun PlaceholderComponent(placeholder: String) {
    Text(
        text = placeholder,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = LocalPalette.current.contentPalette.low,
        style = LocalTypographies.current.mid.m,
    )
}

@Composable
private fun SupportingComponent(supportingData: TextFieldSupportingData) {
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = supportingData.text,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = LocalPalette.current.contentPalette.low,
        style = LocalTypographies.current.low.xs,
    )
}

@Composable
private fun ErrorComponent(state: TextFieldState) {
    Text(
        modifier = Modifier,
        text = state.errorMessage,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = LocalPalette.current.semanticContentPalette.danger,
        style = LocalTypographies.current.low.xs,
    )
}

@Composable
private fun SuccessComponent(successData: TextFieldSuccessData) {
    Text(
        modifier = Modifier,
        text = successData.text,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = LocalPalette.current.semanticContentPalette.success,
        style = LocalTypographies.current.low.xs,
    )
}
