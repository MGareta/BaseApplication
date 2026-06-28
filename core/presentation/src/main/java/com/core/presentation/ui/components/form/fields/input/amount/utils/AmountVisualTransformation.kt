package com.core.presentation.ui.components.form.fields.input.amount.utils

import android.icu.util.Currency
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.PlatformParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import com.core.presentation.ui.components.form.fields.input.amount.model.AmountFieldStyle
import com.core.presentation.ui.components.form.fields.input.amount.model.CurrencyType
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

private const val EMPTY_VALUE = "--"

enum class OffsetMappingType {
    FIXED,
    MOVABLE
}

internal class AmountVisualTransformation(
    private val fractionDigits: Int?,
    private val currencyType: CurrencyType?,
    private val style: AmountFieldStyle,
    private val cursor: OffsetMappingType = OffsetMappingType.MOVABLE,
    locale: Locale = Locale.getDefault()
) : VisualTransformation {

    private val symbols = NumberFormat
        .getInstance(
            locale
        ).apply {
            this.minimumFractionDigits = fractionDigits ?: currency?.defaultFractionDigits ?: 2
            this.maximumFractionDigits = fractionDigits ?: currency?.defaultFractionDigits ?: 2
    } as DecimalFormat

    override fun filter(text: AnnotatedString): TransformedText {
        val thousandsSeparator = symbols.decimalFormatSymbols.groupingSeparator
        val decimalSeparator = symbols.decimalFormatSymbols.decimalSeparator
        val numberOfDecimals = symbols.maximumFractionDigits
        val zero = symbols.decimalFormatSymbols.zeroDigit

        val currency = when(currencyType) {
            is CurrencyType.Symbol -> symbols.currency?.symbol ?: Currency.getInstance(
                currencyType.currency
            ).symbol
            is CurrencyType.Code -> symbols.currency?.currencyCode ?: Currency.getInstance(
                currencyType.currency
            ).currencyCode
            is CurrencyType.Custom -> currencyType.currency
            else -> null
        }

        val inputText = if (text.text.isEmpty()) {
            Pair(
                EMPTY_VALUE,
                null
            )
        } else {
            Pair(
                text.text
                    .dropLast(
                        n = numberOfDecimals
                    )
                    .reversed()
                    .chunked(
                        size = 3
                    )
                    .joinToString(
                        separator = thousandsSeparator.toString()
                    )
                    .reversed()
                    .ifEmpty {
                        zero.toString()
                    },
                text.text.takeLast(numberOfDecimals).let {
                    if (it.length != numberOfDecimals) {
                        List(numberOfDecimals - it.length) {
                            zero
                        }.joinToString("") + it
                    } else {
                        it
                    }
                }
            )
        }

        val formattedText = buildFormattedText(
            inputText = inputText,
            style = style,
            numberOfDecimals = numberOfDecimals,
            decimalSeparator = decimalSeparator,
            currency = currency
        )

        val offsetMapping = when (cursor) {
            OffsetMappingType.FIXED -> FixedCursorOffsetMapping(
                original = text.text,
                input = inputText,
                numberOfDecimals = numberOfDecimals,
                decimalSeparator = decimalSeparator
            )

            OffsetMappingType.MOVABLE -> MovableCursorOffsetMapping(
                original = text.text,
                transform = formattedText.text,
                currency = currency,
                numberOfDecimals = numberOfDecimals
            )
        }

        return TransformedText(
            text = formattedText,
            offsetMapping = offsetMapping
        )
    }

    private fun buildFormattedText(
        inputText: Pair<String, String?>,
        style: AmountFieldStyle,
        numberOfDecimals: Int,
        decimalSeparator: Char,
        currency: String?
    ) = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(
                    lineHeight = style.integerTextStyle.lineHeight,
                    platformStyle = PlatformParagraphStyle(
                        includeFontPadding = true
                    )
                )
            ) {
                withStyle(
                    style = SpanStyle(
                        fontFamily = style.integerTextStyle.fontFamily,
                        fontSize = style.integerTextStyle.fontSize
                    )
                ) {
                    append(inputText.first)
                }

                withStyle(
                    style = SpanStyle(
                        fontFamily = style.decimalTextStyle.fontFamily,
                        fontSize = style.decimalTextStyle.fontSize
                    )
                ) {
                    if (inputText.second?.isNotBlank() == true && numberOfDecimals > 0) {
                        append(decimalSeparator)
                        append(inputText.second)
                    }

                    currency?.let {
                        append(" $it")
                    }
                }
            }
        }

    private class FixedCursorOffsetMapping(
        private val original: String,
        private val input: Pair<String, String?>,
        private val numberOfDecimals: Int,
        private val decimalSeparator: Char
    ) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            if (input.second?.isNotBlank() == true && numberOfDecimals > 0) {
                return (input.first + decimalSeparator + input.second).length
            }

            return input.first.length
        }

        override fun transformedToOriginal(offset: Int): Int = original.length
    }

    private class MovableCursorOffsetMapping(
        private val original: String,
        private val transform: String,
        private val currency: String?,
        private val numberOfDecimals: Int
    ) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            val currencyLength = currency?.length?.plus(1) ?: 0

            return when {
                original.length <= numberOfDecimals -> {
                    transform.dropLast(
                            n = currencyLength
                        ).length - (original.length - offset)
                }
                else -> {
                    offset + offsetMaskCount(
                        offset = offset,
                        maskedText = transform
                            .dropLast(
                                n = currencyLength
                            )
                    )
                }
            }
        }

        override fun transformedToOriginal(offset: Int): Int =
            when {
                original.length <= numberOfDecimals -> {
                    max(
                        a = original.length - (transform.length - offset),
                        b = 0
                    )
                }
                else -> {
                    offset - transform
                        .take(
                            n = offset
                        ).count {
                            !it.isDigit()
                        }
                }
            }

        private fun offsetMaskCount(offset: Int, maskedText: String): Int {
            var maskOffsetCount = 0
            var dataCount = 0

            for (maskChar in maskedText) {
                if (!maskChar.isDigit()) {
                    maskOffsetCount++
                } else if (++dataCount > offset) {
                    break
                }
            }

            return maskOffsetCount
        }
    }
}