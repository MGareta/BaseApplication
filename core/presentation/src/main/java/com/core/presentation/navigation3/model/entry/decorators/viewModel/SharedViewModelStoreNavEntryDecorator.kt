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

@Composable
fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(
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

class SharedViewModelStoreNavEntryDecorator<T : Any>(
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
