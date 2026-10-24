package com.core.data.cache.scopes

import com.core.data.cache.model.config.ICacheConfig
import com.core.data.network.model.ApiResult
import kotlin.reflect.KClass

interface ICacheScope {

    suspend fun putData(
        config: ICacheConfig,
        data: Any
    )

    suspend fun <T : Any> getData(
        key: String,
        kClass: KClass<T>
    ): ApiResult<T>

    suspend fun deleteData(
        key: String
    )

    suspend fun clearAll()
}
