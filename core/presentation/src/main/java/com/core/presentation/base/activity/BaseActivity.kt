package com.core.presentation.base.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.core.data.session.manager.ISessionManager
import com.core.data.session.model.SessionCommand
import com.core.presentation.navigation3.factory.INavigationGraphFactory
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.decorators.viewModel.rememberSharedViewModelStoreNavEntryDecorator
import com.core.presentation.navigation3.model.entry.scenes.BottomSheetSceneStrategy
import com.core.presentation.navigation3.model.graph.type.INavigationGraphBottomNavBar
import com.core.presentation.ui.components.bottomnavbar.BottomNavBarComponent
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.LocalBottomNavBarScrollConnection
import com.core.presentation.ui.components.bottomnavbar.scrollconnection.utils.rememberBottomNavBarScrollConnection
import com.core.presentation.ui.theme.LocalDimensions
import com.core.presentation.ui.theme.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
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
            val dialogStrategy = remember { DialogSceneStrategy<INavigationDestination>() }
            val bottomSheetStrategy = remember { BottomSheetSceneStrategy<INavigationDestination>() }

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

            Theme {
                val scrollConnection = rememberBottomNavBarScrollConnection()
                val bottomNavGraphs = remember {
                    navigationGraphFactory
                        .get()
                        .navigationGraphBottomNavBar
                }

                CompositionLocalProvider(
                    LocalBottomNavBarScrollConnection provides scrollConnection
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        NavDisplay(
                            modifier = Modifier
                                .fillMaxSize(),
                            backStack = navigationGraphManager.get().backStack,
                            sceneStrategies = listOf(
                                bottomSheetStrategy,
                                dialogStrategy
                            ),
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

                        AnimatedContent(
                            modifier = Modifier
                                .align(
                                    alignment = Alignment.BottomCenter
                                )
                                .windowInsetsPadding(
                                    insets = WindowInsets.navigationBars
                                )
                                .padding(
                                    horizontal = LocalDimensions.current.dp.space.space16
                                ),
                            targetState = navigationGraphFactory.get()
                                .getNavigationGraphFromDestination(
                                    destination = navigationGraphManager.get().backStack.last()
                                ),
                            transitionSpec = {
                                slideInVertically { it } togetherWith slideOutVertically { it }
                            }
                        ) {
                            if (it is INavigationGraphBottomNavBar) {
                                BottomNavBarComponent(
                                    modifier = Modifier,
                                    bottomNavGraphs = bottomNavGraphs,
                                    selectedNavGraph = it,
                                    scrollConnection = scrollConnection,
                                    onTabSelected = {
                                    }
                                )
                            }
                        }
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
