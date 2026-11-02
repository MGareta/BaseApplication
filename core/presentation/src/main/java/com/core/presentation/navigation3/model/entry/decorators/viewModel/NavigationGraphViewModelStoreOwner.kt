package com.core.presentation.navigation3.model.entry.decorators.viewModel

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * A custom [ViewModelStoreOwner] implementation designed to scope ViewModels to a navigation graph's lifecycle.
 *
 * This class acts as a container for a [ViewModelStore] that is managed externally by the
 * [com.core.presentation.navigation3.manager.INavigationGraphManager]. By wrapping a specific
 * [ViewModelStore] in this owner, multiple destinations within the same [com.core.presentation.navigation3.model.graph.INavigationGraph]
 * can share the same ViewModel instances.
 *
 * ### Usage:
 * It is primarily used within [SharedViewModelStoreNavEntryDecorator] to provide a shared
 * [androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner] via [androidx.compose.runtime.CompositionLocalProvider].
 */
internal class NavigationGraphViewModelStoreOwner(
    override val viewModelStore: ViewModelStore,
    override val defaultViewModelProviderFactory: ViewModelProvider.Factory,
    override val defaultViewModelCreationExtras: CreationExtras
) : ViewModelStoreOwner, HasDefaultViewModelProviderFactory
