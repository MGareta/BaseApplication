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

private val Context.dataStore by preferencesDataStore(
    name = CACHE_FILENAME
)

class CacheDiskScope(
    @param:ApplicationContext private val context: Context,
    private val json: Json,
    private val securityManager: ISecurityManager
) : ICacheScope {

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override suspend fun putData(
        config: ICacheConfig,
        data: Any
    ) {
        val encryptedData = securityManager
            .encryptData(
                type = CipherType.AES,
                data = json.encodeToString(
                    serializer = DynamicLookupSerializer(),
                    value = data
                ),
                publicKey = null
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
                    publicKey = null
                ).getOrNull() ?: return

            val encryptedKey = securityManager
                .encryptData(
                    type = CipherType.AES,
                    data = config.key,
                    publicKey = null
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

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> getData(
        key: String,
        kClass: KClass<T>
    ): ApiResult<T> {
        val preferences = context.dataStore.data
            .first()
            .toPreferences()

        val encryptedKey = preferences.asMap().keys.firstOrNull {
            val decryptedKey = runBlocking {
                securityManager
                    .decryptData(
                        type = CipherType.AES,
                        data = it.name,
                        publicKey = null
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
                publicKey = null
            ).getOrNull() ?: return ApiResult.NoContent()

        val cacheEntry = json.decodeFromString(
            deserializer = CacheEntry::class.serializer(),
            string = decryptedEntry
        )

        return if (cacheEntry.hasExpired()) {
            ApiResult.NoContent()
        } else {
            val decryptedData = securityManager
                .decryptData(
                    type = CipherType.AES,
                    data = cacheEntry.value,
                    publicKey = null
                ).getOrNull() ?: return ApiResult.NoContent()

            ApiResult.Success(
                data = json.decodeFromString(
                    deserializer = kClass.serializer(),
                    string = decryptedData,
                )
            )
        }
    }

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
                        publicKey = null
                    ).getOrNull()
            }

            decryptedKey?.isNotBlank() == true && decryptedKey == key
        }

        if (encryptedKey != null) {
            context.dataStore.edit {
                it.remove(
                    key = encryptedKey
                )
            }
        }
    }


    override suspend fun clearAll() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
