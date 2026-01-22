package com.core.presentation.navigation3.model.entry.decorators.viewModel

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class NavigationGraphViewModelStoreOwner(store: ViewModelStore) : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = store
}
