package com.core.presentation.ui.components.form.fields.input.otp.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.ShadowScope
import androidx.compose.ui.graphics.Color
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Stable
internal data class OtpFieldStyle(
    val backgroundColor: Color,
    val contentColor: Color,
    val borderStroke: BorderStroke,
    val shadow: ShadowScope?
)

@Composable
internal fun rememberOtpFieldStyle(
    enabled: Boolean,
    isError: Boolean,
    isFocused: Boolean,
    isDigitFocused: Boolean,
    isDigitHovered: Boolean,
): OtpFieldStyle {
    val palette = LocalPalette.current
    val dimensions = LocalDimensions.current

    return remember(
        enabled,
        isError,
        isFocused,
        isDigitFocused,
        isDigitHovered,
        palette
    ) {
        when {
            !enabled -> OtpFieldStyle(
                backgroundColor = palette.backgroundPalette.disabled,
                contentColor = palette.contentPalette.disabled,
                borderStroke = BorderStroke(
                    width = dimensions.dp.border.border1,
                    color = palette.borderPalette.disabled
                ),
                shadow = null
            )
            !isFocused && isError -> OtpFieldStyle(
                backgroundColor = palette.backgroundPalette.errorLow,
                contentColor = palette.contentPalette.error,
                borderStroke = BorderStroke(
                    width = dimensions.dp.border.border1,
                    color = palette.borderPalette.error
                ),
                shadow = null
            )
            isDigitHovered || isDigitFocused -> OtpFieldStyle(
                backgroundColor = Color.Transparent,
                contentColor = palette.contentPalette.primary,
                borderStroke = BorderStroke(
                    width = dimensions.dp.border.border1,
                    color = palette.borderPalette.active
                ),
                shadow = null
            )
            else -> OtpFieldStyle(
                backgroundColor = Color.Transparent,
                contentColor = palette.contentPalette.primary,
                borderStroke = BorderStroke(
                    width = dimensions.dp.border.border1,
                    color = palette.borderPalette.standard
                ),
                shadow = null
            )
        }
    }
}