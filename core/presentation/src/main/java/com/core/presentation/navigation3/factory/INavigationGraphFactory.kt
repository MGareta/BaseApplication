package com.core.presentation.navigation3.factory

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import com.core.presentation.navigation3.model.destination.INavigationDestination

interface INavigationGraphFactory {
    val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>
    val navigationGraphLauncher: Map.Entry<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>

    fun register(
        scope: EntryProviderScope<INavigationDestination>
    )
}
