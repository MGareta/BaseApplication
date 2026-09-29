package com.core.presentation.ui.components.form.fields.multiselect.chip

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.R
import com.core.presentation.ui.components.button.block.BlockButtonComponent
import com.core.presentation.ui.components.button.block.model.BlockButtonSizeType
import com.core.presentation.ui.components.button.block.model.BlockButtonStyleType
import com.core.presentation.ui.components.form.fields.multiselect.MultiSelectFieldState
import com.core.presentation.ui.components.form.fields.select.SelectFieldState
import com.core.presentation.ui.components.form.fields.select.chip.ChipComponent
import com.core.presentation.ui.components.form.fields.select.chip.model.ChipStyleType
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.components.list.NonLazyGridComponent
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun MultiChipComponent(
    modifier: Modifier,
    state: MultiSelectFieldState<SelectFieldState>,
    items: Map<SelectFieldState, ChipStyleType>,
    accessibilityStateSelect: AccessibilityState.Select,
    accessibilityStateClick: AccessibilityState.Click,
    columnCount: Int,
    enabled: Boolean
) {
    val context = LocalContext.current

    val selectableItems = remember(
        key1 = items
    ) {
        items.toList()
    }

    val limit = remember(
        key1 = state.validators
    ) {
        state.validators
            .filterIsInstance<Validators.MaxSize<MutableList<SelectFieldState>>>()
            .firstOrNull()
            ?.limit
            ?: Int.MAX_VALUE
    }

    val accessibilityStateDescription = remember(
        key1 = state.value?.size,
        key2 = limit
    ) {
        @SuppressLint("LocalContextGetResourceValueCall")
        when (limit) {
            Int.MAX_VALUE -> context.getString(
                R.string.accessibility_multiselect_no_limit,
                state.value?.size
            )
            1 -> if ((state.value?.size ?: 0) > 0) {
                context.getString(
                    R.string.accessibility_multiselect_single_selected
                )
            } else {
                context.getString(
                    R.string.accessibility_multiselect_single_unselected
                )
            }
            else -> context.getString(
                R.string.accessibility_multiselect_limit_status,
                state.value?.size,
                limit
            )
        }
    }

    NonLazyGridComponent(
        modifier = modifier
            .semantics {
                contentDescription = accessibilityStateDescription
            },
        columns = columnCount,
        itemCount = selectableItems.size,
        verticalSpacing = LocalDimensions.current.dp.space.space8,
        horizontalSpacing = LocalDimensions.current.dp.space.space8
    ) {
        val (selectableState, selectableStyle) = selectableItems[it]

        val isDisabledByLimit = selectableState.value == false
                && (state.value?.size ?: 0) >= limit
                && limit != 1

        ChipComponent(
            modifier = Modifier,
            state = selectableState,
            chipStyleType = selectableStyle,
            accessibilityStateSelect = accessibilityStateSelect,
            accessibilityStateClick = if (isDisabledByLimit) {
                accessibilityStateClick.copy(
                    disabledReason = stringResource(
                        id = R.string.accessibility_multiselect_limit_reached,
                        limit
                    )
                )
            } else {
                accessibilityStateClick
            },
            enabled = enabled && !isDisabledByLimit,
            onValueChange = { result ->
                when {
                    limit == 1 -> {
                        state.value?.forEach {
                            it.change(
                                value = false
                            )
                        }

                        selectableState.change(
                            value = result
                        ).also {
                            state.change(
                                value = mutableListOf(selectableState)
                                    .takeIf {
                                        result
                                    } ?: mutableListOf()
                            )
                        }
                    }

                    !result || (state.value?.size ?: 0) < limit -> {
                        selectableState.change(
                            value = result
                        ).also {
                            state.change(
                                value = selectableState
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun MultiChipComponentLightPreview() {
    val state = remember {
        MultiSelectFieldState<SelectFieldState>(
            initial = emptyList(),
            validators = listOf(
                Validators.Required("Prueba"),
                Validators.MaxSize(
                    limit = 2,
                    message = {
                        "Only one checkbox is allowed"
                    }
                )
            )
        )
    }

    val items = remember {
        buildMap {
            this[SelectFieldState(
                initial = false,
                validators = emptyList()
            )] = ChipStyleType.Text(
                text = "Text 1"
            )

            this[SelectFieldState(
                initial = false,
                validators = emptyList()
            )] = ChipStyleType.Text(
                text = "Text 2"
            )

            this[SelectFieldState(
                initial = false,
                validators = emptyList()
            )] = ChipStyleType.Text(
                text = "Text 3"
            )
        }
    }

    Theme {
        Column {
            MultiChipComponent(
                modifier = Modifier
                    .safeContentPadding(),
                state = state,
                items = items,
                accessibilityStateSelect = AccessibilityState.Select(
                    selected = "On",
                    notSelected = "Off"
                ),
                accessibilityStateClick = AccessibilityState.Click(
                    disabledReason = "Other mandatory"
                ),
                columnCount = 2,
                enabled = true
            )

            BlockButtonComponent(
                modifier = Modifier
                    .safeContentPadding(),
                blockButtonStyleType = BlockButtonStyleType.Tertiary,
                blockButtonSizeType = BlockButtonSizeType.Small,
                text = "Test",
                leadingIcon = android.R.drawable.star_on,
                trailingIcon = android.R.drawable.star_on,
                enabled = state.validate(),
                accessibilityState = AccessibilityState.Click(
                    disabledReason = "The test is not completed"
                ),
                onClick = {
                    val values = state.getData()?.map {
                        it.getData()
                    }
                }
            )
        }
    }
}
