package com.core.presentation.navigation.model.utils.navigator.bottomSheet

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.LocalOwnersProvider
import com.core.presentation.coroutines.zipWithPreviousCount
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetHost(
    navigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    val sheetBackStackState by remember {
        navigator.backStack.zipWithPreviousCount()
    }.collectAsStateWithLifecycle(Pair(0, emptyList()))

    val count by remember {
        derivedStateOf {
            maxOf(
                sheetBackStackState.first,
                sheetBackStackState.second.count(),
            )
        }
    }

    repeat(count) { i ->
        val backStackEntry = sheetBackStackState.second.getOrNull(i)
        BottomSheetHost(
            navigator = navigator,
            modifier = modifier,
            sheetMaxWidth = sheetMaxWidth,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets,
            saveableStateHolder = saveableStateHolder,
            targetBackStackEntry = backStackEntry,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetHost(
    navigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetMaxWidth: Dp,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    scrimColor: Color,
    dragHandle: @Composable (() -> Unit)?,
    contentWindowInsets: @Composable () -> WindowInsets,
    saveableStateHolder: SaveableStateHolder,
    targetBackStackEntry: NavBackStackEntry?,
) {
    val destination = targetBackStackEntry?.destination as? BottomSheetNavigator.Destination
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = destination?.skipPartiallyExpanded ?: true,
    )

    val backStackEntry by produceState<NavBackStackEntry?>(
        initialValue = null,
        key1 = targetBackStackEntry,
    ) {
        try {
            sheetState.hide()
        } catch (_: CancellationException) {
            // We catch but ignore possible cancellation exceptions as we don't want
            // them to bubble up and cancel the whole produceState coroutine
        } finally {
            value = targetBackStackEntry
        }
    }

    BottomSheetHost(
        navigator = navigator,
        modifier = modifier,
        sheetMaxWidth = sheetMaxWidth,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        sheetState = sheetState,
        contentWindowInsets = contentWindowInsets,
        saveableStateHolder = saveableStateHolder,
        backStackEntry = backStackEntry ?: return,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetHost(
    navigator: BottomSheetNavigator,
    modifier: Modifier = Modifier,
    sheetMaxWidth: Dp,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    scrimColor: Color,
    dragHandle: @Composable (() -> Unit)?,
    contentWindowInsets: @Composable () -> WindowInsets,
    sheetState: SheetState,
    saveableStateHolder: SaveableStateHolder,
    backStackEntry: NavBackStackEntry,
) {
    LaunchedEffect(backStackEntry) {
        sheetState.show()
    }

    backStackEntry.LocalOwnersProvider(saveableStateHolder) {
        val destination = backStackEntry.destination as BottomSheetNavigator.Destination

        ModalBottomSheet(
            onDismissRequest = { navigator.dismiss(backStackEntry) },
            modifier = modifier,
            sheetState = sheetState,
            sheetMaxWidth = sheetMaxWidth,
            shape = shape,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            scrimColor = scrimColor,
            dragHandle = dragHandle,
            contentWindowInsets = contentWindowInsets,
            properties = ModalBottomSheetProperties(securePolicy = destination.securePolicy),
        ) {
            LaunchedEffect(backStackEntry) {
                navigator.onTransitionComplete(backStackEntry)
            }

            DisposableEffect(backStackEntry) {
                onDispose {
                    navigator.onTransitionComplete(backStackEntry)
                }
            }

            destination.content(backStackEntry)
        }
    }
}
