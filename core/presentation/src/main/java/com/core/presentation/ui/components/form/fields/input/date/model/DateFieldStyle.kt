package com.core.presentation.ui.components.form.fields.input.date.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalPalette

@Stable
internal data class DateFieldStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val disabledBackgroundColor: Color,
    val disabledContentColor: Color,
    val selectedDayContainerColor: Color,
    val todayDateBorderColor: Color,
    val dayInSelectionRangeContainerColor: Color,
    val dividerColor: Color
)

@Composable
internal fun rememberDateFieldStyle(): DateFieldStyle {
    val palette = LocalPalette.current

    return remember(
        key1 = palette
    ) {
        DateFieldStyle(
            backgroundColor = palette.backgroundPalette.base,
            contentColor = palette.contentPalette.primary,
            disabledBackgroundColor = palette.backgroundPalette.disabled,
            disabledContentColor = palette.contentPalette.disabled,
            selectedDayContainerColor = palette.backgroundPalette.accent,
            todayDateBorderColor = palette.borderPalette.active,
            dayInSelectionRangeContainerColor = palette.backgroundPalette.accentLow,
            dividerColor = palette.borderPalette.standard
        )
    }
}