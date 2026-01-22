package com.core.presentation.navigation3.manager

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModelStore
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.graph.INavigationGraph

interface INavigationGraphManager {

    val backStack : SnapshotStateList<INavigationDestination>

    fun getViewModelStore(
        graph: INavigationGraph
    ): ViewModelStore

    fun goToDestination(
        destination: INavigationDestination
    )

    fun goBack()
}
