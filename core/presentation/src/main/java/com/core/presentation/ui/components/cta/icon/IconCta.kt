package com.core.presentation.ui.components.cta.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.core.presentation.ui.components.cta.icon.model.IconSizeCta
import com.core.presentation.ui.components.cta.icon.model.IconTypeCta
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette

@Composable
fun IconCta(
    iconTypeCta: IconTypeCta,
    iconSizeCta: IconSizeCta,
    @DrawableRes icon: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    when (iconTypeCta) {
        IconTypeCta.Primary -> PrimaryIconCta(
            modifier = iconSizeCta.modifier,
            icon = icon,
            iconSize = iconSizeCta.iconSize,
            enabled = enabled,
            onClick = onClick,
        )
        IconTypeCta.Secondary -> SecondaryIconCta(
            modifier = iconSizeCta.modifier,
            icon = icon,
            iconSize = iconSizeCta.iconSize,
            enabled = enabled,
            onClick = onClick,
        )
        IconTypeCta.Tertiary -> TertiaryIconCta(
            modifier = iconSizeCta.modifier,
            icon = icon,
            iconSize = iconSizeCta.iconSize,
            enabled = enabled,
            onClick = onClick,
        )
    }
}

@Composable
private fun PrimaryIconCta(
    modifier: Modifier,
    @DrawableRes icon: Int,
    iconSize: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = IconButtonColors(
            containerColor = LocalPalette.current.backgroundPalette.accent,
            contentColor = LocalPalette.current.contentPalette.alwaysLight,
            disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .size(
                    size = iconSize,
                ),
            painter = painterResource(
                id = icon,
            ),
            contentDescription = null,
        )
    }
}

@Composable
private fun SecondaryIconCta(
    modifier: Modifier,
    iconSize: Dp,
    @DrawableRes icon: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedIconButton(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = IconButtonColors(
            containerColor = LocalPalette.current.backgroundPalette.alwaysLight,
            contentColor = LocalPalette.current.contentPalette.accent,
            disabledContainerColor = LocalPalette.current.backgroundPalette.stateDisabled,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .size(
                    size = iconSize,
                ),
            painter = painterResource(
                id = icon,
            ),
            contentDescription = null,
        )
    }
}

@Composable
private fun TertiaryIconCta(
    modifier: Modifier,
    iconSize: Dp,
    @DrawableRes icon: Int,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = LocalPalette.current.contentPalette.accent,
            disabledContainerColor = LocalPalette.current.backgroundPalette.base,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier
                .size(
                    size = iconSize,
                ),
            painter = painterResource(
                id = icon,
            ),
            contentDescription = null,
        )
    }
}
