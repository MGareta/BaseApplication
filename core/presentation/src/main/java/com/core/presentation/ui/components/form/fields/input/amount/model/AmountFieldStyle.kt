package com.core.presentation.ui.components.form.fields.input.amount.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import com.core.presentation.ui.theme.LocalTypographies

@Stable
internal data class AmountFieldStyle(
    val integerTextStyle: TextStyle,
    val decimalTextStyle: TextStyle
)

@Composable
internal fun rememberAmountFieldStyle(): AmountFieldStyle {
    val typographies = LocalTypographies.current

    return remember(
        key1 = typographies
    ) {
        AmountFieldStyle(
            integerTextStyle = typographies.title.large,
            decimalTextStyle = typographies.body.large
        )
    }
}
