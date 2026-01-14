package com.core.presentation.navigation.model.directions

import androidx.compose.runtime.Stable
import androidx.navigation.NavOptionsBuilder
import com.core.presentation.navigation.model.destination.INavigationDestination

@Stable
sealed interface NavigationIntent {
    data class Navigate(val destination: INavigationDestination, val builder: NavOptionsBuilder.() -> Unit = {}) :
        NavigationIntent {
        override fun toString(): String = "destination=$destination"
    }

    data object PopCurrentBackStack : NavigationIntent

    data class PopBackStackUntil(
        val route: INavigationDestination,
        val inclusive: Boolean,
        val saveState: Boolean = false,
    ) : NavigationIntent

    data object NavigateUp : NavigationIntent
}
