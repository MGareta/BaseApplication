package com.core.presentation.ui.components.toolbar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.util.fastFirst
import com.core.presentation.ui.components.toolbar.behavior.base.IToolbarScrollBehavior
import com.core.presentation.ui.components.toolbar.behavior.utils.enterAlwaysScrollBehavior
import com.core.presentation.ui.components.toolbar.model.ToolbarFamilyType
import com.core.presentation.ui.components.toolbar.state.utils.settleComponent
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.LocalPalette
import com.core.presentation.ui.theme.Theme
import kotlin.math.roundToInt

@Composable
fun ToolbarComponent(
    modifier: Modifier,
    scrollBehavior: IToolbarScrollBehavior,
    toolbarFamilyType: ToolbarFamilyType
) {
    when (toolbarFamilyType) {
        is ToolbarFamilyType.Animate -> ToolbarAnimateComponent(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            expandedContent = {
                toolbarFamilyType.expandedContent()
            },
            collapsedContent = {
                toolbarFamilyType.collapsedContent()
            }
        )
        is ToolbarFamilyType.Default -> ToolbarDefaultComponent(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            content = {
                toolbarFamilyType.content()
            }
        )
    }
}

@Composable
private fun ToolbarAnimateComponent(
    modifier: Modifier,
    scrollBehavior: IToolbarScrollBehavior,
    expandedContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit
) {
    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        val collapsedMeasuredSize = subcompose(
            slotId = "collapsedViewToMeasure"
        ) {
            collapsedContent()
        }[0].measure(constraints)
            .let {
                scrollBehavior.state.collapsedHeight = it.height.toFloat()

                DpSize(
                    width = it.width.toDp(),
                    height = it.height.toDp()
                )
            }

        val expandedMeasuredSize = subcompose(
            slotId = "expandedViewToMeasure"
        ) {
            expandedContent()
        }[0].measure(constraints)
            .let {
                scrollBehavior.state.expandedHeight = it.height.toFloat()

                DpSize(
                    width = it.width.toDp() + collapsedMeasuredSize.width,
                    height = it.height.toDp() + collapsedMeasuredSize.height
                )
            }

        val contentPlaceable = subcompose(
            slotId = "content"
        ) {
            val density = LocalDensity.current

            val expandedHeightPx = remember(expandedMeasuredSize) {
                with(
                    receiver = density
                ) {
                    expandedMeasuredSize.height
                        .toPx()
                        .coerceAtLeast(
                            minimumValue = 0F
                        )
                }
            }

            val collapsedHeightPx = remember(collapsedMeasuredSize) {
                with(
                    receiver = density
                ) {
                    collapsedMeasuredSize.height
                        .toPx()
                        .coerceAtLeast(
                            minimumValue = 0F
                        )
                }
            }

            val limit = remember(
                key1 = scrollBehavior.state.collapsedHeight,
                key2 = scrollBehavior.state.expandedHeight
            ) {
                1 - (scrollBehavior.state.collapsedHeight / scrollBehavior.state.expandedHeight)
            }

            val hideCollapsedSemantics = remember {
                derivedStateOf {
                    scrollBehavior.state.collapsedFraction < limit
                }
            }

            val hideExpandedSemantics = remember {
                derivedStateOf {
                    !hideCollapsedSemantics.value
                }
            }

            val scrollCollapsedComponentState = rememberScrollableState {
                scrollBehavior.state.heightOffset += it
                it
            }

            LaunchedEffect(
                key1 = scrollCollapsedComponentState.isScrollInProgress
            ) {
                if (!scrollCollapsedComponentState.isScrollInProgress) {
                    settleComponent(
                        state = scrollBehavior.state,
                        velocity = 0F,
                        flingAnimationSpec = scrollBehavior.flingAnimationSpec,
                        snapAnimationSpec = scrollBehavior.snapAnimationSpec
                    )
                }
            }

            SideEffect {
                if (scrollBehavior.state.heightOffsetLimit != collapsedHeightPx - expandedHeightPx) {
                    scrollBehavior.state.heightOffsetLimit = collapsedHeightPx - expandedHeightPx
                }
            }

            Box(
                modifier = modifier
                    .then(
                        other = if (!scrollBehavior.isPinned) {
                            Modifier.scrollable(
                                orientation = Orientation.Vertical,
                                state = scrollCollapsedComponentState,
                            )
                        } else {
                            Modifier
                        }
                            .semantics(
                                mergeDescendants = false
                            ) {
                                isTraversalGroup = true
                            }
                    ),
                propagateMinConstraints = true
            ) {
                Box {
                    Layout(
                        modifier = Modifier
                            .clipToBounds()
                            .heightIn(
                                max = collapsedMeasuredSize.height
                            ),
                        content = {
                            Box(
                                modifier = Modifier
                                    .layoutId(
                                        layoutId = "collapsedContent"
                                    )
                                    .then(
                                        other = if (hideCollapsedSemantics.value) {
                                            Modifier.clearAndSetSemantics {}
                                        } else {
                                            Modifier
                                        }
                                    )
                            ) {
                                collapsedContent()
                            }
                        },
                        measurePolicy = { measurables, constraints ->
                            val contentPlaceable = measurables
                                .fastFirst {
                                    it.layoutId == "collapsedContent"
                                }
                                .measure(
                                    constraints = constraints.copy(
                                        minWidth = 0
                                    )
                                )

                            layout(
                                width = constraints.maxWidth,
                                height = constraints.maxHeight.coerceAtLeast(
                                    minimumValue = 0
                                )
                            ) {
                                contentPlaceable.placeRelative(
                                    x = 0,
                                    y = 0
                                )
                            }
                        }
                    )

                    Layout(
                        modifier = Modifier
                            .windowInsetsPadding(
                                insets = WindowInsets(
                                    left = 0,
                                    top = 0,
                                    right = 0,
                                    bottom = 0
                                )
                            )
                            .clipToBounds()
                            .heightIn(
                                max = expandedMeasuredSize.height - collapsedMeasuredSize.height
                            )
                            .then(
                                other = if (hideExpandedSemantics.value) {
                                    Modifier.clearAndSetSemantics {}
                                } else {
                                    Modifier
                                }
                            ),
                        content = {
                            val verticalOffset = with(LocalDensity.current) {
                                scrollBehavior.state.heightOffset.toDp()
                            }

                            Box(
                                modifier = Modifier
                                    .layoutId(
                                        layoutId = "expandedContent"
                                    )
                                    .offset(
                                        y = verticalOffset
                                    )
                            ) {
                                expandedContent()
                            }
                        },
                        measurePolicy = { measurables, constraints ->
                            val contentPlaceable = measurables
                                .fastFirst {
                                    it.layoutId == "expandedContent"
                                }
                                .measure(
                                    constraints = constraints.copy(
                                        minWidth = 0
                                    )
                                )

                            val scrolledOffsetValue = scrollBehavior.state.heightOffset

                            val heightOffset = if (scrolledOffsetValue.isNaN()) {
                                0
                            } else {
                                scrolledOffsetValue.roundToInt()
                            }

                            val layoutHeight = constraints.maxHeight + heightOffset

                            layout(
                                width = constraints.maxWidth,
                                height = layoutHeight.coerceAtLeast(
                                    minimumValue = 0
                                )
                            ) {
                                contentPlaceable.placeRelative(
                                    x = 0,
                                    y = 0
                                )
                            }
                        }
                    )
                }
            }
        }.firstOrNull()?.measure(
            constraints = constraints
        )

        layout(
            width = contentPlaceable?.width ?: 0,
            height = contentPlaceable?.height  ?: 0
        ) {
            contentPlaceable?.placeRelative(
                x = 0,
                y = 0
            )
        }
    }
}

