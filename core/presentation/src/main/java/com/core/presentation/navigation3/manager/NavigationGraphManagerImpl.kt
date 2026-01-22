package com.core.presentation.navigation3.manager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModelStore
import com.core.presentation.navigation3.factory.INavigationGraphFactory
import com.core.presentation.navigation3.listener.INavigationGraphListener
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.graph.INavigationGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.collections.get

/**
 * The primary implementation of [INavigationGraphManager] and [INavigationGraphListener]
 * responsible for managing the navigation lifecycle and graph-scoped state.
 *
 * This class orchestrates the application's navigation by maintaining a reactive [backStack]
 * and ensuring that [ViewModelStore]s are created and destroyed in synchronization with
 * the user's movement through different navigation graphs.
 *
 * ### Lifecycle Management:
 * It utilizes a [snapshotFlow] to observe changes in the [backStack]. Whenever a destination
 * is added or removed, [onBackStackChange] is triggered to perform automated cleanup of
 * [ViewModelStore]s whose associated graphs are no longer active in the stack.
 *
 * ### Scoping:
 * Supports multi-module navigation by using the [INavigationGraphFactory] to resolve
 * destination-to-graph relationships, enabling seamless ViewModel sharing within
 * logical feature flows.
 *
 * @property scope The [CoroutineScope] used to host the reactive backstack observation flow.
 * @property navigationGraphFactory The factory used to resolve the launcher graph and
 *   map destinations back to their parent graphs for scoping.
 */
internal class NavigationGraphManagerImpl(
    private val scope: CoroutineScope,
    private val navigationGraphFactory: INavigationGraphFactory
) : INavigationGraphManager, INavigationGraphListener {
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    /**
     * Lazily initializes and maintains the reactive navigation backstack.
     *
     * This implementation sets up the initial state of the application by:
     * 1. Resolving the entry point via [INavigationGraphFactory.navigationGraphLauncher].
     * 2. Reflectively extracting the [objectInstance] of the start destination.
     * 3. Creating a [SnapshotStateList] to enable Compose to observe navigation changes.
     * 4. Launching a [snapshotFlow] within the provided [scope] to monitor backstack changes,
     *    which triggers [onBackStackChange] for automated [ViewModelStore] lifecycle management.
     *
     * @throws IllegalStateException if the resolved start destination is not a Kotlin `object`.
     * @return A thread-safe, observable list of [INavigationDestination] managed by the Compose Snapshot system.
     */
    override val backStack: SnapshotStateList<INavigationDestination> by lazy {
        val launcherEntry = navigationGraphFactory.navigationGraphLauncher.value.first()
        val startDestination = checkNotNull(launcherEntry.destination.objectInstance) {
            "Destination ${launcherEntry.destination.simpleName} must be a Kotlin 'object' to be used as a startDestination via reflection."
        }

        mutableStateListOf(startDestination).also {
            snapshotFlow { it.toList() }
                .onEach { onBackStackChange() }
                .launchIn(scope)
        }
    }

    /**
     * Retrieves an existing [ViewModelStore] or creates a new one for a given [INavigationGraph].
     *
     * This implementation uses the [INavigationGraph]'s qualified class name as a unique key
     * to manage a internal map of stores. This allows multiple screens belonging to the same
     * logical graph to share state by accessing the same [ViewModelStore].
     *
     * ### Lifecycle:
     * The stores created here are automatically cleaned up in [onBackStackChange] when the
     * associated graph is no longer represented by any destination in the [backStack].
     *
     * @param graph The navigation graph requesting a storage container for its ViewModels.
     * @return A [ViewModelStore] instance scoped to the provided graph.
     * @throws NullPointerException if the graph's qualified name cannot be resolved.
     */
    override fun getViewModelStore(
        graph: INavigationGraph
    ): ViewModelStore {
        return viewModelStores.getOrPut(
            key = graph::class.qualifiedName!!
        ) {
            ViewModelStore()
        }
    }

    /**
     * Pushes a new [INavigationDestination] onto the top of the [backStack].
     *
     * This implementation appends the provided [destination] to the [SnapshotStateList],
     * which triggers a recomposition in the Compose UI. Because of the [snapshotFlow]
     * observer initialized in the [backStack] property, this call will also trigger
     * [onBackStackChange] to ensure [ViewModelStore]s are correctly evaluated.
     *
     * @param destination The [INavigationDestination] instance to navigate to,
     * typically containing the required navigation arguments for the new screen.
     */
    override fun goToDestination(destination: INavigationDestination) {
        backStack.add(
            element = destination
        )
    }

    /**
     * Performs a back navigation by removing the last element from the [backStack].
     *
     * This implementation uses [removeLastOrNull] to safely decrease the stack size.
     * If the stack is not empty, the removal triggers a state change in the
     * [SnapshotStateList], causing the UI to recompose and show the previous destination.
     *
     * The [onBackStackChange] logic triggered by the [snapshotFlow] in [backStack]
     * will automatically handle any necessary [ViewModelStore] cleanup if the
     * removed destination was the last one associated with a specific graph.
     */
    override fun goBack() {
        backStack.removeLastOrNull()
    }

    /**
     * Responds to a pop event by re-evaluating the navigation state.
     *
     * This implementation delegates the request to [onBackStackChange] to ensure that
     * any [ViewModelStore] associated with graphs that are no longer present in the
     * backstack are properly cleared and destroyed.
     *
     * This method is part of the [INavigationGraphListener] interface, allowing the
     * manager to react to navigation events triggered from external sources.
     */
    override fun onPop() = onBackStackChange()

    /**
     * Synchronizes the lifecycle of graph-scoped [ViewModelStore]s with the current state of the [backStack].
     *
     * This core maintenance function ensures that memory is managed efficiently by identifying which
     * navigation graphs are currently "active" and destroying the storage for those that are no longer
     * reachable.
     *
     * ### Process:
     * 1. **Identify Active Graphs:** Iterates through every destination currently in the [backStack]
     *    and cross-references them with the [INavigationGraphFactory] to determine which parent
     *    graphs are represented.
     * 2. **Compare Keys:** Compares the set of active graph IDs against the keys currently held
     *    in the [viewModelStores] map.
     * 3. **Cleanup:** For any graph ID found in storage but no longer present in the backstack:
     *    - Calls [ViewModelStore.clear], which triggers `onCleared()` in all associated ViewModels.
     *    - Removes the entry from the internal map to prevent memory leaks.
     *
     * This function is automatically invoked by the [snapshotFlow] whenever the [backStack]
     * is modified (push/pop) or when an external [onPop] event occurs.
     */
    private fun onBackStackChange() {
        val graphs = navigationGraphFactory.navigationGraphsWithDestinations
        val activeGraphIds = backStack
            .mapNotNull { navigationKey ->
                graphs.entries.find { (_, entries) ->
                    entries.any {
                        it.destination.qualifiedName == navigationKey::class.qualifiedName
                    }
                }?.key
            }.map {
                it::class.qualifiedName
            }
            .toSet()

        val keysToRemove = viewModelStores.keys - activeGraphIds
        keysToRemove.forEach { graphId ->
            viewModelStores[graphId]?.clear() // Destroys the ViewModels
            viewModelStores.remove(graphId)
        }
    }
}