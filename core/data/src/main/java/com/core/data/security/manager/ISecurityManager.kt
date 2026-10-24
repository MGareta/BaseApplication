package com.core.data.security.manager

import com.core.data.network.model.ApiResult
import com.core.data.security.model.CipherType

interface ISecurityManager {

    suspend fun encryptData(
        type: CipherType,
        data: String,
        publicKey: String?
    ): ApiResult<String>

    suspend fun decryptData(
        type: CipherType,
        data: String,
        publicKey: String?
    ): ApiResult<String>
}
