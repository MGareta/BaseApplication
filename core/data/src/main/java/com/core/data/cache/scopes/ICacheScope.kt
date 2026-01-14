package com.core.data.cache.scopes

import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

interface ICacheScope {
    /**
     * Puts data into the cache associated with the given configuration.
     *
     * This function is a suspend function, meaning it can be paused and resumed
     * without blocking the main thread. It is intended for use with Kotlin coroutines.
     *
     * @param config The configuration for the cache entry. This determines the key
     *               and duration of the cache entry. It can be one of the following:
     *               - `ImmortalCacheConfig`: Indicates the data should be cached indefinitely.
     *               - `TimeToLiveCacheConfig`: Indicates the data should be cached for a
     *                 specific duration defined by `duration` and `durationUnit`.
     *               - Any other implementation of `ICacheConfig` will be treated as an Immortal Cache.
     * @param data The data to be stored in the cache. This can be any object.
     *             The implementation of the underlying cache will handle serialization
     *             if needed.
     *
     * @see ICacheConfig
     */
    suspend fun putData(config: ICacheConfig, data: Any)

    /**
     * Retrieves data associated with a specific key, deserializing it to the specified type.
     *
     * This function attempts to retrieve data identified by the given [key]. The data is expected to be
     * in a format that can be deserialized into an object of type [T]. The [kClass] parameter provides
     * the necessary runtime type information for deserialization.
     *
     * This function is `suspend`, indicating that it may perform long-running operations (e.g., network
     * requests or disk I/O) and should be called within a coroutine or another suspending function.
     *
     * @param T The type to which the data should be deserialized. Must be a non-nullable type.
     * @param key The unique identifier for the data to be retrieved.
     * @param kClass The KClass representing the type [T], used for deserialization.
     * @return An [ApiResult] object.
     *   - [ApiResult.Success] containing the deserialized data of type [T] if the operation is successful.
     *   - [ApiResult.Error] containing an [ApiException] describing the error if an exception occurs during the process.
     *     Possible reasons for error include:
     *       - Key not found.
     *       - Deserialization failure due to data format mismatch.
     *       - Network issues or other unexpected errors.
     *
     * @throws IllegalArgumentException if the provided key is blank.
     *
     * @sample
     * ```kotlin
     * // Example usage:
     * suspend fun fetchUserData(userId: String): ApiResult<User> {
     *   return getData(key = "user_$userId", kClass = User::class)
     * }
     *
     * data class User(val id: String, val name: String)
     *
     * // Example of how to handle the result
     * launch {
     *   val result = fetchUserData("123")
     *   when (result) {
     *     is ApiResult.Success -> {
     *       val user = result.data
     *       println("User name: ${user.name}")
     *     }
     *     is ApiResult.Error -> {
     *       println("Error fetching user: ${result.exception.message}")
     */
    suspend fun <T : Any> getData(key: String, kClass: KClass<T>): ApiResult<T>

    /**
     * Deletes data associated with a specific key from the data store.
     *
     * This function asynchronously removes the data stored under the given `key`.
     * If no data is associated with the provided key, this function will complete
     * without any effect.
     *
     * @param key The key identifying the data to be deleted. Must not be empty or null.
     */
    suspend fun deleteData(key: String)

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
     *
     * **Exceptions:**
     * This function might throw exceptions depending on the specific implementation and
     * the operations performed during the clearing process.  Proper error handling
     * should be considered when calling this function.
     */
    suspend fun clearAll()
}
