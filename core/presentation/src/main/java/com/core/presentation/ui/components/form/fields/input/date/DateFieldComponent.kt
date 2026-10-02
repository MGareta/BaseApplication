package com.core.presentation.ui.components.form.fields.input.date

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.form.fields.input.DateFieldFieldState
import com.core.presentation.ui.components.form.fields.input.TextFieldState
import com.core.presentation.ui.components.form.fields.input.date.model.rememberDateFieldStyle
import com.core.presentation.ui.components.form.fields.input.text.TextFieldComponent
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import java.util.Calendar
import java.util.Locale

@Composable
fun DateFieldComponent(
    modifier: Modifier,
    state: DateFieldFieldState,
    placeholder: String,
    helpText: String? = null,
    successText: String? = null,
    enabled: Boolean
) {
    val dateFieldStyle = rememberDateFieldStyle()

    val minDate = remember(
        key1 = state
    ) {
        state.validators
            .filterIsInstance<Validators.MinDate<Calendar>>()
            .firstOrNull()
            ?.limit
            ?: Calendar.getInstance().apply {
                set(Calendar.YEAR, DatePickerDefaults.YearRange.first)
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
            }
    }

    val maxDate = remember(
        key1 = state
    ) {
        state.validators
            .filterIsInstance<Validators.MaxDate<Calendar>>()
            .firstOrNull()
            ?.limit
            ?: Calendar.getInstance().apply {
                set(Calendar.YEAR, DatePickerDefaults.YearRange.last)
                set(Calendar.MONTH, Calendar.DECEMBER)
                set(Calendar.DAY_OF_MONTH, 31)
            }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.value?.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in minDate.timeInMillis..maxDate.timeInMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year in minDate.get(Calendar.YEAR)..maxDate.get(Calendar.YEAR)
            }
        }
    )

    var showDatePicker by remember {
        mutableStateOf(
            value = false
        )
    }

    TextFieldComponent(
        modifier = modifier
            .pointerInput(
                key1 = state.value
            ) {
                awaitEachGesture {
                    awaitFirstDown(
                        pass = PointerEventPass.Initial
                    )

                    val upEvent = waitForUpOrCancellation(
                        pass = PointerEventPass.Initial
                    )

                    if (upEvent != null) {
                        showDatePicker = true
                    }
                }
            }
            .semantics {
                onClick {
                    showDatePicker = true
                    true
                }
            },
        state = TextFieldState(
            initial = DatePickerDefaults
                .dateFormatter()
                .formatDate(
                    dateMillis = state.value?.timeInMillis,
                    locale = Locale.getDefault()
                )
                ?.trim(),
            transform = {
                it.trim()
            },
            validators = emptyList()
        ),
        placeholder = placeholder,
        helpText = helpText,
        successText = successText,
        enabled = enabled,
        readOnly = true,
        onValueChange = null
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            colors = DatePickerDefaults.colors(
                containerColor = LocalPalette.current.backgroundPalette.base
            ),
            confirmButton = {
                BlockButtonComponent(
                    modifier = Modifier,
                    blockButtonStyleType = BlockButtonStyleType.Primary,
                    blockButtonSizeType = BlockButtonSizeType.Small,
                    text = stringResource(
                        id = R.string.ok
                    ),
                    leadingIcon = null,
                    trailingIcon = null,
                    enabled = datePickerState.selectedDateMillis != null,
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = stringResource(
                            id = R.string.accessibility_select_date
                        )
                    ),
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            state.change(
                                value = Calendar
                                    .getInstance()
                                    .apply {
                                        timeInMillis = it
                                    }
                            )
                        }

                        showDatePicker = false
                    }
                )
            },
            dismissButton = {
                BlockButtonComponent(
                    modifier = Modifier,
                    blockButtonStyleType = BlockButtonStyleType.Secondary,
                    blockButtonSizeType = BlockButtonSizeType.Small,
                    text = stringResource(
                        id = R.string.cancel
                    ),
                    leadingIcon = null,
                    trailingIcon = null,
                    enabled = true,
                    accessibilityState = AccessibilityState.Click(
                        disabledReason = ""
                    ),
                    onClick = {
                        showDatePicker = false
                    }
                )
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = dateFieldStyle.backgroundColor,
                    titleContentColor = dateFieldStyle.contentColor,
                    headlineContentColor = dateFieldStyle.contentColor,
                    weekdayContentColor = dateFieldStyle.contentColor,
                    subheadContentColor = dateFieldStyle.contentColor,
                    navigationContentColor = dateFieldStyle.contentColor,
                    yearContentColor = dateFieldStyle.contentColor,
                    disabledYearContentColor = dateFieldStyle.disabledContentColor,
                    currentYearContentColor = dateFieldStyle.contentColor,
                    selectedYearContentColor = dateFieldStyle.contentColor,
                    disabledSelectedYearContentColor = dateFieldStyle.disabledContentColor,
                    selectedYearContainerColor = dateFieldStyle.backgroundColor,
                    disabledSelectedYearContainerColor = dateFieldStyle.disabledBackgroundColor,
                    dayContentColor = dateFieldStyle.contentColor,
                    disabledDayContentColor = dateFieldStyle.disabledContentColor,
                    selectedDayContentColor = dateFieldStyle.contentColor,
                    disabledSelectedDayContentColor = dateFieldStyle.disabledContentColor,
                    selectedDayContainerColor = dateFieldStyle.selectedDayContainerColor,
                    disabledSelectedDayContainerColor = dateFieldStyle.disabledBackgroundColor,
                    todayContentColor = dateFieldStyle.contentColor,
                    todayDateBorderColor = dateFieldStyle.todayDateBorderColor,
                    dividerColor = dateFieldStyle.dividerColor,
                )
            )
        }
    }
}

@Preview
@Composable
private fun DateFieldComponentLightPreview() {
    Theme {
        DateFieldComponent(
            modifier = Modifier
                .safeContentPadding(),
            state = DateFieldFieldState(
                initial = null,
                transform = {
                    it
                },
                validators = listOf(
                    Validators.Required(
                        message = "Required"
                    ),
                    Validators.MinDate(
                        limit = Calendar.getInstance().apply {
                            set(Calendar.YEAR, 2025)
                            set(Calendar.MONTH, Calendar.DECEMBER)
                            set(Calendar.DAY_OF_MONTH, 31)
                        },
                        message = {
                            "Min date"
                        }
                    ),
                    Validators.MaxDate(
                        limit = Calendar.getInstance().apply {
                            set(Calendar.YEAR, 2026)
                            set(Calendar.MONTH, Calendar.DECEMBER)
                            set(Calendar.DAY_OF_MONTH, 31)
                        },
                        message = {
                            "Max date"
                        }
                    )
                )
            ),
            placeholder = "Date",
            helpText = null,
            successText = null,
            enabled = true
        )
    }
}
