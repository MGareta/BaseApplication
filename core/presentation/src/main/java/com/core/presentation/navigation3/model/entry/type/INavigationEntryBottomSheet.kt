package com.core.presentation.navigation3.model.entry.type

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.INavigationEntry

/**
 * A specialized [INavigationEntry] designed for destinations that should be presented
 * as a [androidx.compose.material3.ModalBottomSheet].
 *
 * This abstract class extends the base navigation entry to include configurations specific
 * to bottom sheets. When a destination implementation extends this class, the navigation
 * system (via a `BottomSheetSceneStrategy`) identifies it and wraps its [Render] content
 * within a Material 3 bottom sheet overlay rather than a standard full-screen scene.
 *
 * ### Responsibilities:
 * - **Overlay Configuration:** Requires implementations to provide [modalBottomSheetProperties]
 *   to define the sheet's behavior (e.g., dismiss on tap outside, drag handles).
 * - **Visual Layering:** Ensures the destination is treated as a floating UI element
 *   that overlays previous backstack entries.
 *
 * @param DESTINATION The type of [INavigationDestination] this entry handles (the Route).
 * @param VM The type of [ViewModel] associated with this destination's lifecycle.
 * @see INavigationEntry
 * @see androidx.compose.material3.ModalBottomSheet
 */
abstract class INavigationEntryBottomSheet<out DESTINATION : INavigationDestination, out VM: ViewModel> : INavigationEntry<DESTINATION, VM>() {

    /**
     * Defines the configuration and behavior for the [androidx.compose.material3.ModalBottomSheet].
     *
     * Implementations must provide a [ModalBottomSheetProperties] instance that dictates
     * how the sheet interacts with the user. This includes settings such as:
     * - **dismissOnBackPress**: Whether the sheet closes when the system back button is pressed.
     * - **dismissOnClickOutside**: Whether tapping the background scrim dismisses the sheet.
     * - **shouldDismissOnBackPress**: Custom logic to determine if back presses should trigger dismissal.
     *
     * This property is used by the navigation strategy to apply these specific behaviors
     * to the generated bottom sheet scene.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    abstract val modalBottomSheetProperties: ModalBottomSheetProperties
}