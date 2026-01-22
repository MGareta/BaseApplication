package com.core.presentation.navigation3.di

import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.factory.INavigationGraphFactory
import com.core.presentation.navigation3.factory.NavigationGraphFactoryImpl
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.manager.NavigationGraphManagerImpl
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {

    @Provides
    @ActivityRetainedScoped
    fun providesNavigationGraphFactory(
        destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationEntry<INavigationDestination, ViewModel>>>,
        graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>,
    ): INavigationGraphFactory = NavigationGraphFactoryImpl(
        destinations = destinations,
        graphs = graphs,
    )

    @Provides
    @ActivityRetainedScoped
    fun provideNavigationScope(): CoroutineScope = CoroutineScope(
        context = SupervisorJob() + Dispatchers.Main.immediate
    )

    @Provides
    @ActivityRetainedScoped
    fun providesNavigationGraphManager(
        scope: CoroutineScope,
        navigationGraphFactory: INavigationGraphFactory,
    ): INavigationGraphManager = NavigationGraphManagerImpl(
        scope = scope,
        navigationGraphFactory = navigationGraphFactory
    )
}