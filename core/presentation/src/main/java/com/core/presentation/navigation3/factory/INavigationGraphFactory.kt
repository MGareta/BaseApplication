package com.core.presentation.navigation3.factory

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import com.core.presentation.navigation3.model.destination.INavigationDestination

/**
 * A central registry and orchestrator for managing the application's navigation hierarchy.
 *
 * This interface defines the contract for aggregating modular navigation components
 * (graphs and entries) and registering them with the Navigation3 runtime. It facilitates
 * a decoupled architecture where feature modules can contribute their own navigation
 * logic without direct dependencies on other features.
 *
 * ### Core Responsibilities:
 * - **Aggregation:** Collects all [INavigationGraph]s and [INavigationEntry]s, typically
 *   via dependency injection (Dagger/Hilt) multibindings.
 * - **Bootstrapping:** Identifies the primary entry point of the application using
 *   the [navigationGraphLauncher].
 * - **Runtime Registration:** Configures the [EntryProviderScope] by mapping serializable
 *   destinations to their respective ViewModels and UI rendering logic.
 *
 * @see INavigationGraph
 * @see INavigationEntry
 * @see INavigationDestination
 */
interface INavigationGraphFactory {

    /**
     * A registry mapping each navigation graph to its collection of associated destinations.
     *
     * This property acts as the central data structure for the navigation factory. It uses
     * Dagger/Hilt multi-bindings to aggregate navigation graphs and their entries from
     * various feature modules into a single coordinated map.
     *
     * ### Structure:
     * - **Key ([INavigationGraph]):** Represents the graph container (e.g., a specific feature flow
     *   or a nested navigation structure).
     * - **Value (Set<[INavigationEntry]>):** A set of unique entries (screens or destinations)
     *   that belong specifically to that graph.
     *
     * ### Usage:
     * This map is typically processed during the [register] phase to build the navigation
     * hierarchy for the [androidx.navigation3.runtime.EntryProviderScope].
     *
     * @see INavigationGraph
     * @see INavigationEntry
     * @see navigationGraphLauncher
     */
    val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>

    /**
     * The designated entry point for the navigation system.
     *
     * This property identifies the specific [INavigationGraph] and its associated
     * [INavigationEntry] set that should be launched when the application or
     * specific flow starts.
     *
     * In a multi-graph environment, this acts as the "Home" or "Start" graph configuration,
     * ensuring the [navigationGraphLauncher] is prioritized during the initial
     * composition of the [EntryProviderScope].
     *
     * @return A single [Map.Entry] containing the starting graph and its valid destinations.
     */
    val navigationGraphLauncher: Map.Entry<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>>

    /**
     * Registers all navigation graphs and their respective destinations into the provided
     * [EntryProviderScope].
     *
     * This function is the execution bridge between the collected multi-bound data
     * ([navigationGraphsWithDestinations]) and the Navigation3 runtime. It iterates through
     * the registry and binds each [INavigationDestination] to its corresponding
     * [INavigationEntry] logic (UI and ViewModel).
     *
     * @param scope The [EntryProviderScope] provided by the Navigation3 runtime,
     * responsible for resolving and providing the composable content for each destination.
     */
    fun register(
        scope: EntryProviderScope<INavigationDestination>
    )
}
