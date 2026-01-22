package com.core.presentation.navigation3.model.entry.decorators.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntryDecorator
import com.core.presentation.navigation3.listener.INavigationGraphListener
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph

/**
 * Creates and remembers a [SharedViewModelStoreNavEntryDecorator] instance within the composition.
 *
 * This helper function leverages the [remember] block to ensure that the decorator is not
 * re-instantiated across recompositions unless the underlying [graphs] registry changes.
 *
 * @param T The type of the navigation key/destination.
 * @param navigationGraphManager The manager responsible for providing graph-scoped [androidx.lifecycle.ViewModelStore]s.
 * @param graphs A mapping of [com.core.presentation.navigation3.model.graph.INavigationGraph]s
 * to their respective entries, used to resolve which graph a destination belongs to.
 * @return A [SharedViewModelStoreNavEntryDecorator] configured to provide shared ViewModel scopes.
 */
@Composable
internal fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(
    navigationGraphManager: INavigationGraphManager,
    graphs: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>
): SharedViewModelStoreNavEntryDecorator<T> {
    return remember(
        key1 = graphs
    ) {
        SharedViewModelStoreNavEntryDecorator(
            navigationGraphManager = navigationGraphManager,
            graphs = graphs
        )
    }
}

/**
 * A [NavEntryDecorator] that enables ViewModel sharing across different screens
 * within the same navigation graph.
 *
 * This decorator intercepts the rendering process of a navigation entry and determines
 * if the destination belongs to a specific [INavigationGraph]. If a parent graph is
 * identified, it provides a graph-scoped [androidx.lifecycle.ViewModelStore] via
 * [LocalViewModelStoreOwner].
 *
 * ### Responsibilities:
 * - **Graph Resolution:** Scans the [graphs] registry to find which graph contains the
 *   destination matching the current [androidx.navigation3.runtime.NavEntry.contentKey].
 * - **Scope Injection:** Uses [CompositionLocalProvider] to override the default
 *   [LocalViewModelStoreOwner] with a [NavigationGraphViewModelStoreOwner], ensuring
 *   ViewModels are shared among all siblings in that graph.
 * - **Lifecycle Synchronization:** Forwards [onPop] events to the [navigationGraphManager]
 *   to trigger automated cleanup of graph-scoped resources.
 *
 * @param T The type of the navigation key/destination.
 * @param navigationGraphManager The manager that provides and maintains graph-scoped stores.
 * @param graphs The registry containing the mapping of graphs to their respective entries.
 */
internal class SharedViewModelStoreNavEntryDecorator<T : Any>(
    navigationGraphManager: INavigationGraphManager,
    graphs: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>
) : NavEntryDecorator<T>(
    onPop = {
        (navigationGraphManager as INavigationGraphListener).onPop()
    },
    decorate = { entry ->
        val parentGraph = graphs.entries.find { (_, entries) ->
            entries.any {
                it.destination.qualifiedName == entry.contentKey
            }
        }?.key

        if (parentGraph == null) {
            entry.Content()
        } else {
            val store = navigationGraphManager.getViewModelStore(
                graph = parentGraph
            )

            CompositionLocalProvider(
                value = LocalViewModelStoreOwner provides NavigationGraphViewModelStoreOwner(store = store)
            ) {
                entry.Content()
            }
        }
    }
)
