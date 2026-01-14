package com.core.presentation.ui.components.tab

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.core.presentation.ui.components.tab.model.Tab
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies

@Composable
fun <T> TabComponent(modifier: Modifier, tabs: List<Tab<T>>, selectedTab: Tab<T>, onTabSelected: (Tab<T>) -> Unit) {
    val tabSelected = remember(selectedTab) {
        val indexOfSelectedTab = try {
            val index = tabs.indexOf(selectedTab)
            index.takeIf { it >= 0 } ?: 0
        } catch (exception: Exception) {
            0
        }

        indexOfSelectedTab
    }

    val tabHeight = LocalDimensions.current.dp.dp52
    val tabIndicatorBorderRadius = LocalDimensions.current.dp.dp2
    val tabSelectedColor = LocalPalette.current.backgroundPalette.accent
    val tabSelectedContentColor = LocalPalette.current.contentPalette.alwaysLight
    val tabUnselectedContentColor = LocalPalette.current.contentPalette.accent

    Column(
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val indicatorHeight = remember {
                tabHeight
            }

            val indicatorWidth = remember {
                (maxWidth / tabs.size)
            }

            val roundedCornerShape = remember(tabSelected) {
                when (tabSelected) {
                    0 -> RoundedCornerShape(
                        topEnd = tabIndicatorBorderRadius,
                    )

                    tabs.size -> RoundedCornerShape(
                        topStart = tabIndicatorBorderRadius,
                    )

                    else -> RoundedCornerShape(
                        topStart = tabIndicatorBorderRadius,
                        topEnd = tabIndicatorBorderRadius,
                    )
                }
            }

            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        height = tabHeight,
                    ),
                selectedTabIndex = tabSelected,
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .zIndex(-1f)
                            .tabIndicatorOffset(currentTabPosition = it[tabSelected]),
                        width = indicatorWidth,
                        height = indicatorHeight,
                        color = tabSelectedColor,
                        shape = roundedCornerShape,
                    )
                },
                divider = {},
            ) {
                tabs.forEach { tab ->
                    Tab(
                        modifier = Modifier
                            .height(
                                height = tabHeight,
                            ),
                        text = {
                            Text(
                                text = tab.title.text,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = if (tab == selectedTab) {
                                    tabSelectedContentColor
                                } else {
                                    tabUnselectedContentColor
                                },
                                style = LocalTypographies.current.mid.l,
                            )
                        },
                        icon = tab.title.icon?.let {
                            {
                                Icon(
                                    painter = painterResource(id = it),
                                    tint = if (tab == selectedTab) {
                                        tabSelectedContentColor
                                    } else {
                                        tabUnselectedContentColor
                                    },
                                    contentDescription = tab.title.text,
                                )
                            }
                        },
                        selected = tab == selectedTab,
                        selectedContentColor = tabSelectedContentColor,
                        unselectedContentColor = tabUnselectedContentColor,
                        onClick = {
                            onTabSelected(tab)
                        },
                    )
                }
            }
        }

        with(tabs[tabSelected]) {
            content(data)
        }
    }
}
