package com.core.data.security.manager

import com.core.data.network.model.ApiResult
import com.core.data.security.cryptography.ICryptographyType
import com.core.data.security.model.CipherType

class SecurityManagerImpl(
    private val cryptography: Map<@JvmSuppressWildcards CipherType, @JvmSuppressWildcards ICryptographyType>
) : ISecurityManager {

    override suspend fun encryptData(
        type: CipherType,
        data: String,
        publicKey: String?
    ): ApiResult<String> {
        val cryptographyType = cryptography[type] ?: return ApiResult.Exception(
            exception = NoSuchElementException("Cryptography ${type.name} does not exist")
        )

        with(cryptographyType) {
            val encryptedData = if (publicKey != null) {
                encryptData(
                    data = data,
                    publicKey = publicKey
                )
            } else {
                encryptData(
                    data = data
                )
            }

            return encryptedData
        }
    }

    override suspend fun decryptData(
        type: CipherType,
        data: String,
        publicKey: String?
    ): ApiResult<String> {
        val cryptographyType = cryptography[type] ?: return ApiResult.Exception(
            exception = NoSuchElementException("Cryptography ${type.name} does not exist")
        )

        with(cryptographyType) {
            val encryptedData = if (publicKey != null) {
                decryptData(
                    data = data,
                    publicKey = publicKey
                )
            } else {
                decryptData(
                    data = data
                )
            }

            return encryptedData
        }
    }
}
