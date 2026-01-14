package com.core.presentation.navigation.model.entry.type

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.destination.INavigationDestination
import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.utils.StableHolder

interface INavigationGraphScreenEntry<
    DESTINATION : INavigationDestination,
    VIEWMODEL : BaseViewModel<*, *, *>,
    > : INavigationGraphEntry<DESTINATION, VIEWMODEL> {
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?

    val sizeTransform: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform)?

    fun onComposableStarted(viewModel: VIEWMODEL)

    fun onComposableStopped(viewModel: VIEWMODEL)

    @Composable
    fun Render(controller: StableHolder<NavHostController>, viewModel: VIEWMODEL)
}
