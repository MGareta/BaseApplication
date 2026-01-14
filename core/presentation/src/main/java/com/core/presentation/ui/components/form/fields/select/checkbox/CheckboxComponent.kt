package com.core.presentation.ui.components.form.fields.select.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun CheckboxComponent(modifier: Modifier, state: CheckboxState, label: String, enabled: Boolean = true) {
    Row(
        modifier = modifier
            .then(
                other = Modifier
                    .toggleable(
                        value = state.getData() ?: false,
                        enabled = enabled,
                        role = Role.Checkbox,
                        onValueChange = {
                            state.change(select = it)
                        },
                    ),
            ),
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.dp8,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = state.getData() ?: false,
            enabled = enabled,
            colors = CheckboxColors(
                checkedCheckmarkColor = LocalPalette.current.contentPalette.accent,
                uncheckedCheckmarkColor = Color.Transparent,
                checkedBoxColor = Color.Transparent,
                uncheckedBoxColor = Color.Transparent,
                disabledCheckedBoxColor = Color.Transparent,
                disabledUncheckedBoxColor = Color.Transparent,
                disabledIndeterminateBoxColor = Color.Unspecified,
                checkedBorderColor = LocalPalette.current.contentPalette.accent,
                uncheckedBorderColor = LocalPalette.current.contentPalette.high,
                disabledBorderColor = LocalPalette.current.contentPalette.stateDisabled,
                disabledUncheckedBorderColor = LocalPalette.current.contentPalette.stateDisabled,
                disabledIndeterminateBorderColor = LocalPalette.current.contentPalette.stateDisabled,
            ),
            onCheckedChange = null,
        )

        Text(
            text = label,
            color = when (enabled) {
                true -> if (state.validate()) {
                    LocalPalette.current.contentPalette.high
                } else {
                    LocalPalette.current.semanticContentPalette.danger
                }
                false -> LocalPalette.current.contentPalette.stateDisabled
            },
            style = LocalTypographies.current.mid.m,
        )
    }
}
