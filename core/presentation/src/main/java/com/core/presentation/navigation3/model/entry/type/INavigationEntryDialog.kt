package com.core.presentation.navigation3.model.entry.type

import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.INavigationEntry

/**
 * A specialized [INavigationEntry] designed for destinations that should be presented
 * within a standard [androidx.compose.ui.window.Dialog].
 *
 * This abstract class extends the base navigation entry to support overlay-based UI.
 * When a destination implementation extends this class, the navigation system identifies
 * it and wraps its [Render] content within a Dialog window rather than a standard
 * full-screen scene. This allows the destination to appear on top of the current
 * screen while maintaining the existing backstack visibility.
 *
 * ### Responsibilities:
 * - **Overlay Configuration:** Requires implementations to provide [dialogProperties]
 *   to define standard window behaviors (e.g., dismiss on back press or outside click).
 * - **Focus Management:** Ensures that the destination captures user input while
 *   the dialog is active.
 *
 * @param DESTINATION The type of [INavigationDestination] this entry handles (the Route).
 * @param VM The type of [ViewModel] associated with this destination's lifecycle.
 * @see INavigationEntry
 * @see androidx.compose.ui.window.Dialog
 */
abstract class INavigationEntryDialog<out DESTINATION : INavigationDestination, out VM: ViewModel> : INavigationEntry<DESTINATION, VM>() {

    /**
     * Defines the configuration and behavior for the [androidx.compose.ui.window.Dialog] window.
     *
     * Implementations must provide a [DialogProperties] instance that dictates how the
     * dialog window interacts with the user and the system. This includes settings such as:
     *
     * - **dismissOnBackPress**: Whether the dialog closes when the system back button is pressed.
     * - **dismissOnClickOutside**: Whether tapping the background scrim dismisses the dialog.
     * - **usePlatformDefaultWidth**: Whether the dialog should be restricted to the platform's
     *   default window width.
     *
     * This property is used by the navigation strategy to apply these window-specific
     * behaviors when rendering the entry as an overlay.
     */
    abstract val dialogProperties: DialogProperties
}