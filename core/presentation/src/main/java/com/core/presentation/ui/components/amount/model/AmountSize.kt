package com.core.presentation.ui.components.amount.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import com.core.presentation.ui.theme.LocalTypographies

@Stable
sealed interface AmountSizeType {
    data object Small : AmountSizeType
    data object Medium : AmountSizeType
    data object Large : AmountSizeType
    data class Custom(
        val integerStyle: TextStyle,
        val decimalStyle: TextStyle
    ) : AmountSizeType
}

@Stable
internal data class AmountSize(
    val integerStyle: TextStyle,
    val decimalStyle: TextStyle
)


@Composable
internal fun rememberAmountSize(
    amountSizeType: AmountSizeType
): AmountSize {
    val typographies = LocalTypographies.current

    return remember(
        key1 = amountSizeType,
        key2 = typographies
    ) {
        when (amountSizeType) {
            AmountSizeType.Small -> AmountSize(
                integerStyle = typographies.body.small,
                decimalStyle = typographies.label.small
            )
            AmountSizeType.Medium -> AmountSize(
                integerStyle = typographies.body.medium,
                decimalStyle = typographies.label.small
            )
            AmountSizeType.Large -> AmountSize(
                integerStyle = typographies.body.large,
                decimalStyle = typographies.body.small
            )
            is AmountSizeType.Custom -> AmountSize(
                integerStyle = amountSizeType.integerStyle,
                decimalStyle = amountSizeType.decimalStyle
            )
        }
    }
}
