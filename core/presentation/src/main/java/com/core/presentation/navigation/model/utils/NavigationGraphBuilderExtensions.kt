@file:Suppress("UNCHECKED_CAST")

package com.core.presentation.navigation.model.utils

import android.annotation.SuppressLint
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.compose.DialogNavigator
import androidx.navigation.compose.DialogNavigatorDestinationBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.get
import com.core.presentation.base.screen.BaseFlow
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.destination.INavigationDestination
import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.entry.type.INavigationGraphBottomSheetEntry
import com.core.presentation.navigation.model.entry.type.INavigationGraphDialogEntry
import com.core.presentation.navigation.model.entry.type.INavigationGraphScreenEntry
import com.core.presentation.navigation.model.graph.INavigationGraph
import com.core.presentation.navigation.model.utils.navigator.bottomSheet.BottomSheetNavigator
import com.core.presentation.navigation.model.utils.navigator.bottomSheet.BottomSheetNavigatorDestinationBuilder
import com.core.presentation.ui.utils.ComposableLifecycle
import kotlin.reflect.KClass

@SuppressLint("RestrictedApi")
fun NavGraphBuilder.addGraphs(
    navController: StableHolder<NavHostController>,
    navigationGraphs: Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>,
) {
    navigationGraphs.forEach { (navigationGraph, destinationGraphEntries) ->
        navigation(
            startDestination = navigationGraph.startingDestination,
            route = navigationGraph::class,
            typeMap = emptyMap(),
            deepLinks = emptyList(),
            enterTransition = navigationGraph.enterTransition,
            exitTransition = navigationGraph.exitTransition,
            popEnterTransition = navigationGraph.popEnterTransition,
            popExitTransition = navigationGraph.popExitTransition,
            sizeTransform = null,
            builder = {
                destinationGraphEntries.forEach { destinationGraphEntry ->
                    when (destinationGraphEntry) {
                        is INavigationGraphScreenEntry<*, *> -> addComposableDestination(
                            navController = navController,
                            navigationGraph = navigationGraph,
                            navigationGraphEntry = destinationGraphEntry,
                        )
                        is INavigationGraphDialogEntry<*, *> -> addDialogDestination(
                            navController = navController,
                            navigationGraph = navigationGraph,
                            navigationGraphEntry = destinationGraphEntry,
                        )
                        is INavigationGraphBottomSheetEntry<*, *> -> addBottomSheetDestination(
                            navController = navController,
                            navigationGraph = navigationGraph,
                            navigationGraphEntry = destinationGraphEntry,
                        )
                    }
                }
            },
        )
    }
}

