package com.core.presentation.navigation3.model.entry.decorators.viewModel

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

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
 *
 * @param store The [ViewModelStore] that will hold the ViewModels for the associated navigation graph.
 */
internal class NavigationGraphViewModelStoreOwner(store: ViewModelStore) : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = store
}
