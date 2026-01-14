package com.core.presentation.navigation.model.utils.navigator.bottomSheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("M3BottomSheetNavigator")
class BottomSheetNavigator : Navigator<BottomSheetNavigator.Destination>() {
    internal val backStack get() = state.backStack

    internal fun dismiss(backStackEntry: NavBackStackEntry) {
        state.popWithTransition(backStackEntry, false)
    }

    override fun navigate(entries: List<NavBackStackEntry>, navOptions: NavOptions?, navigatorExtras: Extras?) {
        entries.forEach { entry ->
            state.pushWithTransition(entry)
        }
    }

    override fun createDestination(): Destination = Destination(this) { }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        state.popWithTransition(popUpTo, savedState)
    }

    internal fun onTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    @NavDestination.ClassType(Composable::class)
    class Destination(navigator: BottomSheetNavigator, internal val content: @Composable (NavBackStackEntry) -> Unit) :
        NavDestination(navigator),
        FloatingWindow {
        internal var securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit
        internal var skipPartiallyExpanded: Boolean = true
    }
}
