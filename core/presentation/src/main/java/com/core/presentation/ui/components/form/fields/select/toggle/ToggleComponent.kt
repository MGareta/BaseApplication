package com.core.presentation.ui.components.form.fields.select.toggle

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.core.presentation.ui.theme.LocalPalette

@Composable
fun ToggleComponent(modifier: Modifier, state: ToggleState, enabled: Boolean = true) {
    Switch(
        modifier = modifier,
        checked = state.getData() ?: false,
        enabled = enabled,
        onCheckedChange = {
            state.change(select = it)
        },
        colors = SwitchColors(
            checkedThumbColor = LocalPalette.current.contentPalette.alwaysLight,
            checkedTrackColor = LocalPalette.current.backgroundPalette.accent,
            checkedBorderColor = LocalPalette.current.borderPalette.accent,
            checkedIconColor = LocalPalette.current.contentPalette.alwaysLight,
            uncheckedThumbColor = LocalPalette.current.contentPalette.accent,
            uncheckedTrackColor = LocalPalette.current.contentPalette.alwaysLight,
            uncheckedBorderColor = LocalPalette.current.borderPalette.accent,
            uncheckedIconColor = LocalPalette.current.contentPalette.accent,
            disabledCheckedThumbColor = LocalPalette.current.contentPalette.stateDisabled,
            disabledCheckedTrackColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledCheckedBorderColor = LocalPalette.current.borderPalette.stateDisabled,
            disabledCheckedIconColor = LocalPalette.current.contentPalette.stateDisabled,
            disabledUncheckedThumbColor = LocalPalette.current.contentPalette.stateDisabled,
            disabledUncheckedTrackColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledUncheckedBorderColor = LocalPalette.current.borderPalette.stateDisabled,
            disabledUncheckedIconColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
    )
}
