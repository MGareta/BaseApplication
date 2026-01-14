package com.core.presentation.navigation.di

import com.core.presentation.navigation.factory.INavigationGraphFactory
import com.core.presentation.navigation.factory.NavigationGraphFactoryImpl
import com.core.presentation.navigation.manager.INavigationManager
import com.core.presentation.navigation.manager.NavigationManagerImpl
import com.core.presentation.navigation.model.directions.INavigationDirections
import com.core.presentation.navigation.model.entry.INavigationGraphEntry
import com.core.presentation.navigation.model.graph.INavigationGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
    @Singleton
    @Provides
    fun providesNavigationGraphFactory(
        destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationGraphEntry<*, *>>>,
        graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>,
    ): INavigationGraphFactory = NavigationGraphFactoryImpl(
        destinations = destinations,
        graphs = graphs,
    )

    @Singleton
    @Provides
    fun providesNavigationManagerImpl(): INavigationManager = NavigationManagerImpl()

    @Singleton
    @Provides
    fun providesNavigationDirections(navigationManager: INavigationManager): INavigationDirections =
        navigationManager as INavigationDirections
}
