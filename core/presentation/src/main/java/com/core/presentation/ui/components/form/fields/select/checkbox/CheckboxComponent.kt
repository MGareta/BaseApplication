package com.core.presentation.ui.components.form.fields.select.checkbox

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.ui.components.form.fields.select.checkbox.model.rememberCheckboxStyle
import com.core.presentation.ui.components.form.validators.Validators
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import com.core.presentation.ui.utils.AccessibilityLabel
import com.core.presentation.ui.utils.AccessibilityStateDescription
import com.core.presentation.ui.utils.thenOnNotNull

@Composable
fun CheckboxComponent(
    modifier: Modifier,
    state: CheckboxState,
    text: String,
    description: String,
    accessibilityLabel: AccessibilityLabel,
    accessibilityStateDescription: AccessibilityStateDescription,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }

    val checkboxStyle = rememberCheckboxStyle(
        isSelected = state.getData() ?: false,
        enabled = enabled,
        interactionSource = interactionSource
    )

    Row(
        modifier = modifier
            .background(
                color = checkboxStyle.background,
                shape = RoundedCornerShape(
                    size = LocalDimensions.current.dp.radius.radius4
                )
            )
            .thenOnNotNull(
                condition = checkboxStyle.borderStroke,
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
                role = Role.Checkbox,
                onValueChange = {
                    if (enabled) {
                        state.change(select = it)
                    }
                }
            )
            .semantics(
                mergeDescendants = true
            ) {
                val baseLabel = "$text. $description"
                this.contentDescription = if (!enabled) {
                    "$baseLabel. ${accessibilityLabel.disabled}"
                } else {
                    baseLabel
                }

                stateDescription = if (state.getData() == true) {
                    accessibilityStateDescription.selected
                } else {
                    accessibilityStateDescription.notSelected
                }

                if (!enabled) {
                    disabled()
                }
            },
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.space.space16
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier
                .semantics {
                    hideFromAccessibility()
                },
            checked = state.getData() ?: false,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = checkboxStyle.checkboxBackgroundColor,
                uncheckedColor = checkboxStyle.checkboxBorderColor,
                checkmarkColor = checkboxStyle.checkboxMarkColor,
                disabledCheckedColor = Color.Unspecified,
                disabledUncheckedColor = Color.Unspecified,
                disabledIndeterminateColor = Color.Unspecified
            ),
            onCheckedChange = null,
        )

        Column(
            modifier = Modifier
                .weight(
                    weight = 1F,
                    fill = false
                )
        ) {
            Text(
                modifier = Modifier
                    .semantics {
                        hideFromAccessibility()
                    },
                text = text,
                color = LocalPalette.current.contentPalette.primary,
                style = LocalTypographies.current.body.medium
            )

            Text(
                modifier = Modifier
                    .semantics {
                        hideFromAccessibility()
                    },
                text = description,
                color = LocalPalette.current.contentPalette.secondary,
                style = LocalTypographies.current.body.small
            )
        }
    }
}

@Preview
@Composable
private fun CheckboxComponentLightPreview() {
    Theme {
        CheckboxComponent(
            modifier = Modifier
                .safeContentPadding(),
            state = CheckboxState(
                default = true,
                validators = listOf(
                    Validators.Required("Prueba")
                )
            ),
            text = "This is a text",
            description = "This is a description",
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a text. \n This is a description",
                disabled = "Other mandatory"
            ),
            accessibilityStateDescription = AccessibilityStateDescription(
                selected = "On",
                notSelected = "Off"
            ),
            enabled = true
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun CheckboxComponentDarkPreview() {
    Theme {
        CheckboxComponent(
            modifier = Modifier
                .safeContentPadding(),
            state = CheckboxState(
                default = true,
                validators = listOf(
                    Validators.Required("Prueba")
                )
            ),
            text = "This is a text",
            description = "This is a description",
            accessibilityLabel = AccessibilityLabel(
                enabled = "This is a text. \n This is a description",
                disabled = "Other mandatory"
            ),
            accessibilityStateDescription = AccessibilityStateDescription(
                selected = "On",
                notSelected = "Off"
            ),
            enabled = false
        )
    }
}
