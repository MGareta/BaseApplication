package com.demo.presentation.di

import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import com.demo.presentation.navigation.DemoScreenNavGraph
import com.demo.presentation.navigation.DemoScreenNavGraphEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityRetainedComponent::class)
class DemoScreenModule {

    @Provides
    @IntoMap
    @ClassKey(value = DemoScreenNavGraph::class)
    fun providesDemoNavGraph(graph: DemoScreenNavGraph): INavigationGraph = graph


    @Provides
    @IntoMap
    @ClassKey(value = DemoScreenNavGraph::class)
    fun providesDemoScreenNavGraphEntry(
        demoNavigationEntry: DemoScreenNavGraphEntry
    ) : Set<INavigationEntry<INavigationDestination, ViewModel>> = setOf(
        demoNavigationEntry
    )
}