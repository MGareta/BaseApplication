package com.core.data.security.cryptography

import com.core.data.network.model.ApiResult

interface ICryptographyType {

    suspend fun encryptData(
        data: String,
        publicKey: String
    ): ApiResult<String>

    suspend fun encryptData(
        data: String
    ): ApiResult<String>

    suspend fun decryptData(
        data: String,
        publicKey: String
    ): ApiResult<String>

    suspend fun decryptData(
        data: String
    ): ApiResult<String>
}
