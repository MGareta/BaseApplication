package com.core.presentation.ui.components.cta.link

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Composable
fun LinkCta(
    modifier: Modifier,
    enabled: Boolean,
    text: String,
    textStyle: TextStyle,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable {
                if (enabled) {
                    onClick()
                }
            },
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.dp10,
        ),
    ) {
        leadingIcon?.let {
            Icon(
                modifier = Modifier,
                painter = painterResource(
                    id = it,
                ),
                tint = if (enabled) {
                    LocalPalette.current.contentPalette.accent
                } else {
                    LocalPalette.current.contentPalette.stateDisabled
                },
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier,
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textDecoration = TextDecoration.Underline,
            color = if (enabled) {
                LocalPalette.current.contentPalette.accent
            } else {
                LocalPalette.current.contentPalette.stateDisabled
            },
            style = textStyle,
        )

        trailingIcon?.let {
            Icon(
                modifier = Modifier,
                painter = painterResource(
                    id = it,
                ),
                tint = if (enabled) {
                    LocalPalette.current.contentPalette.accent
                } else {
                    LocalPalette.current.contentPalette.stateDisabled
                },
                contentDescription = null,
            )
        }
    }
}
