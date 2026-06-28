package com.core.presentation.ui.components.form.fields.input.amount

import android.icu.util.Currency
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.form.fields.input.TextFieldState
import com.core.presentation.ui.components.form.fields.input.amount.model.CurrencyType
import com.core.presentation.ui.components.form.fields.input.amount.model.rememberAmountFieldStyle
import com.core.presentation.ui.components.form.fields.input.amount.utils.AmountVisualTransformation
import com.core.presentation.ui.components.form.fields.input.text.TextFieldComponent
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import com.core.presentation.ui.utils.inputToAmount
import java.util.Locale

@Composable
fun AmountFieldComponent(
    modifier: Modifier,
    state: TextFieldState,
    fractionDigits: Int?,
    currencyType: CurrencyType,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    enabled: Boolean
) {
    val amountFieldStyle = rememberAmountFieldStyle()
    val currency = remember(
        key1 = currencyType
    ) {
        when(currencyType) {
            is CurrencyType.Symbol -> Currency.getInstance(
                currencyType.currency
            ).symbol
            is CurrencyType.Code -> Currency.getInstance(
                currencyType.currency
            ).currencyCode
            is CurrencyType.Custom -> currencyType.currency
        }
    }

    TextFieldComponent(
        modifier = modifier,
        state = state,
        placeholder = placeholder,
        helpText = helpText,
        successText = successText,
        suffix = {
            Text(
                modifier = Modifier
                    .padding(
                        start = LocalDimensions.current.dp.space.space4
                    ),
                text = currency,
                style = amountFieldStyle.integerTextStyle
                    .copy(
                        fontWeight = FontWeight.Light
                    )
            )
        },
        visualTransformation = AmountVisualTransformation(
            fractionDigits = fractionDigits,
            currencyType = null,
            style = amountFieldStyle
        ),
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = keyboardActions,
        textAlign = TextAlign.End,
        enabled = enabled,
        onValueChange = {
            if (it.isDigitsOnly()) {
                if (it.startsWith(prefix = "0")) {
                    return@TextFieldComponent
                } else {
                    state.change(
                        value = it
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun AmountFieldComponentLightPreview() {
    val state = remember {
        TextFieldState(
            initial = null,
            transform = {
                it.inputToAmount(
                    fractionDigits = 2,
                ).toPlainString()
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
                AmountFieldComponent(
                    modifier = Modifier,
                    state = state,
                    fractionDigits = 2,
                    currencyType = CurrencyType.Code(
                        value = Currency.getInstance(
                            Locale.getDefault()
                        )
                    ),
                    placeholder = "Quantity",
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
                    onClick = {
                        state.getData()
                    }
                )
            }
        }
    }
}
