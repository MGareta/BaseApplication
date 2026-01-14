package com.core.presentation.navigation.manager

import androidx.navigation.NavOptionsBuilder
import com.core.presentation.navigation.model.destination.INavigationDestination
import com.core.presentation.navigation.model.directions.INavigationDirections
import com.core.presentation.navigation.model.directions.NavigationIntent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Singleton

@Singleton
class NavigationManagerImpl :
    INavigationManager,
    INavigationDirections {
    private val _directions = Channel<NavigationIntent>(Channel.BUFFERED)
    override val directions = _directions.receiveAsFlow()

    override fun navigate(destination: INavigationDestination, builder: NavOptionsBuilder.() -> Unit) {
        _directions.trySend(
            element = NavigationIntent.Navigate(
                destination = destination,
                builder = builder,
            ),
        )
    }

    override fun navigateUp() {
        _directions.trySend(element = NavigationIntent.NavigateUp)
    }

    override fun popCurrentBackStack() {
        _directions.trySend(element = NavigationIntent.PopCurrentBackStack)
    }

    override fun popBackStackUntil(destination: INavigationDestination, inclusive: Boolean, saveState: Boolean) {
        _directions.trySend(
            element = NavigationIntent.PopBackStackUntil(
                route = destination,
                inclusive = inclusive,
                saveState = saveState,
            ),
        )
    }
}
