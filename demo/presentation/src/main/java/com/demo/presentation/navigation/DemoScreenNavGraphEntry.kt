package com.demo.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.core.presentation.navigation.model.arguments.navGraphEntryArgument
import com.core.presentation.navigation.model.entry.type.INavigationGraphScreenEntry
import com.core.presentation.navigation.model.utils.StableHolder
import com.demo.presentation.model.DemoScreenNavDestination
import com.demo.presentation.model.DemoScreenNavDestinationTypeMap
import com.demo.presentation.view.DemoScreen
import com.demo.presentation.viewModel.DemoViewModel
import javax.inject.Inject
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class DemoScreenNavGraphEntry @Inject constructor() : INavigationGraphScreenEntry<DemoScreenNavDestination, DemoViewModel> {
    override val params: Map<KType, NavType<*>>
        get() = DemoScreenNavDestinationTypeMap().value

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?
        get() = null

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?
        get() = null

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?
        get() = null

    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?
        get() = null

    override val sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform)?
        get() = null

    override fun onComposableStarted(viewModel: DemoViewModel) = Unit

    override fun onComposableStopped(viewModel: DemoViewModel) = Unit

    @Composable
    override fun Render(
        controller: StableHolder<NavHostController>,
        viewModel: DemoViewModel
    ) {
        val state = viewModel.viewState.collectAsStateWithLifecycle().value
        val params = controller.item.currentBackStackEntry?.toRoute<DemoScreenNavDestination>()?.id

        DemoScreen(
            state = state
        ) {
            viewModel.handleUiEvents(
                event = it
            )
        }
    }
}