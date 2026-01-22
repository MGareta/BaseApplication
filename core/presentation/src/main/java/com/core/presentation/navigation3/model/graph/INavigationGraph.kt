package com.core.presentation.navigation3.model.graph

/**
 * A base abstraction representing a logical grouping of navigation destinations.
 *
 * In a modularized architecture, an [INavigationGraph] defines a boundary for a
 * specific feature flow or sub-section of the application (e.g., Login Flow,
 * Settings Flow). It acts as a container that groups related [com.core.presentation.navigation3.model.entry.INavigationEntry]
 * objects together.
 *
 * ### Architectural Role:
 * - **Modularization:** Allows feature modules to define their own internal navigation
 *   structure independently.
 * - **Scoping:** Serves as a key for managing shared state (ViewModels) across multiple
 *   screens within the same graph.
 * - **Identification:** Used by the [com.core.presentation.navigation3.factory.INavigationGraphFactory]
 *   to resolve and register groups of destinations during application startup.
 *
 * Implementations are typically defined as Kotlin `object`s or `class`es and are
 * registered via Hilt multibindings.
 */
abstract class INavigationGraph