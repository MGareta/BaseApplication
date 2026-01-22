package com.core.presentation.navigation3.model.destination

import androidx.navigation3.runtime.NavKey

/**
 * Represents a unique destination or route within the navigation system.
 * This class serves as the base contract for all serializable navigation routes.
 * It extends [NavKey] to integrate with the Navigation3 runtime, allowing instances
 * of this class to be used as keys within backstacks and entry providers.
 *
 * ### Architectural Role:
 * - **Type-Safety:** By extending [NavKey], it enables type-safe navigation and argument
 *   passing across feature modules.
 * - **Serialization:** Implementations should typically be marked with `@Serializable`
 *   to support state restoration and deep linking.
 * - **Key Identification:** Instances of this class are used by the [com.core.presentation.navigation3.factory.INavigationGraphFactory]
 *   to resolve which UI ([com.core.presentation.navigation3.model.entry.INavigationEntry]) should be rendered.
 *
 * @see androidx.navigation3.runtime.NavKey
 */
abstract class INavigationDestination : NavKey