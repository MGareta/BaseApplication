package com.core.presentation.base.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.core.data.session.manager.ISessionManager
import com.core.data.session.model.SessionCommand
import com.core.presentation.navigation.factory.INavigationGraphFactory
import com.core.presentation.navigation.model.directions.INavigationDirections
import com.core.presentation.navigation.model.directions.NavigationIntent
import com.core.presentation.navigation.model.graph.INavigationGraphBottomNavBar
import com.core.presentation.navigation.model.utils.addGraphs
import com.core.presentation.navigation.model.utils.asStable
import com.core.presentation.navigation.model.utils.navigator.bottomSheet.BottomSheetHost
import com.core.presentation.navigation.model.utils.navigator.bottomSheet.BottomSheetNavigator
import com.core.presentation.ui.components.bottomnavbar.BottomNavBarComponent
import com.core.presentation.ui.components.container.ContainerComponent
import com.core.presentation.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    @Inject
    lateinit var graphFactory: dagger.Lazy<INavigationGraphFactory>

    @Inject
    lateinit var navigationDirections: dagger.Lazy<INavigationDirections>

    @Inject
    lateinit var sessionManager: dagger.Lazy<ISessionManager>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            ),
        )

        super.onCreate(savedInstanceState)

        setContent {
            val bottomSheetNavigator = remember { BottomSheetNavigator() }
            navController = rememberNavController(
                navigators = arrayOf(bottomSheetNavigator),
            )

            val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryFlow
                .collectAsStateWithLifecycle(
                    null,
                )

            Theme {
                LaunchedEffect(key1 = sessionManager) {
                    sessionManager
                        .get()
                        .commands
                        .distinctUntilChanged()
                        .collectLatest {
                            when (it) {
                                SessionCommand.Warning -> onSessionWarning()
                                SessionCommand.TimeOut -> onSessionTimeout()
                            }
                        }
                }

                LaunchedEffect(key1 = navController) {
                    navigationDirections
                        .get()
                        .directions
                        .collectLatest { navigationEvent ->
                            when (navigationEvent) {
                                is NavigationIntent.Navigate -> navController.navigate(
                                    navigationEvent.destination,
                                    navigationEvent.builder,
                                )

                                is NavigationIntent.NavigateUp -> navController.navigateUp()
                                is NavigationIntent.PopCurrentBackStack -> navController.popBackStack()
                                is NavigationIntent.PopBackStackUntil -> navController.popBackStack(
                                    navigationEvent.route,
                                    navigationEvent.inclusive,
                                    navigationEvent.saveState,
                                )
                            }

                            Log.d("NavDirections", navigationEvent.toString())
                        }
                }

                ContainerComponent(
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = {
                        navBackStackEntry?.destination?.route?.let { route ->
                            AnimatedVisibility(
                                visible = graphFactory.get().isBottomNavBarDestination(
                                    destination = route,
                                ),
                                enter = slideInVertically { it },
                                exit = slideOutVertically { it },
                            ) {
                                BottomNavBarComponent(
                                    bottomNavGraphs = graphFactory
                                        .get()
                                        .getBottomNavBarNavigationGraphs()
                                        .map {
                                            it.key as INavigationGraphBottomNavBar
                                        }.toSet(),
                                )
                            }
                        }
                    },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        NavHost(
                            modifier = Modifier
                                .fillMaxSize(),
                            navController = navController,
                            startDestination = graphFactory.get().getLauncherNavGraph()::class,
                        ) {
                            addGraphs(
                                navController = navController.asStable,
                                navigationGraphs = graphFactory.get().navigationGraphsWithDestinations,
                            )
                        }

                        BottomSheetHost(
                            navigator = bottomSheetNavigator,
                        )
                    }
                }
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()

        lifecycleScope.launch {
            sessionManager.get().restartSessionTimeout()
        }
    }

    abstract fun onSessionWarning()

    open fun onSessionTimeout() {
        lifecycleScope.launch {
            sessionManager.get().invalidateSession()
        }
    }
}
