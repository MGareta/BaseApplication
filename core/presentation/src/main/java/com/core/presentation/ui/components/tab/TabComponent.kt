package com.core.presentation.ui.components.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.core.presentation.ui.components.tab.model.TabStyleType
import com.core.presentation.ui.components.tab.model.rememberTabStyle
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme

@Composable
fun <TAB : Enum<TAB>> TabComponent(
    modifier: Modifier,
    tabs: Map<TAB, TabStyleType>,
    currentTab: TAB,
    content: @Composable (TAB) -> Unit
) {
    val tabsKeys = remember(
        key1 = tabs
    ) {
        tabs.keys.toList()
    }

    var selectedTab by remember {
        mutableIntStateOf(
            value = tabs.keys.indexOf(
                element = currentTab
            )
        )
    }

    Column(
        modifier = modifier
            .semantics {
                isTraversalGroup = true
            }
    ) {
        PrimaryTabRow(
            modifier = Modifier
                .fillMaxWidth(),
            selectedTabIndex = selectedTab,
            contentColor = LocalPalette.current.contentPalette.primary,
            containerColor = Color.Transparent,
            indicator = {
                if (selectedTab in tabsKeys.indices) {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            selectedTabIndex = selectedTab,
                            matchContentSize = false
                        ),
                        color = LocalPalette.current.contentPalette.accent,
                        width = Dp.Unspecified,
                    )
                }
            }
        ) {
            tabsKeys.forEachIndexed { index, tab ->
                val tabStyle = rememberTabStyle(
                    tabStyleType = tabs.getValue(
                        key = tab
                    )
                )

                Tab(
                    selected = index == selectedTab,
                    text = {
                        tabStyle.text?.let {
                            Text(
                                text = it,
                                color = if (index == selectedTab) {
                                    LocalPalette.current.contentPalette.accent
                                } else {
                                    LocalPalette.current.contentPalette.primary
                                },
                                style = LocalTypographies.current.body.medium
                            )
                        }
                    },
                    icon = {
                        tabStyle.icon?.let {
                            Icon(
                                modifier = Modifier
                                    .size(
                                        size = LocalDimensions.current.dp.target.target24
                                    ),
                                painter = painterResource(
                                    id = it
                                ),
                                tint = if (index == selectedTab) {
                                    LocalPalette.current.contentPalette.accent
                                } else {
                                    LocalPalette.current.contentPalette.primary
                                },
                                contentDescription = null
                            )
                        }
                    },
                    onClick = {
                        selectedTab = index
                    }
                )
            }
        }

        content(
            tabs.keys.elementAt(
                index = selectedTab
            )
        )
    }
}

@Preview
@Composable
private fun TabComponentPreview() {
    Theme {
        val tabs = remember {
            PreviewTabs.entries.associateWith {
                when (it) {
                    PreviewTabs.TAB_1 -> TabStyleType.IconText(
                        icon = android.R.drawable.star_on,
                        text = "Tab 1"
                    )

                    PreviewTabs.TAB_2 -> TabStyleType.Icon(
                        icon = android.R.drawable.star_on
                    )

                    PreviewTabs.TAB_3 -> TabStyleType.Text(
                        text = "Tab 3"
                    )
                }
            }
        }

        TabComponent(
            modifier = Modifier
                .safeContentPadding(),
            tabs = tabs,
            currentTab = tabs.keys.first(),
            content = {
                when (it) {
                    PreviewTabs.TAB_1 -> Text(
                        text = "Tab 1"
                    )
                    PreviewTabs.TAB_2 -> Text(
                        text = "Tab 2"
                    )
                    PreviewTabs.TAB_3 -> Text(
                        text = "Tab 3"
                    )
                }
            }
        )
    }
}


private enum class PreviewTabs {
    TAB_1,
    TAB_2,
    TAB_3
}
