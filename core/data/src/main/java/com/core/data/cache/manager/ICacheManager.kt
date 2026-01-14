package com.core.data.cache.manager

import com.core.data.cache.model.CacheScope
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

interface ICacheManager {
    /**
     * Caches the provided data based on the given scope and configuration.
     *
     * This function is responsible for storing the provided `data` in a cache
     * identified by the `scope` and configured using the `config`. The specific
     * caching mechanism and storage location are determined by the implementation
     * of the `ICacheConfig` interface.
     *
     * This function is a suspending function, meaning it can be safely called
     * within a coroutine and will suspend execution until the caching operation
     * is complete.
     *
     * @param scope The scope under which the data should be cached. This typically
     *              represents a logical grouping or category for the cached data.
     *              See [CacheScope] for available options.
     * @param config The configuration object that defines how the data should be
     *               cached. This includes details like the storage location,
     *               expiration policies, and data serialization/deserialization
     *               methods. Must implement [ICacheConfig].
     * @param data The data to be cached. This can be any type of object, but its
     *             serialization and deserialization must be handled by the provided
     *             [ICacheConfig].
     *
     * @see CacheScope
     * @see ICacheConfig
     */
    suspend fun cacheData(scope: CacheScope, config: ICacheConfig, data: Any)

    /**
     * Retrieves data from the cache.
     *
     * This function attempts to retrieve data associated with a specific key from the
     * provided cache scope.
     *
     * The retrieved or fetched data is then returned wrapped in an [ApiResult] object.
     *
     * @param T The type of data to retrieve. Must be a non-nullable class.
     * @param scope The [CacheScope] defining the scope of the cache (e.g., memory, disk).
     * @param key The key used to identify the data within the cache.
     * @param kClass The KClass of the data type [T], used for deserialization if data is fetched from the network.
     * @return An [ApiResult] object containing either the retrieved data ([ApiResult.Success]) or an error indication ([ApiResult.Error]).
     *
     * @throws IllegalArgumentException if the key is empty or blank
     *
     * Example usage:
     * ```kotlin
     * val myData: ApiResult<MyDataClass> = getData(MemoryCache, "my_data_key", MyDataClass::class)
     * when (myData) {
     *     is ApiResult.Success -> {
     *         println("Successfully retrieved data: ${myData.data}")
     *     }
     *     is ApiResult.Error -> {
     *         println("Error retrieving data: ${myData.exception}")
     *     }
     * }
     * ```
     */
    suspend fun <T : Any> getData(scope: CacheScope, key: String, kClass: KClass<T>): ApiResult<T>

    /**
     * Deletes data associated with the given key from the cache.
     *
     * This function removes the cached data identified by the provided `key`.
     * If a `scope` is specified, the deletion will be limited to that scope.
     * If no scope is provided (null), the data will be deleted globally across all scopes.
     *
     * Note: This is a suspending function, meaning it should be called from within a coroutine.
     *
     * @param scope The optional scope within which to delete the data.
     *              If null, data is deleted globally.
     * @param key   The unique key identifying the data to be deleted.
     * @throws Exception if there is any issue while deleting the data.
     */
    suspend fun deleteData(scope: CacheScope?, key: String)

    /**
     * Deletes a specific cache scope.
     *
     * This function is responsible for removing all cached data associated with the given [scope].
     * It's a suspending function, meaning it can be safely called from coroutines and will suspend
     * execution until the deletion process is complete.
     *
     * @param scope The [CacheScope] to delete. This determines which set of cached data will be removed.
     *
     * @throws Exception If an error occurs during the deletion process. The specific type of exception
     *                  will depend on the underlying cache implementation.
     *
     * @see CacheScope
     */
    suspend fun deleteScope(scope: CacheScope)

    /**
     * Clears all data associated with the current scope or context.
     *
     * This function is a suspend function, indicating that it may perform
     * long-running or asynchronous operations and should be called within a
     * coroutine or another suspend function.
     *
     * Depending on the specific implementation, "all data" might include:
     * - Cached information
     * - User-specific preferences
     * - Temporary files or resources
     * - State held within the current scope
     * - Data stored in a database or other persistent storage.
     *
     * **Note:** The exact behavior of this function depends on where it is used.
     * It's crucial to understand the specific context to determine which data is cleared.
     *
     * **Example Use Cases:**
     * - Resetting the state of an application or a specific feature.
     * - Logging out a user and clearing their session data.
     * - Removing cached data to ensure fresh information is loaded.
     * - Cleaning up temporary resources after a task is completed.
     *
     * **Thread Safety:**
     * The thread safety of this function depends on the specific implementation and the
     * data structures it manipulates. If it modifies shared mutable state, it must
     * be implemented in a thread-safe manner.
     */
    suspend fun clearAll()
}
