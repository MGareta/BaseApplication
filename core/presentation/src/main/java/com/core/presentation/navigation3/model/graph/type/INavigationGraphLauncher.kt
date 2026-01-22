package com.core.presentation.navigation3.model.graph.type

import com.core.presentation.navigation3.model.graph.INavigationGraph

/**
 * A specialized type of navigation graph that serves as the primary entry point for the application.
 *
 * This abstract class marks a specific [INavigationGraph] as the "Launcher" or "Start" graph.
 * In a multi-module system where multiple graphs are registered via Hilt multibindings, the
 * navigation system uses this type to identify which flow should be presented to the
 * user upon application startup.
 *
 * ### Architectural Constraints:
 * - **Uniqueness:** Only one graph in the entire application should implement this interface
 *   to ensure a deterministic start destination.
 * - **Initialization:** The navigation manager scans for this type to populate the
 *   initial state of the backstack.
 *
 * @see INavigationGraph
 * @see com.core.presentation.navigation3.factory.INavigationGraphFactory
 */
abstract class INavigationGraphLauncher : INavigationGraph()