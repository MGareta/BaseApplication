package com.core.presentation.navigation3.listener

/**
 * A contract for observing and reacting to navigation lifecycle events.
 *
 * This interface is used by navigation managers and controllers to listen for
 * structural changes in the navigation hierarchy. It is specifically designed
 * to handle events that originate from the UI or the system (such as the back press)
 * that require the navigation state to be updated or cleaned up.
 *
 * ### Implementation:
 * Typically implemented by a navigation manager (e.g., [com.core.presentation.navigation3.manager.INavigationGraphManager])
 * to synchronize internal state, such as clearing graph-scoped `ViewModelStore`s,
 * when a navigation context is removed.
 *
 * @see com.core.presentation.navigation3.manager.INavigationGraphManager
 */
internal interface INavigationGraphListener {

    /**
     * Callback triggered when a navigation graph or screen is requested to be popped
     * from the backstack.
     *
     * This method is typically invoked during "back" navigation events (e.g., system
     * back press or a manual 'up' navigation) to notify listeners that the current
     * navigation context is being closed or dismissed.
     */
    fun onPop()
}