package com.demo.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.entry.type.INavigationEntryBottomSheet
import com.demo.presentation.model.DemoScreenNavDestination2
import com.demo.presentation.viewModel.DemoViewModel
import dagger.Lazy
import javax.inject.Inject

class DemoScreenNavEntry2 @Inject constructor(
    override val navigator: Lazy<INavigationGraphManager>
) : INavigationEntryBottomSheet<DemoScreenNavDestination2, DemoViewModel>() {

//    override val dialogProperties: DialogProperties
//        get() = DialogProperties()

    @OptIn(ExperimentalMaterial3Api::class)
    override val modalBottomSheetProperties: ModalBottomSheetProperties
        get() = ModalBottomSheetProperties()

    @Composable
    override fun Render(
        params: DemoScreenNavDestination2,
        viewModel: DemoViewModel
    ) {
        val state = viewModel.viewState.collectAsStateWithLifecycle().value

        Text(
            text = params.name
        )
    }
}