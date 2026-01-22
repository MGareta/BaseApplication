package com.core.presentation.navigation3.factory

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.scene.DialogSceneStrategy
import com.core.presentation.navigation3.model.entry.INavigationEntry
import com.core.presentation.navigation3.model.graph.INavigationGraph
import com.core.presentation.navigation3.model.destination.INavigationDestination
import com.core.presentation.navigation3.model.entry.scenes.BottomSheetSceneStrategy
import com.core.presentation.navigation3.model.entry.type.INavigationEntryBottomSheet
import com.core.presentation.navigation3.model.entry.type.INavigationEntryDialog
import com.core.presentation.navigation3.model.graph.type.INavigationGraphLauncher

/**
 * The concrete implementation of [INavigationGraphFactory] that orchestrates the discovery
 * and registration of modular navigation components.
 *
 * This class serves as the engine for the navigation system, utilizing Hilt-injected maps
 * of [graphs] and [destinations] to build a cohesive navigation structure. It bridges
 * the gap between decentralized feature module declarations and the centralized
 * [androidx.navigation3] runtime.
 *
 * ### Performance:
 * It utilizes [lazy] initialization for [navigationGraphsWithDestinations] and
 * [navigationGraphLauncher] to ensure that heavy join operations and validation checks
 * are only performed once the navigation system is actually initialized at runtime.
 *
 * @property destinations A map of entries provided via Hilt multibindings, keyed by their
 *   implementing class type.
 * @property graphs A map of navigation graphs provided via Hilt multibindings, used
 *   to group and manage associated entries.
 */
internal class NavigationGraphFactoryImpl(
    private val destinations: Map<Class<*>, @JvmSuppressWildcards Set<INavigationEntry<INavigationDestination, ViewModel>>>,
    private val graphs: Map<Class<*>, @JvmSuppressWildcards INavigationGraph>,
) : INavigationGraphFactory {

    /**
     * Lazily computes the mapping between [INavigationGraph] instances and their associated
     * [INavigationEntry] sets.
     *
     * This property performs a join operation between the [graphs] registry and the
     * [destinations] registry, matching them by their shared class key. It ensures that
     * only complete pairs (where both a graph definition and at least one destination entry
     * exist for the same class key) are included in the final map.
     *
     * ### Logic:
     * 1. Merges keys from both [destinations] and [graphs] maps.
     * 2. Iterates through the keys to find matches in both registries.
     * 3. Filters out incomplete pairings where either the graph or the entry set is missing.
     * 4. Returns a finalized [Map] used by [register] to populate the navigation scope.
     *
     * @return A consolidated map used to coordinate feature-specific navigation logic.
     */
    override val navigationGraphsWithDestinations: Map<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>> by lazy {
        destinations.keys
            .plus(
                elements = graphs.keys
            )
            .mapNotNull { key ->
                val graph = graphs[key]
                val entrySet = destinations[key]
                if (graph != null && entrySet != null) {
                    graph to entrySet
                } else {
                    null
                }
            }.toMap()
    }

    /**
     * Identifies and provides the application's starting navigation graph configuration.
     *
     * This property scans the registered [navigationGraphsWithDestinations] to find the
     * specific graph marked as a [INavigationGraphLauncher]. This entry serves as the
     * root or "home" flow of the application.
     *
     * ### Validation Rules:
     * - **Must exist:** Throws an [IllegalStateException] if no graph implements [INavigationGraphLauncher].
     * - **Must be unique:** Throws an [IllegalStateException] if multiple graphs implement [INavigationGraphLauncher],
     *   ensuring a deterministic start point for the app.
     *
     * @return A [Map.Entry] containing the [INavigationGraph] launcher and its set of
     * associated [INavigationEntry] destinations.
     * @throws IllegalStateException if the launcher graph is missing or duplicated.
     */
    override val navigationGraphLauncher: Map.Entry<INavigationGraph, Set<INavigationEntry<INavigationDestination, ViewModel>>> by lazy {
        val launcherGraphs =  navigationGraphsWithDestinations.entries.filter {
            it.key is INavigationGraphLauncher
        }

        check(launcherGraphs.isNotEmpty()) {
            "Launcher navigation graph is not set. Please implement ${INavigationGraphLauncher::class.simpleName}" +
                    " in one ${INavigationGraph::class.simpleName}"
        }

        check(launcherGraphs.size == 1) {
            "Launcher navigation graph must be 1 and only 1. Please remove duplicated uses of ${INavigationGraphLauncher::class.simpleName}"
        }

        launcherGraphs.first()
    }

    /**
     * Registers all collected navigation destinations into the [EntryProviderScope].
     *
     * This implementation iterates through the flattened set of [INavigationEntry] objects
     * from all valid navigation graphs. For each entry, it calls [EntryProviderScope.addEntryProvider],
     * which defines how the Navigation3 runtime should handle a specific [INavigationDestination].
     *
     * ### Registration Process:
     * 1. **Destination Mapping:** Binds the [INavigationEntry.destination] class to the provider.
     * 2. **Metadata Assignment:** Checks if the entry is a specialized type (e.g., [INavigationEntryBottomSheet]
     *   or [INavigationEntryDialog]) and attaches the appropriate [BottomSheetSceneStrategy] or
     *   [DialogSceneStrategy] metadata.
     * 3. **Lifecycle Management:** Automatically instantiates the associated [ViewModel] using the
     *   Compose [viewModel] factory, ensuring the ViewModel is correctly scoped.
     * 4. **Composition:** Invokes the [INavigationEntry.Render] function to provide the Composable UI, passing
     *   the typed destination parameters and the resolved ViewModel.
     *
     * @param scope The current navigation provider scope responsible for coordinating UI composition
     * based on the active backstack.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    override fun register(scope: EntryProviderScope<INavigationDestination>) {
        navigationGraphsWithDestinations.values.flatten().forEach { entry ->
            scope.addEntryProvider(
                clazz = entry.destination,
                clazzContentKey = {
                    it::class.qualifiedName.toString()
                },
                metadata = when (entry) {
                    is INavigationEntryBottomSheet -> BottomSheetSceneStrategy.bottomSheet(
                        modalBottomSheetProperties = entry.modalBottomSheetProperties
                    )
                    is INavigationEntryDialog -> DialogSceneStrategy.dialog(
                        dialogProperties = entry.dialogProperties
                    )
                    else -> emptyMap()
                },
                content = { destination ->
                    val viewModel = viewModel(
                        modelClass = entry.viewModel.java
                    )

                    entry.Render(
                        params = destination,
                        viewModel = viewModel
                    )
                }
            )
        }
    }
}
