package com.core.presentation.navigation3.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.graph.type.INavigationGraphLauncher

class NavigationGraphFactoryImpl(
    private val destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationEntry<INavigationDestination, ViewModel>>>,
    private val graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>,
) : INavigationGraphFactory {

    override val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>> by lazy {
        destinations.keys
            .plus(
                elements = graphs.keys
            )
            .mapNotNull { key ->
                val graph = graphs[key]
                val entrySet = destinations[key]
                if (graph != null && entrySet != null) {
                    graph to entrySet
                } else {
                    null
                }
            }.toMap()
    }

    override val navigationGraphLauncher: Map.Entry<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>> by lazy {
        val launcherGraphs =  navigationGraphsWithDestinations.entries.filter {
            it.key is INavigationGraphLauncher
        }

        check(launcherGraphs.isNotEmpty()) {
            "Launcher navigation graph is not set. Please implement ${INavigationGraphLauncher::class.simpleName}" +
                    " in one ${INavigationGraph::class.simpleName}"
        }

        check(launcherGraphs.size == 1) {
            "Launcher navigation graph must be 1 and only 1. Please remove duplicated uses of ${INavigationGraphLauncher::class.simpleName}"
        }

        launcherGraphs.first()
    }

    override fun register(scope: EntryProviderScope<INavigationDestination>) {
        navigationGraphsWithDestinations.values.flatten().forEach { entry ->
            scope.addEntryProvider(
                clazz = entry.destination,
                clazzContentKey = {
                    it::class.qualifiedName.toString()
                },
                content = { destination ->
                    val viewModel = viewModel(
                        modelClass = entry.viewModel.java
                    )

                    entry.Render(
                        params = destination,
                        viewModel = viewModel
                    )
                }
            )
        }
    }
}
