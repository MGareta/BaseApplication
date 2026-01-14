package com.core.presentation.navigation.model.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Immutable
import androidx.navigation.NavBackStackEntry
import com.core.presentation.navigation.model.destination.INavigationDestination
import kotlin.reflect.KClass

@Immutable
interface INavigationGraph {
    val startingDestination: KClass<out INavigationDestination>

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?
}
