package com.core.presentation.ui.components.cta.block

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.core.presentation.ui.components.cta.block.model.BlockSizeCta
import com.core.presentation.ui.components.cta.block.model.BlockTypeCta
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Composable
fun BlockCta(
    blockTypeCta: BlockTypeCta,
    blockSizeCta: BlockSizeCta,
    text: String,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    when (blockTypeCta) {
        BlockTypeCta.Primary -> PrimaryBlockCta(
            modifier = blockSizeCta.modifier,
            contentPadding = blockSizeCta.contentPadding,
            enabled = enabled,
            onClick = onClick,
            text = text,
            textStyle = blockSizeCta.textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
        BlockTypeCta.Secondary -> SecondaryBlockCta(
            modifier = blockSizeCta.modifier,
            contentPadding = blockSizeCta.contentPadding,
            enabled = enabled,
            onClick = onClick,
            text = text,
            textStyle = blockSizeCta.textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
        BlockTypeCta.Tertiary -> TertiaryBlockCta(
            modifier = blockSizeCta.modifier,
            contentPadding = blockSizeCta.contentPadding,
            enabled = enabled,
            onClick = onClick,
            text = text,
            textStyle = blockSizeCta.textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
        )
    }
}

@Composable
private fun PrimaryBlockCta(
    modifier: Modifier,
    contentPadding: PaddingValues,
    text: String,
    textStyle: TextStyle,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = ButtonColors(
            containerColor = LocalPalette.current.backgroundPalette.accent,
            contentColor = LocalPalette.current.contentPalette.alwaysLight,
            disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        contentPadding = contentPadding,
        onClick = onClick,
    ) {
        leadingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier
                .weight(
                    weight = 1F,
                ),
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )

        trailingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun SecondaryBlockCta(
    modifier: Modifier,
    contentPadding: PaddingValues,
    text: String,
    textStyle: TextStyle,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = ButtonColors(
            containerColor = LocalPalette.current.backgroundPalette.alwaysLight,
            contentColor = LocalPalette.current.contentPalette.accent,
            disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        border = BorderStroke(
            width = LocalDimensions.current.dp.dp1,
            color = if (enabled) {
                LocalPalette.current.borderPalette.accent
            } else {
                LocalPalette.current.borderPalette.stateDisabled
            },
        ),
        contentPadding = contentPadding,
        onClick = onClick,
    ) {
        leadingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier
                .weight(
                    weight = 1F,
                ),
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )

        trailingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun TertiaryBlockCta(
    modifier: Modifier,
    contentPadding: PaddingValues,
    text: String,
    textStyle: TextStyle,
    @DrawableRes leadingIcon: Int?,
    @DrawableRes trailingIcon: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = ButtonColors(
            containerColor = Color.Transparent,
            contentColor = LocalPalette.current.contentPalette.accent,
            disabledContainerColor = LocalPalette.current.backgroundPalette.base,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        contentPadding = contentPadding,
        onClick = onClick,
    ) {
        leadingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }

        Text(
            modifier = Modifier
                .weight(
                    weight = 1F,
                ),
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = textStyle,
        )

        trailingIcon?.let {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(),
                painter = painterResource(
                    id = it,
                ),
                contentDescription = null,
            )
        }
    }
}
