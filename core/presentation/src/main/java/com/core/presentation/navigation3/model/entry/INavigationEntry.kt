package com.core.presentation.navigation3.model.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.lifecycle.ViewModel
import com.core.presentation.navigation3.manager.INavigationGraphManager
import com.core.presentation.navigation3.model.destination.INavigationDestination
import dagger.Lazy
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

/**
 * A base contract for defining a navigation destination's UI and business logic.
 *
 * This abstract class bridges a specific [INavigationDestination] (the route data)
 * with its corresponding [ViewModel] (the state) and its Composable representation.
 * It uses reflection to automatically resolve the class types of the destination
 * and the ViewModel provided in the generic parameters.
 *
 * ### Responsibilities:
 * - **Type Resolution:** Automatically extracts [DESTINATION] and [VM] types for the navigation factory
 *   to identify routes and instantiate ViewModels.
 * - **UI Rendering:** Defines the [Render] contract that binds destination data and state to the UI.
 * - **Navigation Access:** Provides access to the [INavigationGraphManager] for triggering
 *   navigation actions (push, pop, etc.).
 *
 * @param DESTINATION The type of [INavigationDestination] this entry handles (the Route).
 * @param VM The type of [ViewModel] associated with this destination's lifecycle.
 */
abstract class INavigationEntry<out DESTINATION : INavigationDestination, out VM: ViewModel> {

    /**
     * A lazy-initialized reference to the [INavigationGraphManager].
     *
     * This property provides the entry with the ability to trigger navigation actions
     * (push, pop, etc.) within the application's navigation hierarchy. By using [dagger.Lazy],
     * the manager is only instantiated when a navigation action is actually performed,
     * preventing circular dependencies or premature initialization during the entry's construction.
     */
    abstract val navigator: Lazy<INavigationGraphManager>

    /**
     * Resolves the [KClass] of the [DESTINATION] type handled by this navigation entry.
     *
     * This property uses reflection to extract the first type argument ([DESTINATION]) defined in the
     * class signature. The resolved class serves as the unique identifier for the route,
     * allowing the navigation factory to map an incoming [INavigationDestination]
     * instance to this specific entry's [Render] logic.
     *
     * @return The [KClass] of the destination type [DESTINATION].
     */
    val destination: KClass<out DESTINATION> by lazy {
        extractTypeArgument(index = 0)
    }

    /**
     * Resolves the [KClass] of the [ViewModel] associated with this navigation entry.
     *
     * This property uses reflection to extract the second type argument ([VM]) defined in the
     * class signature. The resolved class is used by the navigation factory to
     * automatically instantiate the correct [ViewModel] and scope it to the
     * navigation backstack or graph.
     *
     * @return The [KClass] of the [ViewModel] type [VM].
     */
    val viewModel: KClass<out VM> by lazy {
        extractTypeArgument(index = 1)
    }

    /**
     * Defines the UI composition logic for this navigation entry.
     *
     * This function is invoked by the navigation runtime whenever the associated [DESTINATION]
     * is at the top of the backstack. It provides the actual [Composable] content for the screen.
     *
     * @param params The specific instance of the [DESTINATION] used to reach this entry,
     * typically containing serialized navigation arguments (e.g., IDs, flags).
     * @param viewModel The resolved [VM] instance scoped to this destination,
     * providing the necessary state and business logic for the UI.
     */
    @Composable
    abstract fun Render(
        params: @UnsafeVariance DESTINATION,
        viewModel: @UnsafeVariance VM
    )

    /**
     * Extracts the [KClass] of a generic type argument from the class hierarchy using reflection.
     *
     * This function traverses the inheritance tree of the current instance to resolve the
     * concrete classes assigned to [DESTINATION] or [VM]. It is designed to handle multi-level
     * inheritance (e.g., when an entry extends an intermediate base class like `INavigationEntryDialog`)
     * and [ParameterizedType]s.
     *
     * ### Process:
     * 1. Starts from the runtime class and moves up the [superclass] chain.
     * 2. Identifies the [ParameterizedType] that implements or extends [INavigationEntry].
     * 3. Resolves the type argument at the specified [index].
     * 4. Supports standard [Class] types and [ParameterizedType]s (extracting the raw class).
     *
     * @param DESTINATION The target type to cast the resulting class to.
     * @param index The position of the type argument (0 for Destination, 1 for ViewModel).
     * @return The [KClass] representing the resolved generic type.
     * @throws NoSuchElementException If the class hierarchy is exhausted without finding
     * a valid [INavigationEntry] type argument.
     */
    @Suppress("UNCHECKED_CAST")
    private fun <DESTINATION : Any> extractTypeArgument(index: Int): KClass<DESTINATION> {
        var currentClass: Class<*>? = this::class.java

        while (currentClass != null && currentClass != Any::class.java) {
            val genericSuper = currentClass.genericSuperclass
            if (genericSuper is ParameterizedType) {
                val rawType = genericSuper.rawType as Class<*>
                if (INavigationEntry::class.java.isAssignableFrom(rawType)) {
                    val typeArg = genericSuper.actualTypeArguments[index]
                    if (typeArg is Class<*>) {
                        return typeArg.kotlin as KClass<DESTINATION>
                    }

                    if (typeArg is ParameterizedType) {
                        return (typeArg.rawType as Class<*>).kotlin as KClass<DESTINATION>
                    }
                }
            }

            currentClass = currentClass.superclass
        }

        throw NoSuchElementException("Generic type not found")
    }
}