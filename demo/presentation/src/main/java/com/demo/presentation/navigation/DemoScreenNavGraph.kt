package com.demo.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry
import com.core.presentation.navigation.model.destination.INavigationDestination
import com.core.presentation.navigation.model.graph.INavigationGraph
import com.core.presentation.navigation.model.graph.INavigationGraphLauncher
import com.demo.presentation.model.DemoScreenNavDestination
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlin.reflect.KClass

@Serializable
class DemoScreenNavGraph @Inject constructor() : INavigationGraph, INavigationGraphLauncher {
    override val startingDestination: KClass<out INavigationDestination>
        get() = DemoScreenNavDestination::class

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?
        get() = null

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?
        get() = null

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)?
        get() = null

    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)?
        get() = null
}
