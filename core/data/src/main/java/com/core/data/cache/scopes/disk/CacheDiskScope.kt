package com.core.data.cache.scopes.disk

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.core.data.cache.model.CacheEntry
import com.core.data.cache.model.config.ICacheConfig
import com.core.data.cache.model.config.ImmortalCacheConfig
import com.core.data.cache.model.config.TimeToLiveCacheConfig
import com.core.data.cache.scopes.ICacheScope
import com.core.data.cache.utils.DynamicLookupSerializer
import com.core.data.cache.utils.hasExpired
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.model.CipherType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.time.Duration

private const val CACHE_FILENAME = "cache"

private val Context.dataStore by preferencesDataStore(name = CACHE_FILENAME)

/**
 * `CacheDiskScope` is a concrete implementation of the `ICacheScope` interface
 * that provides a persistent, disk-based caching mechanism using Android's
 * DataStore Preferences. It encrypts and decrypts cached data for security
 * using an `ISecurityManager` instance.
 *
 * This class utilizes AES encryption to safeguard the data stored on disk.
 * It supports different cache configurations, including time-to-live (TTL)
 * and immortal caches. Data is stored as encrypted JSON strings within the
 * DataStore.
 *
 * @property context The application context used for accessing the DataStore.
 * @property json The `Json` instance used for encoding and decoding data to/from JSON.
 * @property securityManager The `ISecurityManager` instance used for encrypting and decrypting data.
 *
 * @constructor Creates a `CacheDiskScope` instance.
 */
class CacheDiskScope(
    @param:ApplicationContext private val context: Context,
    private val json: Json,
    private val securityManager: ISecurityManager,
) : ICacheScope {
    /**
     * **How it works:**
     * This function performs the following steps:
     * 1. Encrypts the provided `data` using AES encryption.
     * 2. Determines the cache entry duration based on the provided `config`.
     *    - If `config` is an [ImmortalCacheConfig], the duration is set to [Duration.INFINITE].
     *    - If `config` is a [TimeToLiveCacheConfig], the duration is calculated from `config.duration` and `config.durationUnit`.
     *    - Otherwise, the duration defaults to [Duration.INFINITE].
     * 3. Creates a [CacheEntry] object containing the encrypted data and the calculated duration.
     * 4. Encrypts the [CacheEntry] object using AES encryption.
     * 5. Encrypts the cache key (`config.key`) using AES encryption.
     * 6. Creates a [stringPreferencesKey] using the encrypted key.
     * 7. Edits the DataStore:
     *    - If an entry with the generated key already exists, it's removed.
     *    - The encrypted [CacheEntry] is stored under the generated key.
     */
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun putData(config: ICacheConfig, data: Any) {
        val encryptedData = securityManager
            .encryptData(
                type = CipherType.AES,
                data = json.encodeToString(
                    serializer = DynamicLookupSerializer(),
                    value = data,
                ),
                publicKey = null,
            ).getOrNull() ?: return

        val duration = when (config) {
            is ImmortalCacheConfig -> Duration.INFINITE
            is TimeToLiveCacheConfig -> config.duration
            else -> Duration.INFINITE
        }

        CacheEntry(
            value = encryptedData,
            duration = duration,
        ).run {
            val encryptedEntry = securityManager
                .encryptData(
                    type = CipherType.AES,
                    data = json.encodeToString(value = this),
                    publicKey = null,
                ).getOrNull() ?: return

            val encryptedKey = securityManager
                .encryptData(
                    type = CipherType.AES,
                    data = config.key,
                    publicKey = null,
                ).getOrNull() ?: return

            val preferencesKey = stringPreferencesKey(name = encryptedKey)
            context.dataStore.edit {
                if (it.contains(key = preferencesKey)) {
                    it.remove(key = preferencesKey)
                }

                it[preferencesKey] = encryptedEntry
            }
        }
    }

    /**
     * **How it works:**
     * This function attempts to retrieve data of the specified type (`kClass`) from the cache using the provided key.
     * It performs the following steps:
     * 1. Retrieves the DataStore preferences.
     * 2. Searches for the encrypted key corresponding to the provided key.
     * 3. If the encrypted key is found:
     *     - Retrieves the encrypted cache entry associated with the key.
     *     - Decrypts the cache entry.
     *     - Decodes the cache entry into a `CacheEntry` object.
     *     - Checks if the cache entry has expired.
     *     - If the entry has not expired:
     *         - Decrypts the cached data.
     *         - Decodes the decrypted data into an object of the specified type (`kClass`).
     *         - Returns the data wrapped in an `ApiResult.Success`.
     * 4. If the encrypted key is not found or the cache entry has expired, returns `ApiResult.NoContent`.
     */
    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> getData(key: String, kClass: KClass<T>): ApiResult<T> {
        val preferences = context.dataStore.data
            .first()
            .toPreferences()

        val encryptedKey = preferences.asMap().keys.firstOrNull {
            val decryptedKey = runBlocking {
                securityManager
                    .decryptData(
                        type = CipherType.AES,
                        data = it.name,
                        publicKey = null,
                    ).getOrNull()
            }

            decryptedKey?.isNotBlank() == true && decryptedKey == key
        }

        if (encryptedKey == null) {
            return ApiResult.NoContent()
        }

        val encryptedEntry = preferences[encryptedKey] ?: return ApiResult.NoContent()
        val decryptedEntry = securityManager
            .decryptData(
                type = CipherType.AES,
                data = encryptedEntry as String,
                publicKey = null,
            ).getOrNull() ?: return ApiResult.NoContent()

        val cacheEntry = json.decodeFromString(
            deserializer = CacheEntry::class.serializer(),
            string = decryptedEntry,
        )

        return if (cacheEntry.hasExpired()) {
            ApiResult.NoContent()
        } else {
            val decryptedData = securityManager
                .decryptData(
                    type = CipherType.AES,
                    data = cacheEntry.value,
                    publicKey = null,
                ).getOrNull() ?: return ApiResult.NoContent()

            ApiResult.Success(
                data = json.decodeFromString(
                    deserializer = kClass.serializer(),
                    string = decryptedData,
                ),
            )
        }
    }

    /**
     * **How it works:**
     * This function securely deletes data stored in the data store that is associated
     * with the provided `key`. The key itself is assumed to have been previously
     * encrypted before being stored. This function decrypts each key in the datastore
     * until it find the one matching the provided `key`. Then, it removes the
     * associated encrypted key-value pair from the data store.
     */
    override suspend fun deleteData(key: String) {
        val preferences = context.dataStore.data
            .first()
            .toPreferences()

        val encryptedKey = preferences.asMap().keys.firstOrNull {
            val decryptedKey = runBlocking {
                securityManager
                    .decryptData(
                        type = CipherType.AES,
                        data = it.name,
                        publicKey = null,
                    ).getOrNull()
            }

            decryptedKey?.isNotBlank() == true && decryptedKey == key
        }

        if (encryptedKey != null) {
            context.dataStore.edit {
                it.remove(key = encryptedKey)
            }
        }
    }

    /**
     * **How it works:**
     * This function removes all key-value pairs from the underlying storage.
     * After calling this function, the DataStore will be empty.
     */
    override suspend fun clearAll() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
