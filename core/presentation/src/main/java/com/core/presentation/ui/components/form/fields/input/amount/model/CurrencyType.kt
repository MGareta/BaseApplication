package com.core.presentation.ui.components.form.fields.input.amount.model

import android.icu.util.Currency
import androidx.compose.runtime.Immutable

@Immutable
sealed class CurrencyType(
    val currency: String
) {
    data class Symbol(
        val value: Currency
    ) : CurrencyType(
        currency = value.symbol
    )

    data class Code(
        val value: Currency
    ) : CurrencyType(
        currency = value.currencyCode
    )

    data class Custom(
        val value: String
    ) : CurrencyType(
        currency = value
    )
}