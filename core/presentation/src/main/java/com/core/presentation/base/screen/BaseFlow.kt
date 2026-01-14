package com.core.presentation.base.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.HiltViewModelFactory
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.core.presentation.base.viewmodel.BaseViewModel
import com.core.presentation.navigation.model.graph.INavigationGraph

@Composable
inline fun <reified VIEWMODEL : BaseViewModel<*, *, *>> BaseFlow(
    navHostController: NavHostController,
    viewModelClass: Class<VIEWMODEL>,
    navigationGraph: INavigationGraph,
    currentNavBackStackEntry: NavBackStackEntry,
    content: @Composable (BaseViewModel<*, *, *>) -> Unit,
) {
    val navGraph = remember(currentNavBackStackEntry) {
        navHostController.getBackStackEntry(navigationGraph)
    }

    val factory = HiltViewModelFactory(
        context = LocalContext.current,
        delegateFactory = navGraph.defaultViewModelProviderFactory,
    )

    val extras = if (LocalViewModelStoreOwner.current is HasDefaultViewModelProviderFactory) {
        (LocalViewModelStoreOwner.current as HasDefaultViewModelProviderFactory).defaultViewModelCreationExtras
    } else {
        CreationExtras.Empty
    }

    val provider = ViewModelProvider(navGraph.viewModelStore, factory, extras)

    @SuppressLint("RestrictedApi")
    val viewModel = if (navGraph.viewModelStore[viewModelClass.simpleName] != null) {
        provider[viewModelClass.simpleName, viewModelClass]
    } else {
        provider[viewModelClass.simpleName, viewModelClass].also {
            it.loadInitData()
        }
    }

    return content(viewModel)
}
