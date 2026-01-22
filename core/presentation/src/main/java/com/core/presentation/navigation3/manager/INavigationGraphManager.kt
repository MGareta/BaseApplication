package com.core.presentation.navigation3.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModelStore
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.graph.INavigationGraph

/**
 * A central coordinator responsible for managing the application's navigation state
 * and lifecycle-aware storage.
 *
 * This interface acts as the primary API for feature modules to interact with the
 * navigation system. It maintains the [backStack] and provides the necessary
 * infrastructure for **Graph-scoped ViewModels**, ensuring that shared state is
 * preserved or cleared according to the user's navigation flow.
 *
 * ### Key Responsibilities:
 * - **State Management:** Maintains a reactive [backStack] of [INavigationDestination]s.
 * - **Navigation Control:** Provides high-level commands for pushing ([goToDestination])
 *   or popping ([goBack]) the stack.
 * - **Scoped Storage:** Manages [ViewModelStore]s tied to the lifecycle of specific
 *   [INavigationGraph]s, enabling data sharing across multiple related screens.
 *
 * @see com.core.presentation.navigation3.model.destination.INavigationDestination
 * @see com.core.presentation.navigation3.model.graph.INavigationGraph
 */
interface INavigationGraphManager {

    /**
     * An observable list representing the current navigation history.
     *
     * This backstack uses [SnapshotStateList] to ensure that any changes (push or pop operations)
     * are automatically tracked by the Compose Snapshot system. This triggers a recomposition
     * of the UI whenever the stack is modified.
     *
     * Each element in the list is an [INavigationDestination], representing a unique
     * point in the application's navigation flow.
     *
     * @see INavigationDestination
     */
    val backStack : SnapshotStateList<INavigationDestination>

    /**
     * Retrieves or creates a [ViewModelStore] associated with a specific navigation graph.
     *
     * This method facilitates **Graph-scoped ViewModels**, allowing multiple screens within
     * the same [INavigationGraph] to share data or state. The [ViewModelStore] is tied to
     * the lifecycle of the graph; it is created when the graph is first accessed and
     * should be cleared only when the graph is fully removed from the [backStack].
     *
     * @param graph The [INavigationGraph] for which the view model store is requested.
     * @return A [ViewModelStore] dedicated to the lifecycle of the specified navigation graph.
     */
    fun getViewModelStore(
        graph: INavigationGraph
    ): ViewModelStore

    /**
     * Navigates to a new destination by adding it to the top of the [backStack].
     *
     * This method triggers a state change in the [SnapshotStateList], which in turn
     * notifies the Navigation3 runtime to compose the UI associated with the provided
     * [destination].
     *
     * @param destination The [INavigationDestination] to be pushed onto the stack.
     * This object typically contains any required navigation arguments.
     */
    fun goToDestination(
        destination: INavigationDestination
    )

    /**
     * Performs a back navigation by removing the topmost destination from the [backStack].
     *
     * Calling this method effectively returns the user to the previous screen in the
     * navigation history.
     *
     * This operation triggers a state change in the [SnapshotStateList], resulting in
     * the recomposition of the UI to reflect the new top destination.
     */
    fun goBack()
}
