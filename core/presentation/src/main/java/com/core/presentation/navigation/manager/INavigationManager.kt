package com.core.presentation.navigation.manager

import androidx.compose.runtime.Immutable
import androidx.navigation.NavOptionsBuilder
import com.core.presentation.navigation.model.destination.INavigationDestination

@Immutable
interface INavigationManager {
    fun navigate(destination: INavigationDestination, builder: NavOptionsBuilder.() -> Unit = {})

    fun navigateUp()

    fun popCurrentBackStack()

    fun popBackStackUntil(destination: INavigationDestination, inclusive: Boolean, saveState: Boolean = false)
}
