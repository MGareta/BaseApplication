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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.shadow.Shadow
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
import com.core.presentation.navigation3.model.graph.type.INavigationGraphBottomNavBar
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.BottomNavBarScrollConnection
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.LocalTypographies
import com.core.presentation.ui.utils.BorderIndication
import com.core.presentation.ui.utils.thenOnTrue

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
                Column(
                    modifier = Modifier
                        .weight(
                            weight = 1F
                        )
                        .thenOnTrue(
                            condition = it == selectedNavGraph,
                            block = {
                                Modifier
                                    .background(
                                        color = LocalPalette.current.backgroundPalette.accent,
                                        shape = CircleShape
                                    )
                                    .innerShadow(
                                        shape = CircleShape,
                                        shadow = Shadow(
                                            radius = LocalDimensions.current.dp.radius.radius8,
                                            color = LocalPalette.current.backgroundPalette.base
                                        )
                                    )
                            }
                        )
                        .clip(
                            shape = CircleShape
                        )
                        .selectable(
                            selected = it == selectedNavGraph,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = BorderIndication(
                                borderColor = LocalPalette.current.borderPalette.onAccent,
                                strokeWidth = LocalDimensions.current.dp.border.border8,
                                shape = CircleShape
                            ),
                            role = Role.Tab,
                            onClick = {
                                onTabSelected(it)
                            }
                        )
                        .padding(
                            vertical = if (isInline) {
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
                            color = LocalPalette.current.backgroundPalette.base,
                            shape = CircleShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = BorderIndication(
                                borderColor = LocalPalette.current.borderPalette.onAccent,
                                strokeWidth = LocalDimensions.current.dp.border.border8,
                                shape = CircleShape
                            ),
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
                    contentDescription = it.label
                )
            }

    }
}