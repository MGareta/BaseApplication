package com.core.presentation.ui.components.bottomnavbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import com.core.presentation.navigation.model.graph.INavigationGraphBottomNavBar
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun BottomNavBarComponent(bottomNavGraphs: Set<INavigationGraphBottomNavBar>) {
    val fabSize = LocalDimensions.current.dp.dp48
    val fabGap = LocalDimensions.current.dp.dp4

    val shape = remember {
        if (bottomNavGraphs.any { it.isFab }) {
            BottomNavBarShape(
                fabSize = fabSize,
                fabGap = fabGap,
            )
        } else {
            RectangleShape
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        bottomNavGraphs
            .firstOrNull {
                it.isFab
            }?.let {
                IconButton(
                    modifier = Modifier
                        .size(
                            size = fabSize,
                        ).offset(
                            y = -(fabSize.div(other = 2) + fabGap),
                        ).align(Alignment.TopCenter),
                    colors = IconButtonDefaults.iconButtonColors().copy(
                        containerColor = LocalPalette.current.backgroundPalette.accent,
                        contentColor = LocalPalette.current.contentPalette.alwaysLight,
                    ),
                    onClick = {
                    },
                ) {
                    Icon(
                        modifier = Modifier
                            .size(
                                size = LocalDimensions.current.dp.dp24,
                            ),
                        painter = painterResource(
                            id = it.icon,
                        ),
                        contentDescription = null,
                    )
                }
            }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = LocalDimensions.current.dp.dp3,
                    shape = shape,
                ),
            shape = shape,
            color = LocalPalette.current.backgroundPalette.base,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                bottomNavGraphs
                    .sortedBy {
                        it.order
                    }.forEach {
                        NavigationBarItem(
                            modifier = Modifier
                                .navigationBarsPadding(),
                            selected = false,
                            enabled = !it.isFab,
                            onClick = {
                            },
                            icon = {
                                if (!it.isFab) {
                                    Icon(
                                        modifier = Modifier.size(
                                            size = LocalDimensions.current.dp.dp24,
                                        ),
                                        painter = painterResource(
                                            id = it.icon,
                                        ),
                                        tint = LocalPalette.current.contentPalette.high,
                                        contentDescription = null,
                                    )
                                }
                            },
                            label = {
                                if (!it.isFab) {
                                    Text(
                                        text = it.label,
                                        color = LocalPalette.current.contentPalette.high,
                                        style = LocalTypographies.current.mid.xsEmphasis,
                                    )
                                }
                            },
                        )
                    }
            }
        }
    }
}