@Composable
private fun ToolbarDefaultComponent(
    modifier: Modifier,
    scrollBehavior: IToolbarScrollBehavior,
    content: @Composable () -> Unit
) {
    val shadowElevation by animateDpAsState(
        targetValue = if (scrollBehavior.state.overlappedFraction > 0.0F) {
            LocalDimensions.current.dp.border.border2
        } else {
            LocalDimensions.current.dp.border.border0
        }
    )

    SubcomposeLayout(
        modifier = modifier
            .shadow(
                elevation = shadowElevation
            )
    ) { constraints ->
        val measuredSize = subcompose(
            slotId = "viewToMeasure"
        ) {
            content()
        }[0].measure(constraints)
            .let {
                scrollBehavior.state.collapsedHeight = 0F
                scrollBehavior.state.expandedHeight = it.height.toFloat()

                DpSize(
                    width = it.width.toDp(),
                    height = it.height.toDp()
                )
            }

        val contentPlaceable = subcompose(
            slotId = "content"
        ) {
            val density = LocalDensity.current

            val expandedHeightPx = remember(measuredSize) {
                with(density) {
                    measuredSize.height
                        .toPx()
                        .coerceAtLeast(
                            minimumValue = 0F
                        )
                }
            }

            val scrollCollapsedComponentState = rememberScrollableState {
                scrollBehavior.state.heightOffset += it
                it
            }

            LaunchedEffect(scrollCollapsedComponentState.isScrollInProgress) {
                if (!scrollCollapsedComponentState.isScrollInProgress) {
                    settleComponent(
                        state = scrollBehavior.state,
                        velocity = 0F,
                        flingAnimationSpec = scrollBehavior.flingAnimationSpec,
                        snapAnimationSpec = scrollBehavior.snapAnimationSpec
                    )
                }
            }

            SideEffect {
                if (scrollBehavior.state.heightOffsetLimit != -expandedHeightPx) {
                    scrollBehavior.state.heightOffsetLimit = -expandedHeightPx
                }
            }

            Box(
                modifier = modifier
                    .then(
                        other = if (!scrollBehavior.isPinned) {
                            Modifier.scrollable(
                                orientation = Orientation.Vertical,
                                state = scrollCollapsedComponentState,
                            )
                        } else {
                            Modifier
                        }
                            .semantics(
                                mergeDescendants = false
                            ) {
                                isTraversalGroup = true
                            }
                    ),
                propagateMinConstraints = true
            ) {
                Layout(
                    modifier = Modifier
                        .clipToBounds()
                        .heightIn(
                            max = measuredSize.height
                        ),
                    content = {
                        val verticalOffset = with(LocalDensity.current) {
                            scrollBehavior.state.heightOffset.toDp()
                        }

                        Box(
                            modifier = Modifier
                                .layoutId(
                                    layoutId = "content"
                                )
                                .offset(
                                    y = verticalOffset
                                )
                        ) {
                            content()
                        }
                    },
                    measurePolicy = { measurables, constraints ->
                        val contentPlaceable = measurables
                            .fastFirst {
                                it.layoutId == "content"
                            }
                            .measure(
                                constraints = constraints.copy(
                                    minWidth = 0
                                )
                            )

                        val scrolledOffsetValue = scrollBehavior.state.heightOffset

                        val heightOffset = if (scrolledOffsetValue.isNaN()) {
                            0
                        } else {
                            scrolledOffsetValue.roundToInt()
                        }

                        val layoutHeight = constraints.maxHeight + heightOffset

                        layout(
                            width = constraints.maxWidth,
                            height = layoutHeight
                        ) {
                            contentPlaceable.placeRelative(
                                x = 0,
                                y = 0
                            )
                        }
                    }
                )
            }
        }.firstOrNull()?.measure(
            constraints = constraints
        )

        layout(
            width = contentPlaceable?.width ?: 0,
            height = contentPlaceable?.height ?: 0
        ) {
            contentPlaceable?.placeRelative(
                x = 0,
                y = 0
            )
        }
    }
}