private fun NavGraphBuilder.addComposableDestination(
    navController: StableHolder<NavHostController>,
    navigationGraph: INavigationGraph,
    navigationGraphEntry: INavigationGraphScreenEntry<*, *>,
) {
    val viewModelClass = navigationGraphEntry.getViewModelClass()
    val destinationClass = navigationGraphEntry.getDestinationClass()

    destination(
        ComposeNavigatorDestinationBuilder(
            navigator = provider[ComposeNavigator::class],
            route = destinationClass,
            typeMap = navigationGraphEntry.params,
            content = { navBackStackEntry ->
                BaseFlow(
                    navHostController = navController.item,
                    viewModelClass = viewModelClass,
                    navigationGraph = navigationGraph,
                    currentNavBackStackEntry = navBackStackEntry,
                ) { viewModel ->
                    ComposableLifecycle(
                        onComposableStarted = {
                            (navigationGraphEntry as INavigationGraphScreenEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStarted(viewModel = viewModel)
                        },
                        onComposableStopped = {
                            (navigationGraphEntry as INavigationGraphScreenEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStopped(viewModel = viewModel)
                        },
                    )

                    (navigationGraphEntry as INavigationGraphScreenEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                        .Render(controller = navController, viewModel = viewModel)
                }
            },
        ).apply {
            this.enterTransition = navigationGraphEntry.enterTransition
            this.exitTransition = navigationGraphEntry.exitTransition
            this.popEnterTransition = navigationGraphEntry.popEnterTransition
            this.popExitTransition = navigationGraphEntry.popExitTransition
            this.sizeTransform = navigationGraphEntry.sizeTransform
        },
    )
}

private fun NavGraphBuilder.addDialogDestination(
    navController: StableHolder<NavHostController>,
    navigationGraph: INavigationGraph,
    navigationGraphEntry: INavigationGraphDialogEntry<*, *>,
) {
    val viewModelClass = navigationGraphEntry.getViewModelClass()
    val destinationClass = navigationGraphEntry.getDestinationClass()

    destination(
        DialogNavigatorDestinationBuilder(
            navigator = provider[DialogNavigator::class],
            route = destinationClass,
            typeMap = navigationGraphEntry.params,
            dialogProperties = navigationGraphEntry.dialogProperties,
            content = { navBackStackEntry ->
                BaseFlow(
                    navHostController = navController.item,
                    viewModelClass = viewModelClass,
                    navigationGraph = navigationGraph,
                    currentNavBackStackEntry = navBackStackEntry,
                ) { viewModel ->
                    ComposableLifecycle(
                        onComposableStarted = {
                            (navigationGraphEntry as INavigationGraphDialogEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStarted(viewModel = viewModel)
                        },
                        onComposableStopped = {
                            (navigationGraphEntry as INavigationGraphDialogEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStopped(viewModel = viewModel)
                        },
                    )

                    (navigationGraphEntry as INavigationGraphDialogEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                        .Render(controller = navController, viewModel = viewModel)
                }
            },
        ),
    )
}

private fun NavGraphBuilder.addBottomSheetDestination(
    navController: StableHolder<NavHostController>,
    navigationGraph: INavigationGraph,
    navigationGraphEntry: INavigationGraphBottomSheetEntry<*, *>,
) {
    val viewModelClass = navigationGraphEntry.getViewModelClass()
    val destinationClass = navigationGraphEntry.getDestinationClass()

    destination(
        BottomSheetNavigatorDestinationBuilder(
            navigator = provider[BottomSheetNavigator::class],
            route = destinationClass,
            typeMap = navigationGraphEntry.params,
            content = { navBackStackEntry ->
                BaseFlow(
                    navHostController = navController.item,
                    viewModelClass = viewModelClass,
                    navigationGraph = navigationGraph,
                    currentNavBackStackEntry = navBackStackEntry,
                ) { viewModel ->
                    ComposableLifecycle(
                        onComposableStarted = {
                            (navigationGraphEntry as INavigationGraphBottomSheetEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStarted(viewModel = viewModel)
                        },
                        onComposableStopped = {
                            (navigationGraphEntry as INavigationGraphBottomSheetEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                                .onComposableStopped(viewModel = viewModel)
                        },
                    )

                    (navigationGraphEntry as INavigationGraphBottomSheetEntry<INavigationDestination, BaseViewModel<*, *, *>>)
                        .Render(controller = navController, viewModel = viewModel)
                }
            },
        ).apply {
            this.securePolicy = navigationGraphEntry.secureFlagPolicy
            this.skipPartiallyExpanded = navigationGraphEntry.skipPartiallyExpanded
        },
    )
}

private fun INavigationGraphEntry<*, *>.getViewModelClass(): Class<BaseViewModel<*, *, *>> {
    val viewModelClass = Class.forName(
        this::class
            .supertypes[0]
            .arguments[1]
            .type
            .toString(),
    )

    if (viewModelClass.superclass == BaseViewModel::class.java) {
        return viewModelClass as Class<BaseViewModel<*, *, *>>
    }

    throw IllegalStateException(
        "ViewModel ${viewModelClass.simpleName} must be a ${BaseViewModel::class.java.simpleName}",
    )
}

private fun INavigationGraphEntry<*, *>.getDestinationClass(): KClass<INavigationDestination> {
    val destinationClass = (
        this::class
            .supertypes[0]
            .arguments[0]
            .type
            ?.classifier as? KClass<*>
        )
    val isNavigationDestination = destinationClass?.supertypes?.any {
        Class.forName(it.toString()) == INavigationDestination::class.java
    }

    if (isNavigationDestination == true) {
        return destinationClass as KClass<INavigationDestination>
    }

    throw IllegalStateException(
        "DestinationClass ${destinationClass?.simpleName} must be a ${INavigationDestination::class.java.simpleName}",
    )
}
