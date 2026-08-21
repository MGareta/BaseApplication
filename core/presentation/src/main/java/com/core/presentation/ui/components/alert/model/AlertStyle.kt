package com.core.presentation.ui.components.alert.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalPalette

@Stable
sealed interface AlertStyleType {
    data object Warning : AlertStyleType
    data object Info : AlertStyleType
    data object Success : AlertStyleType
    data object Error : AlertStyleType
}

@Stable
internal data class AlertStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderColor: Color
)

@Composable
internal fun rememberAlertStyle(
    alertStyleType: AlertStyleType
): AlertStyle {
    val palette = LocalPalette.current

    return remember(
        key1 = alertStyleType,
        key2 = palette
    ) {
        when (alertStyleType) {
            AlertStyleType.Warning -> AlertStyle(
                backgroundColor = palette.backgroundPalette.warningLow,
                contentColor = palette.contentPalette.warning,
                borderColor = palette.borderPalette.error
            )
            AlertStyleType.Info -> AlertStyle(
                backgroundColor = palette.backgroundPalette.infoLow,
                contentColor = palette.contentPalette.info,
                borderColor = palette.borderPalette.error
            )
            AlertStyleType.Success -> AlertStyle(
                backgroundColor = palette.backgroundPalette.successLow,
                contentColor = palette.contentPalette.success,
                borderColor = palette.borderPalette.error
            )
            AlertStyleType.Error -> AlertStyle(
                backgroundColor = palette.backgroundPalette.errorLow,
                contentColor = palette.contentPalette.error,
                borderColor = palette.borderPalette.error
            )
        }
    }
}
