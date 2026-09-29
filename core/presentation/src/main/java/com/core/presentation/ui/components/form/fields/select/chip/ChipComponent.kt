package com.core.presentation.ui.components.form.fields.select.chip

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.form.fields.select.SelectFieldState
import com.core.presentation.ui.components.form.fields.select.chip.model.ChipStyleType
import com.core.presentation.ui.components.form.fields.select.chip.model.rememberChipStyle
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState

@Composable
fun ChipComponent(
    modifier: Modifier,
    state: SelectFieldState,
    chipStyleType: ChipStyleType,
    accessibilityStateSelect: AccessibilityState.Select,
    accessibilityStateClick: AccessibilityState.Click,
    enabled: Boolean,
    onValueChange: ((Boolean) -> Unit)?= null
) {
    val interactionSource = remember { MutableInteractionSource() }

    val chipStyle = rememberChipStyle(
        isSelected = state.getData() ?: false,
        enabled = enabled,
        interactionSource = interactionSource
    )

    FilterChip(
        modifier = modifier
            .semantics(
                mergeDescendants = true
            ) {
                val state = accessibilityStateSelect.selected
                    .takeIf {
                        state.getData() == true
                    }
                    ?: accessibilityStateSelect.notSelected

                val label = accessibilityStateClick.disabledReason
                    .takeIf {
                        !enabled
                    }

                stateDescription = listOfNotNull(
                    state,
                    label
                ).joinToString(". ")

                if (!enabled) {
                    disabled()
                }
            },
        enabled = true,
        selected = state.getData() ?: false,
        interactionSource = interactionSource,
        label = {
            when (chipStyleType) {
                is ChipStyleType.Text -> Text(
                    text = chipStyleType.text
                )
                is ChipStyleType.Custom -> chipStyleType.content()
            }
        },
        colors = SelectableChipColors(
            containerColor = chipStyle.background,
            labelColor = chipStyle.content,
            leadingIconColor = chipStyle.content,
            trailingIconColor = chipStyle.content,
            disabledContainerColor = chipStyle.background,
            disabledLabelColor = chipStyle.content,
            disabledLeadingIconColor = chipStyle.content,
            disabledTrailingIconColor = chipStyle.content,
            selectedContainerColor = chipStyle.background,
            disabledSelectedContainerColor = chipStyle.background,
            selectedLabelColor = chipStyle.content,
            selectedLeadingIconColor = chipStyle.content,
            selectedTrailingIconColor = chipStyle.content,
        ),
        border = chipStyle.borderStroke,
        onClick = {
            if (enabled) {
                val value = state.getData() ?: false
                onValueChange?.invoke(!value) ?: state.change(
                    value = !value
                )
            }
        }
    )
}


@Preview
@Composable
private fun ChipComponentLightPreview() {
    Theme {
        Column(
            modifier = Modifier
                .safeContentPadding()
        ) {
            ChipComponent(
                modifier = Modifier,
                state = SelectFieldState(
                    initial = true,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                chipStyleType = ChipStyleType.Text(
                    text = "Text 1"
                ),
                accessibilityStateClick = AccessibilityState.Click(
                    disabledReason = "Other mandatory"
                ),
                accessibilityStateSelect = AccessibilityState.Select(
                    selected = "On",
                    notSelected = "Off"
                ),
                enabled = true
            )

            ChipComponent(
                modifier = Modifier,
                state = SelectFieldState(
                    initial = false,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                chipStyleType = ChipStyleType.Custom(
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = LocalDimensions.current.dp.space.space4
                            )
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = android.R.drawable.star_on
                                ),
                                contentDescription = null
                            )

                            Text(
                                text = "Text 2"
                            )
                        }

                    }
                ),
                accessibilityStateClick = AccessibilityState.Click(
                    disabledReason = "Other mandatory"
                ),
                accessibilityStateSelect = AccessibilityState.Select(
                    selected = "On",
                    notSelected = "Off"
                ),
                enabled = true
            )
        }
    }
}
