package com.core.presentation.ui.components.form.fields.select.toggle

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.form.fields.select.SelectFieldState
import com.core.presentation.ui.components.form.fields.select.toggle.model.ToggleStyleType
import com.core.presentation.ui.components.form.fields.select.toggle.model.rememberToggleStyle
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityState
import com.core.presentation.ui.utils.thenOnNotNull

@Composable
fun ToggleComponent(
    modifier: Modifier,
    state: SelectFieldState,
    toggleStyleType: ToggleStyleType,
    accessibilityStateSelect: AccessibilityState.Select,
    accessibilityStateClick: AccessibilityState.Click,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }

    val toggleStyle = rememberToggleStyle(
        isSelected = state.getData() ?: false,
        enabled = enabled,
        interactionSource = interactionSource
    )

    Row(
        modifier = modifier
            .background(
                color = toggleStyle.background,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius4
                )
            )
            .thenOnNotNull(
                condition = toggleStyle.borderStroke,
                block = {
                    border(
                        border = it,
                        shape = RoundedCornerShape(
                            size = LocalDimensions.current.dp.radius.radius4
                        )
                    )
                }
            )
            .toggleable(
                value = state.getData() ?: false,
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                role = Role.Switch,
                onValueChange = {
                    if (enabled) {
                        state.change(
                            value = it
                        )
                    }
                }
            )
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(
                    weight = 1F,
                    fill = false
                )
                .padding(
                    end = LocalDimensions.current.dp.space.space16
                )
        ) {
            when (toggleStyleType) {
                is ToggleStyleType.Text -> ToggleTextComponent(
                    toggleStyleText = toggleStyleType
                )
                is ToggleStyleType.Custom -> ToggleCustomComponent(
                    toggleStyleCustom = toggleStyleType
                )
            }
        }

        Switch(
            modifier = Modifier
                .semantics {
                    hideFromAccessibility()
                },
            checked = state.getData() ?: false,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = toggleStyle.switchThumbColor,
                checkedTrackColor = toggleStyle.switchTrackColor,
                checkedBorderColor = toggleStyle.switchBorderColor,
                checkedIconColor = toggleStyle.switchThumbColor,
                uncheckedThumbColor = toggleStyle.switchThumbColor,
                uncheckedTrackColor = toggleStyle.switchTrackColor,
                uncheckedBorderColor = toggleStyle.switchBorderColor,
                uncheckedIconColor = toggleStyle.switchThumbColor,
                disabledCheckedThumbColor = Color.Unspecified,
                disabledCheckedTrackColor = Color.Unspecified,
                disabledCheckedBorderColor = Color.Unspecified,
                disabledCheckedIconColor = Color.Unspecified,
                disabledUncheckedThumbColor = Color.Unspecified,
                disabledUncheckedTrackColor = Color.Unspecified,
                disabledUncheckedBorderColor = Color.Unspecified,
                disabledUncheckedIconColor = Color.Unspecified
            ),
        )
    }
}

@Composable
private fun ToggleTextComponent(
    toggleStyleText: ToggleStyleType.Text
) {
    Text(
        text = toggleStyleText.text,
        color = LocalPalette.current.contentPalette.primary,
        style = LocalTypographies.current.body.medium
    )

    Text(
        text = toggleStyleText.description,
        color = LocalPalette.current.contentPalette.secondary,
        style = LocalTypographies.current.body.small
    )
}

@Composable
private fun ToggleCustomComponent(
    toggleStyleCustom: ToggleStyleType.Custom
) {
    toggleStyleCustom.content()
}

@Preview
@Composable
private fun ToggleComponentLightPreview() {
    Theme {
        Column {
            ToggleComponent(
                modifier = Modifier
                    .safeContentPadding(),
                state = SelectFieldState(
                    initial = false,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                toggleStyleType = ToggleStyleType.Text(
                    text = "This is a text",
                    description = "This is a description",
                ),
                accessibilityStateClick = AccessibilityState.Click(
                    disabledReason = "Other mandatory"
                ),
                accessibilityStateSelect = AccessibilityState.Select(
                    selected = "On",
                    notSelected = "Off"
                ),
                enabled = false
            )

            ToggleComponent(
                modifier = Modifier
                    .safeContentPadding(),
                state = SelectFieldState(
                    initial = false,
                    validators = listOf(
                        Validators.Required("Prueba")
                    )
                ),
                toggleStyleType = ToggleStyleType.Custom(
                    content = {
                        Text(
                            text = "This is a custom text"
                        )
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ToggleComponentDarkPreview() {
    Theme {
        ToggleComponent(
            modifier = Modifier
                .safeContentPadding(),
            state = SelectFieldState(
                initial = false,
                validators = listOf(
                    Validators.Required("Prueba")
                )
            ),
            toggleStyleType = ToggleStyleType.Text(
                text = "This is a text",
                description = "This is a description",
            ),
            accessibilityStateClick = AccessibilityState.Click(
                disabledReason = "Other mandatory"
            ),
            accessibilityStateSelect = AccessibilityState.Select(
                selected = "On",
                notSelected = "Off"
            ),
            enabled = false
        )
    }
}
