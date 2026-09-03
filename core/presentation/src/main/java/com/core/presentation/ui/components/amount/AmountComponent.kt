package com.core.presentation.ui.components.amount

import android.annotation.SuppressLint
import android.icu.math.BigDecimal
import android.icu.util.Currency
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.amount.model.AmountSizeType
import com.core.presentation.ui.components.amount.model.rememberAmountSize
import com.core.presentation.ui.components.form.fields.input.amount.model.CurrencyType
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.splitByDecimalSeparator
import java.util.Locale

@Composable
fun AmountComponent(
    modifier: Modifier,
    amountSizeType: AmountSizeType,
    amount: BigDecimal?,
    fractionDigits: Int?,
    positivePrefix: String?,
    currencyType: CurrencyType,
    locale: Locale = Locale.getDefault()
) {
    val context = LocalContext.current

    val amountSize = rememberAmountSize(
        amountSizeType = amountSizeType
    )

    val number = amount.splitByDecimalSeparator(
        fractionDigits = fractionDigits,
        positivePrefix = positivePrefix,
        locale = locale
    )

    val color = LocalPalette.current.contentPalette.primary

    Text(
        modifier = modifier
            .semantics {
                if (amount == null) {
                    @SuppressLint("LocalContextGetResourceValueCall")
                    contentDescription = context.getString(
                        R.string.accessibility_amount_empty
                    )
                }
            },
        text = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(
                    lineHeight = amountSize.integerStyle.lineHeight,
                    platformStyle = amountSize.integerStyle.platformStyle?.paragraphStyle
                )
            ) {
                withStyle(
                    style = SpanStyle(
                        fontFamily = amountSize.integerStyle.fontFamily,
                        fontSize = amountSize.integerStyle.fontSize,
                        color = color
                    )
                ) {
                    append(number.first)

                    if (fractionDigits == null || fractionDigits == 0) {
                        append(" ${currencyType.currency}")
                    }
                }

                if (fractionDigits != null && fractionDigits > 0) {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = amountSize.decimalStyle.fontFamily,
                            fontSize = amountSize.decimalStyle.fontSize,
                            color = color
                        )
                    ) {
                        if (number.second?.isNotBlank() == true) {
                            append(number.second)
                        }

                        append(" ${currencyType.currency}")

                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun AmountComponentPreview() {
    Theme {
        AmountComponent(
            modifier = Modifier
                .safeContentPadding(),
            amountSizeType = AmountSizeType.Large,
            amount = BigDecimal(-100),
            fractionDigits = 2,
            positivePrefix = null,
            currencyType = CurrencyType.Code(
                value = Currency.getInstance(
                    Locale.getDefault()
                )
            )
        )
    }
}
