package com.core.presentation.ui.components.bottomnavbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.core.presentation.navigation3.model.graph.type.INavigationGraphBottomNavBar
import com.core.presentation.ui.components.bottomnavbar.model.rememberBottomNavBarStyle
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.BottomNavBarScrollConnection
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.utils.rememberBottomNavBarScrollConnection
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.theme.Theme
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@Composable
fun BottomNavBarComponent(
    modifier: Modifier,
    bottomNavGraphs: Set<INavigationGraphBottomNavBar>,
    selectedNavGraph: INavigationGraphBottomNavBar?,
    scrollConnection: BottomNavBarScrollConnection,
    onTabSelected: (INavigationGraphBottomNavBar) -> Unit,
) {
    val density = LocalDensity.current

    val navigationGraphs = remember {
        bottomNavGraphs
            .sortedBy {
                it.order
            }
    }

    AnimatedContent(
        modifier = modifier
            .onSizeChanged { size ->
                scrollConnection.bottomBarHeight = with(
                    receiver = density
                ) { 
                    size.height.toDp() 
                }
            },
        targetState = scrollConnection.isInline,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
    ) { isInline ->
        Row(
            modifier = Modifier
                .height(
                    intrinsicSize = IntrinsicSize.Min
                )
                .semantics {
                    isTraversalGroup = true

                    collectionInfo = CollectionInfo(
                        rowCount = 1,
                        columnCount = navigationGraphs.size
                    )
                },
            horizontalArrangement = Arrangement.spacedBy(
                space = LocalDimensions.current.dp.space.space16
            )
        ) {
            BottomNavBarTabsComponent(
                modifier = Modifier
                    .weight(
                        weight = 1F
                    )
                    .shadow(
                        elevation = LocalDimensions.current.dp.space.space2,
                        shape = CircleShape
                    )
                    .background(
                        color = LocalPalette.current.backgroundPalette.base,
                        shape = CircleShape
                    )
                    .border(
                        width = LocalDimensions.current.dp.border.border2,
                        color = LocalPalette.current.borderPalette.subtle,
                        shape = CircleShape
                    ),
                bottomNavGraphs = navigationGraphs.filter { !it.isFab },
                selectedNavGraph = selectedNavGraph,
                isInline = isInline,
                onTabSelected = { tab ->
                    scrollConnection.expand().also {
                        onTabSelected(tab)
                    }
                }
            )

            if (navigationGraphs.filter { !it.isFab }.size <= 1) {
                Spacer(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        )
                )
            }

            BottomNavBarFabsComponent(
                modifier = Modifier,
                bottomNavGraphs = navigationGraphs.filter { it.isFab },
                isInline = isInline,
                onTabSelected = { tab ->
                    scrollConnection.expand().also {
                        onTabSelected(tab)
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomNavBarTabsComponent(
    modifier: Modifier,
    bottomNavGraphs: List<INavigationGraphBottomNavBar>,
    selectedNavGraph: INavigationGraphBottomNavBar?,
    isInline: Boolean,
    onTabSelected: (INavigationGraphBottomNavBar) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavGraphs
            .forEach {
                val bottomNavBarStyle = rememberBottomNavBarStyle(
                    isSelected = it == selectedNavGraph
                )

                Column(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        )
                        .padding(
                            all = LocalDimensions.current.dp.space.space4
                        )
                        .background(
                            color = bottomNavBarStyle.backgroundColor,
                            shape = CircleShape
                        )
                        .clip(
                            shape = CircleShape
                        )
                        .selectable(
                            selected = it == selectedNavGraph,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = bottomNavBarStyle.indication,
                            role = Role.Tab,
                            onClick = {
                                onTabSelected(it)
                            }
                        )
                        .padding(
                            vertical = if (isInline) {
                                LocalDimensions.current.dp.space.space2
                            } else {
                                LocalDimensions.current.dp.space.space4
                            }
                        )
                        .semantics {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = 0,
                                rowSpan = 1,
                                columnIndex = it.order,
                                columnSpan = 1
                            )

                            contentDescription = it.label
                        },
                    verticalArrangement = Arrangement.spacedBy(
                        space = LocalDimensions.current.dp.space.space2
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = null
                    )

                    if (!isInline) {
                        Text(
                            modifier = Modifier
                                .semantics {
                                    hideFromAccessibility()
                                },
                            text = it.label,
                            color = bottomNavBarStyle.contentColor,
                            style = LocalTypographies.current.body.small
                        )
                    }
                }
            }
    }
}

@Composable
private fun BottomNavBarFabsComponent(
    modifier: Modifier,
    bottomNavGraphs: List<INavigationGraphBottomNavBar>,
    isInline: Boolean,
    onTabSelected: (INavigationGraphBottomNavBar) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalDimensions.current.dp.space.space8
        )
    ) {
        bottomNavGraphs
            .forEach {
                val bottomNavBarStyle = rememberBottomNavBarStyle(
                    isSelected = false
                )

                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(
                            ratio = 1F
                        )
                        .shadow(
                            elevation = LocalDimensions.current.dp.space.space2,
                            shape = CircleShape
                        )
                        .background(
                            color = bottomNavBarStyle.backgroundColor,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = bottomNavBarStyle.indication,
                            role = Role.Button,
                            onClick = {
                                onTabSelected(it)
                            }
                        )
                        .align(
                            alignment = Alignment.CenterVertically
                        )
                        .padding(
                            all = if (isInline) {
                                LocalDimensions.current.dp.space.space4
                            } else {
                                LocalDimensions.current.dp.space.space8
                            }
                        )
                        .semantics {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = 0,
                                rowSpan = 1,
                                columnIndex = it.order,
                                columnSpan = 1
                            )
                        },
                    painter = painterResource(
                        id = it.icon
                    ),
                    tint = bottomNavBarStyle.contentColor,
                    contentDescription = it.label
                )
            }

    }
}

@Preview
@Composable
private fun BottomNavBarComponentPreview() {
    Theme {
        val navGraphs = remember {
            setOf(
                object : INavigationGraphBottomNavBar {
                    override val order: Int
                        get() = 0
                    override val icon: Int
                        get() = android.R.drawable.star_on
                    override val label: String
                        get() = "Test"
                    override val notificationCount: StateFlow<Int>
                        get() = flowOf(0) as StateFlow<Int>
                    override val isFab: Boolean
                        get() = false
                },
                object : INavigationGraphBottomNavBar {
                    override val order: Int
                        get() = 0
                    override val icon: Int
                        get() = android.R.drawable.star_on
                    override val label: String
                        get() = "Test"
                    override val notificationCount: StateFlow<Int>
                        get() = flowOf(0) as StateFlow<Int>
                    override val isFab: Boolean
                        get() = false
                },
                object : INavigationGraphBottomNavBar {
                    override val order: Int
                        get() = 1
                    override val icon: Int
                        get() = android.R.drawable.star_on
                    override val label: String
                        get() = "Test"
                    override val notificationCount: StateFlow<Int>
                        get() = flowOf(0) as StateFlow<Int>
                    override val isFab: Boolean
                        get() = true
                }
            )
        }

        var selectedNavGraph by remember {
            mutableStateOf(
                value = navGraphs.first()
            )
        }

        BottomNavBarComponent(
            modifier = Modifier,
            bottomNavGraphs = navGraphs,
            selectedNavGraph = selectedNavGraph,
            scrollConnection = rememberBottomNavBarScrollConnection(),
            onTabSelected = {
                selectedNavGraph = it
            }
        )
    }
}
