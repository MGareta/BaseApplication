package com.core.presentation.navigation3.manager

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModelStore
import com.core.presentation.navigation3.factory.INavigationGraphFactory
import com.core.presentation.navigation3.listener.INavigationGraphListener
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.graph.INavigationGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.collections.get

class NavigationGraphManagerImpl(
    private val scope: CoroutineScope,
    private val navigationGraphFactory: INavigationGraphFactory
) : INavigationGraphManager, INavigationGraphListener {
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    override val backStack: SnapshotStateList<INavigationDestination> by lazy {
        val launcherEntry = navigationGraphFactory.navigationGraphLauncher.value.first()
        val startDestination = checkNotNull(launcherEntry.destination.objectInstance) {
            "Destination ${launcherEntry.destination.simpleName} must be a Kotlin 'object' to be used as a startDestination via reflection."
        }

        mutableStateListOf(startDestination).also {
            snapshotFlow { it.toList() }
                .onEach { onBackStackChange() }
                .launchIn(scope)
        }
    }

    override fun getViewModelStore(
        graph: INavigationGraph
    ): ViewModelStore {
        return viewModelStores.getOrPut(
            key = graph::class.qualifiedName!!
        ) {
            ViewModelStore()
        }
    }

    override fun goToDestination(destination: INavigationDestination) {
        backStack.add(
            element = destination
        )
    }

    override fun goBack() {
        backStack.removeLastOrNull()
    }

    override fun onPop() = onBackStackChange()

    private fun onBackStackChange() {
        val graphs = navigationGraphFactory.navigationGraphsWithDestinations
        val activeGraphIds = backStack
            .mapNotNull { navigationKey ->
                graphs.entries.find { (_, entries) ->
                    entries.any {
                        it.destination.qualifiedName == navigationKey::class.qualifiedName
                    }
                }?.key
            }.map {
                it::class.qualifiedName
            }
            .toSet()

        val keysToRemove = viewModelStores.keys - activeGraphIds
        keysToRemove.forEach { graphId ->
            viewModelStores[graphId]?.clear() // Destroys the ViewModels
            viewModelStores.remove(graphId)
        }
    }
}