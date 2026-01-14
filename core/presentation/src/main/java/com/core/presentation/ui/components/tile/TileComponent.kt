package com.core.presentation.ui.components.tile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.core.presentation.ui.components.tile.model.TileUiState
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun TileComponent(
    modifier: Modifier,
    data: TileUiState,
    enabled: Boolean,
    horizontalAlignment: Alignment.Horizontal,
    customContent: (@Composable () -> Unit)?,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(
            size = LocalDimensions.current.dp.dp4,
        ),
        colors = CardColors(
            containerColor = LocalPalette.current.backgroundPalette.base,
            contentColor = Color.Unspecified,
            disabledContainerColor = LocalPalette.current.backgroundPalette.base,
            disabledContentColor = LocalPalette.current.contentPalette.stateDisabled,
        ),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = LocalDimensions.current.dp.dp16,
                ),
            verticalArrangement = Arrangement.spacedBy(
                space = LocalDimensions.current.dp.dp16,
            ),
            horizontalAlignment = horizontalAlignment,
        ) {
            data.icon?.let {
                Icon(
                    modifier = Modifier
                        .size(
                            size = LocalDimensions.current.dp.dp24,
                        ),
                    painter = painterResource(id = it),
                    tint = LocalPalette.current.contentPalette.accent,
                    contentDescription = null,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    space = LocalDimensions.current.dp.dp8,
                ),
            ) {
                data.label?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = it,
                        textAlign = when (horizontalAlignment) {
                            Alignment.Start -> TextAlign.Start
                            Alignment.CenterHorizontally -> TextAlign.Center
                            Alignment.End -> TextAlign.End
                            else -> TextAlign.Start
                        },
                        color = if (enabled) {
                            LocalPalette.current.contentPalette.high
                        } else {
                            LocalPalette.current.contentPalette.stateDisabled
                        },
                        style = LocalTypographies.current.mid.mEmphasis,
                    )
                }

                data.paragraph?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = it,
                        textAlign = when (horizontalAlignment) {
                            Alignment.Start -> TextAlign.Start
                            Alignment.CenterHorizontally -> TextAlign.Center
                            Alignment.End -> TextAlign.End
                            else -> TextAlign.Start
                        },
                        color = if (enabled) {
                            LocalPalette.current.contentPalette.mid
                        } else {
                            LocalPalette.current.contentPalette.stateDisabled
                        },
                        style = LocalTypographies.current.low.s,
                    )
                }

                data.extraParagraph?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = it,
                        textAlign = when (horizontalAlignment) {
                            Alignment.Start -> TextAlign.Start
                            Alignment.CenterHorizontally -> TextAlign.Center
                            Alignment.End -> TextAlign.End
                            else -> TextAlign.Start
                        },
                        color = if (enabled) {
                            LocalPalette.current.contentPalette.low
                        } else {
                            LocalPalette.current.contentPalette.stateDisabled
                        },
                        style = LocalTypographies.current.low.s,
                    )
                }

                customContent?.invoke()
            }
        }

        Spacer(
            modifier = Modifier
                .weight(weight = 1f),
        )
    }
}
