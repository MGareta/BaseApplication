package com.core.presentation.base.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.core.data.session.manager.ISessionManager
import com.core.presentation.navigation3.factory.INavigationGraphFactory
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.entry.decorators.viewModel.rememberSharedViewModelStoreNavEntryDecorator
import com.core.presentation.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var navigationGraphFactory: dagger.Lazy<INavigationGraphFactory>

    @Inject
    lateinit var navigationGraphManager: dagger.Lazy<INavigationGraphManager>

    @Inject
    lateinit var sessionManager: dagger.Lazy<ISessionManager>

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        super.onCreate(savedInstanceState)

        setContent {
            Theme {
                NavDisplay(
                    backStack = navigationGraphManager.get().backStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberSharedViewModelStoreNavEntryDecorator(
                            navigationGraphManager = navigationGraphManager.get(),
                            graphs = navigationGraphFactory.get().navigationGraphsWithDestinations
                        )
                    ),
                    entryProvider = entryProvider {
                        navigationGraphFactory.get().register(
                            scope = this
                        )
                    }
                )
            }

//            val bottomSheetNavigator = remember { BottomSheetNavigator() }
//            navController = rememberNavController(
//                navigators = arrayOf(bottomSheetNavigator),
//            )
//
//            val navBackStackEntry: NavBackStackEntry? by navController.currentBackStackEntryFlow
//                .collectAsStateWithLifecycle(
//                    null,
//                )
//
//            Theme {
//                LaunchedEffect(key1 = sessionManager) {
//                    sessionManager
//                        .get()
//                        .commands
//                        .distinctUntilChanged()
//                        .collectLatest {
//                            when (it) {
//                                SessionCommand.Warning -> onSessionWarning()
//                                SessionCommand.TimeOut -> onSessionTimeout()
//                            }
//                        }
//                }
//
//                LaunchedEffect(key1 = navController) {
//                    navigationDirections
//                        .get()
//                        .directions
//                        .collectLatest { navigationEvent ->
//                            when (navigationEvent) {
//                                is NavigationIntent.Navigate -> navController.navigate(
//                                    navigationEvent.destination,
//                                    navigationEvent.builder,
//                                )
//
//                                is NavigationIntent.NavigateUp -> navController.navigateUp()
//                                is NavigationIntent.PopCurrentBackStack -> navController.popBackStack()
//                                is NavigationIntent.PopBackStackUntil -> navController.popBackStack(
//                                    navigationEvent.route,
//                                    navigationEvent.inclusive,
//                                    navigationEvent.saveState,
//                                )
//                            }
//
//                            Log.d("NavDirections", navigationEvent.toString())
//                        }
//                }
//
//                ContainerComponent(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                    bottomBar = {
//                        navBackStackEntry?.destination?.route?.let { route ->
//                            AnimatedVisibility(
//                                visible = graphFactory.get().isBottomNavBarDestination(
//                                    destination = route,
//                                ),
//                                enter = slideInVertically { it },
//                                exit = slideOutVertically { it },
//                            ) {
//                                BottomNavBarComponent(
//                                    bottomNavGraphs = graphFactory
//                                        .get()
//                                        .getBottomNavBarNavigationGraphs()
//                                        .map {
//                                            it.key as INavigationGraphBottomNavBar
//                                        }.toSet(),
//                                )
//                            }
//                        }
//                    },
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize(),
//                    ) {
//                        NavHost(
//                            modifier = Modifier
//                                .fillMaxSize(),
//                            navController = navController,
//                            startDestination = graphFactory.get().getLauncherNavGraph()::class,
//                        ) {
//                            addGraphs(
//                                navController = navController.asStable,
//                                navigationGraphs = graphFactory.get().navigationGraphsWithDestinations,
//                            )
//                        }
//
//                        BottomSheetHost(
//                            navigator = bottomSheetNavigator,
//                        )
//                    }
//                }
//            }
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
