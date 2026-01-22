package com.core.presentation.navigation3.model.entry.scenes

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * A specialized [OverlayScene] that renders its content within a Material 3 [ModalBottomSheet].
 *
 * This class represents a "floating" destination in the Navigation3 system. Unlike a standard
 * full-screen [Scene], this scene overlays the [entry] on top of the [previousEntries]
 * without removing them from the composition, allowing the background to remain visible
 * behind the sheet.
 *
 * ### Lifecycle:
 * - When [onBack] is triggered (via dismiss request), the navigation stack should be
 *   popped to remove this scene.
 * - The content is rendered using the [entry]'s [NavEntry.Content] Composable inside
 *   the [ModalBottomSheet] container.
 *
 * @param T The type of the key representing the navigation destination.
 * @property key The unique identifier for this scene, derived from the [entry]'s content key.
 * @property previousEntries The list of entries beneath this bottom sheet in the backstack.
 * @property overlaidEntries The list of entries that this sheet is currently overlaying.
 * @property entry The [NavEntry] containing the specific UI content for the sheet.
 * @property modalBottomSheetProperties Configuration for the sheet (e.g., behavior, appearance).
 * @property onBack A callback invoked when the user dismisses the sheet (e.g., swiping down or clicking outside).
 */
@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val modalBottomSheetProperties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    /**
     * The list of [NavEntry] objects managed by this scene.
     *
     * In a [BottomSheetScene], this list contains exactly one element: the [entry] that
     * represents the content of the bottom sheet itself. This provides the navigation
     * runtime with access to the entry's lifecycle, state, and arguments while the
     * sheet is active.
     */
    override val entries: List<NavEntry<T>> = listOf(entry)

    /**
     * The [Composable] lambda that defines the visual representation of this scene.
     *
     * This implementation wraps the destination's [entry] content inside a
     * Material 3 [ModalBottomSheet]. It manages the integration between the
     * Navigation3 lifecycle and the Compose Material UI by:
     * 1. Assigning [onBack] to [ModalBottomSheet.onDismissRequest] to handle swipe-to-dismiss
     *    and outside-tap events.
     * 2. Applying the configured [modalBottomSheetProperties] for custom sheet behavior.
     * 3. Invoking [NavEntry.Content] as the primary UI inside the bottom sheet container.
     */
    override val content: @Composable (() -> Unit) = {
        ModalBottomSheet(
            onDismissRequest = onBack,
            properties = modalBottomSheetProperties,
        ) {
            entry.Content()
        }
    }
}

/**
 * A [SceneStrategy] that identifies and creates [BottomSheetScene]s based on entry metadata.
 *
 * This strategy acts as a coordinator during the navigation rendering process. It examines the
 * backstack and determines if the topmost destination should be rendered as a [ModalBottomSheet]
 * instead of a standard full-screen [Scene].
 *
 * ### Mechanism:
 * - It checks the [NavEntry.metadata] of the last entry in the backstack for the presence of
 *   [BOTTOM_SHEET_KEY].
 * - If the key is present and contains [ModalBottomSheetProperties], it produces a [BottomSheetScene].
 * - If the key is absent, it returns `null`, allowing the next strategy in the chain (typically
 *   a standard [SceneStrategy]) to handle the rendering.
 *
 * @param T The type of the key representing the navigation destination.
 */
@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    /**
     * Determines whether the current set of backstack entries should be rendered as a [BottomSheetScene].
     *
     * This implementation inspects the last entry in the [entries] list. If that entry contains
     * [ModalBottomSheetProperties] within its metadata (identified by [BOTTOM_SHEET_KEY]), it
     * constructs a [BottomSheetScene] to wrap the entry.
     *
     * ### Selection Logic:
     * - **Match Found:** If the metadata exists, the last entry is designated as the bottom sheet,
     *   and all preceding entries are passed as [BottomSheetScene.previousEntries] to ensure
     *   the underlying screen remains visible in the background.
     * - **No Match:** If the metadata is missing or the list is empty, it returns `null`,
     *   signaling to the navigation runtime that this strategy does not apply and the
     *   next strategy in the pipeline should be used.
     *
     * @param entries The current backstack of [NavEntry] objects.
     * @return A [BottomSheetScene] if the top entry is configured as a bottom sheet, or `null` otherwise.
     */
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull()
        val bottomSheetProperties = lastEntry?.metadata?.get(BOTTOM_SHEET_KEY) as? ModalBottomSheetProperties

        return bottomSheetProperties?.let { properties ->
            @Suppress("UNCHECKED_CAST")
            BottomSheetScene(
                key = lastEntry.contentKey as T,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                entry = lastEntry,
                modalBottomSheetProperties = properties,
                onBack = onBack
            )
        }
    }

    companion object {

        /**
         * Creates a metadata map used to flag a [NavEntry] as a [ModalBottomSheet].
         *
         * This helper function should be used when defining a navigation destination's
         * metadata. By including the resulting map in the [NavEntry], the
         * [BottomSheetSceneStrategy] will recognize the destination and wrap its
         * content in a bottom sheet instead of a standard full-screen scene.
         *
         * @param modalBottomSheetProperties Configuration for the sheet's behavior,
         * such as whether it can be dismissed with a tap outside, its visual appearance,
         * or input handling. Defaults to standard [ModalBottomSheetProperties].
         * @return A map containing the [BOTTOM_SHEET_KEY] associated with the
         * provided [ModalBottomSheetProperties], ready to be passed to a [NavEntry].
         */
        @OptIn(ExperimentalMaterial3Api::class)
        fun bottomSheet(
            modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties()
        ): Map<String, Any> = mapOf(BOTTOM_SHEET_KEY to modalBottomSheetProperties)

        internal const val BOTTOM_SHEET_KEY = "bottomsheet"
    }
}