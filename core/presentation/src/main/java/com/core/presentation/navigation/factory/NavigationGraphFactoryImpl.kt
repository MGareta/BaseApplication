package com.core.presentation.navigation.factory

import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.graph.INavigationGraph
import com.core.presentation.navigation.model.graph.INavigationGraphBottomNavBar
import com.core.presentation.navigation.model.graph.INavigationGraphLauncher

class NavigationGraphFactoryImpl(
    private val destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationGraphEntry<*, *>>>,
    private val graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>,
) : INavigationGraphFactory {
    @Suppress("UNCHECKED_CAST")
    override val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>
        get() = (destinations.keys.plus(graphs.keys).asSequence())
            .associate {
                graphs[it] to destinations[it]
            }.filter {
                it.key != null && it.value != null
            } as Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>

    override fun getLauncherNavGraph(): INavigationGraph {
        val launcherGraph = navigationGraphsWithDestinations.filterKeys {
            it is INavigationGraphLauncher
        }

        check(launcherGraph.isNotEmpty()) {
            "Launcher navigation graph is not set. Please implement ${INavigationGraphLauncher::class.simpleName}" +
                " in one ${INavigationGraph::class.simpleName}"
        }

        check(launcherGraph.size == 1) {
            "Launcher navigation graph must be 1 and only 1. Please remove duplicated uses of ${INavigationGraphLauncher::class.simpleName}"
        }

        return launcherGraph.keys.first()
    }

    override fun getBottomNavBarNavigationGraphs(): Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>> =
        navigationGraphsWithDestinations.filter {
            it.key is INavigationGraphBottomNavBar
        }

    override fun isBottomNavBarDestination(destination: String): Boolean =
        getBottomNavBarNavigationGraphs().values.any {
            it.any { entry ->
                entry::class.supertypes.any { supertype ->
                    supertype.arguments.any { classname ->
                        classname.toString() == destination
                    }
                }
            }
        }
}
