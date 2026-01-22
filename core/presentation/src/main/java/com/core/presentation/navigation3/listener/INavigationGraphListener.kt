package com.core.presentation.navigation3.listener

interface INavigationGraphListener {

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