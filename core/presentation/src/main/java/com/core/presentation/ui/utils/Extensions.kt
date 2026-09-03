package com.core.presentation.ui.utils

import android.icu.text.DecimalFormatSymbols
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Dp.dpToRoundPx() = with(LocalDensity.current) { this@dpToRoundPx.roundToPx() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Modifier.thenOnTrue(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier
) = thenInternal(condition, onTrue = block)

@Composable
fun Modifier.thenOnFalse(
    condition: Boolean,
    block: @Composable Modifier.() -> Modifier
) = thenInternal(condition, onFalse = block)

@Composable
fun Modifier.then(
    condition: Boolean,
    onTrue: @Composable Modifier.() -> Modifier,
    onFalse: @Composable Modifier.() -> Modifier
) = thenInternal(condition, onTrue, onFalse)

@Composable
fun <T : Any> Modifier.thenOnNotNull(
    condition: T?,
    block: @Composable Modifier.(T) -> Modifier
) = thenInternal(condition, onNotNull = block)

@Composable
fun <T : Any> Modifier.thenOnNull(
    condition: T?,
    block: @Composable Modifier.() -> Modifier
) = thenInternal(condition, onNull = block)

@Composable
fun <T : Any> Modifier.then(
    condition: T?,
    onNotNull: @Composable Modifier.(T) -> Modifier,
    onNull: @Composable Modifier.() -> Modifier
) = thenInternal(condition, onNotNull, onNull)

@Composable
private fun Modifier.thenInternal(
    condition: Boolean,
    onTrue: (@Composable Modifier.() -> Modifier)? = null,
    onFalse: (@Composable Modifier.() -> Modifier)? = null
) = (if (condition) {
    onTrue?.let { then(Modifier.it()) }
} else {
    onFalse?.let { then(Modifier.it()) }
}) ?: this

@Composable
private fun <T : Any> Modifier.thenInternal(
    condition: T?,
    onNotNull: (@Composable Modifier.(T) -> Modifier)? = null,
    onNull: (@Composable Modifier.() -> Modifier)? = null
) = (condition?.let {
    onNotNull?.let { then(Modifier.it(condition))}
} ?: run {
    onNull?.let { then(Modifier.it()) }
}) ?: this

fun Modifier.ignoreHorizontalParentPadding(
    horizontal: Dp
) = this.layout { measurable, constraints ->
    val placeable = measurable.measure(
        constraints = constraints.copy(
            maxWidth = constraints.maxWidth + 2 * horizontal.roundToPx()
        )
    )

    layout(
        width = placeable.width,
        height = placeable.height
    ) {
        placeable.place(
            x = 0,
            y = 0
        )
    }
}

fun String.inputToAmount(
    fractionDigits: Int
): BigDecimal {
    if (this.isEmpty()) {
        return BigDecimal(0.0)
    }

    return BigDecimal(
        BigInteger(this),
        fractionDigits
    )
}

internal fun Number?.splitByDecimalSeparator(
    fractionDigits: Int?,
    positivePrefix: String? = null,
    locale: Locale = Locale.getDefault()
): Pair<String, String?> {
    val numberFormatter = NumberFormat.getInstance(locale).apply {
        this.minimumFractionDigits = fractionDigits
            ?: currency?.defaultFractionDigits ?: 2
        this.maximumFractionDigits = fractionDigits
            ?: currency?.defaultFractionDigits ?: 2
        this.roundingMode = RoundingMode.DOWN
    } as DecimalFormat

    if (this == null) {
        return Pair(
            first = "-".repeat(
                n = 3
            ),
            second = numberFormatter.decimalFormatSymbols.decimalSeparator
                .plus(
                    other = "-".repeat(
                        n = numberFormatter.maximumFractionDigits
                    )
                )
        )
    }

    val number = numberFormatter.apply {
        this.positivePrefix = positivePrefix ?: ""
    }.format(this.toDouble())

    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val separator = number.indexOf(char = decimalSeparator)

    if (separator < 0) {
        return Pair(number, null)
    }

    val integerPart = number.substring(startIndex = 0, endIndex = separator)
    val decimalPart = number.substring(startIndex = separator)

    return Pair(integerPart, decimalPart)
}
