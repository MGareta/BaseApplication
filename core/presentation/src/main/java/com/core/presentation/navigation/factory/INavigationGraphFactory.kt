package com.core.presentation.navigation.factory

import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.graph.INavigationGraph

interface INavigationGraphFactory {
    val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>

    fun getLauncherNavGraph(): INavigationGraph

    fun getBottomNavBarNavigationGraphs(): Map<INavigationGraph, Set<INavigationGraphEntry<*, *>>>

    fun isBottomNavBarDestination(destination: String): Boolean
}