@Preview
@Composable
private fun ToolbarAnimatedComponentPreview() {
    Theme {
        val scrollBehavior = enterAlwaysScrollBehavior()
//        val scrollBehavior = exitUntilCollapsedScrollBehavior()

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ToolbarComponent(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                toolbarFamilyType = ToolbarFamilyType.Animate(
                    expandedContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = LocalPalette.current.backgroundPalette.accentLow
                                )
                        ) {
                            Text(
                                text = "Expanded Component 1"
                            )
                            Text(
                                text = "Expanded Component 2"
                            )
                            Text(
                                text = "Expanded Component 3"
                            )
                            Text(
                                text = "Expanded Component 4"
                            )
                            Text(
                                text = "Expanded Component 5"
                            )
                            Text(
                                text = "Expanded Component 6"
                            )
                            Text(
                                text = "Expanded Component 7"
                            )
                            Text(
                                text = "Expanded Component 8"
                            )
                        }
                    },
                    collapsedContent = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.Red
                                ),
                            text = "Collapsed Component"
                        )
                    }
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(
                        weight = 1F
                    )
                    .nestedScroll(
                        connection = scrollBehavior.nestedScrollConnection
                    )
            ) {
                items(
                    count = 50
                ) {
                    Text(
                        text = "Item $it"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ToolbarDefaultComponentPreview() {
    Theme {
        val scrollBehavior = enterAlwaysScrollBehavior()
//        val scrollBehavior = exitUntilCollapsedScrollBehavior()
//        val scrollBehavior = pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    insets = WindowInsets.statusBars
                ),
            topBar = {
                ToolbarComponent(
                    modifier = Modifier,
                    scrollBehavior = scrollBehavior,
                    toolbarFamilyType = ToolbarFamilyType.Default(
                        content = {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = LocalPalette.current.backgroundPalette.accentLow
                                    )
                                    .padding(
                                        all = LocalDimensions.current.dp.space.space16
                                    ),
                                text = "Collpasing Component"
                            )
                        }
                    )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        paddingValues = it
                    )
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(
                            weight = 1F
                        )
                        .nestedScroll(
                            connection = scrollBehavior.nestedScrollConnection
                        )
                ) {
                    items(
                        count = 100
                    ) {
                        Text(
                            text = "Item $it"
                        )
                    }
                }
            }
        }
    }
}
