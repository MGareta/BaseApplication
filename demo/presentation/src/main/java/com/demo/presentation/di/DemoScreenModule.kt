package com.demo.presentation.di

import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.graph.INavigationGraph
import com.demo.presentation.navigation.DemoScreenNavGraph
import com.demo.presentation.navigation.DemoScreenNavGraphEntry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
class DemoScreenModule {

    @Provides
    @IntoMap
    @ClassKey(DemoScreenNavGraph::class)
    fun providesDemoScreenNavGraphEntries(
        demoScreenNavGraphEntry: DemoScreenNavGraphEntry
    ): Set<INavigationGraphEntry<*, *>> =
        setOf(
            demoScreenNavGraphEntry
        )

    @Provides
    @IntoMap
    @ClassKey(DemoScreenNavGraph::class)
    fun bindsOtpNavGraph(graph: DemoScreenNavGraph): INavigationGraph = graph
}