package com.core.data.security.manager

import com.core.data.network.model.ApiResult
import com.core.data.security.cryptography.ICryptographyType
import com.core.data.security.model.CipherType

class SecurityManagerImpl(
    private val cryptography: Map<@JvmSuppressWildcards CipherType, @JvmSuppressWildcards ICryptographyType>,
) : ISecurityManager {
    /**
     * **How it works:**
     * This function utilizes a cryptography implementation based on the provided [type].
     *
     * The function first checks if a cryptography implementation exists for the given [CipherType].
     * If not, it returns an [ApiResult.Exception] with a [NoSuchElementException].
     *
     * The actual encryption process is delegated to the cryptography implementation associated with the [CipherType].
     */
    override suspend fun encryptData(type: CipherType, data: String, publicKey: String?): ApiResult<String> {
        val cryptographyType = cryptography[type] ?: return ApiResult.Exception(
            exception = NoSuchElementException("Cryptography ${type.name} does not exist"),
        )

        with(cryptographyType) {
            val encryptedData = if (publicKey != null) {
                encryptData(
                    data = data,
                    publicKey = publicKey,
                )
            } else {
                encryptData(
                    data = data,
                )
            }

            return encryptedData
        }
    }

    /**
     * **How it works:**
     * This function handles decryption of data based on the provided [CipherType]. It utilizes the
     * appropriate cryptography implementation associated with the specified type.
     * If a public key is provided, it performs public-key decryption; otherwise, it performs decryption without a public key
     * (e.g., using a shared secret or other method determined by the cryptography type).
     */
    override suspend fun decryptData(type: CipherType, data: String, publicKey: String?): ApiResult<String> {
        val cryptographyType = cryptography[type] ?: return ApiResult.Exception(
            exception = NoSuchElementException("Cryptography ${type.name} does not exist"),
        )

        with(cryptographyType) {
            val encryptedData = if (publicKey != null) {
                decryptData(
                    data = data,
                    publicKey = publicKey,
                )
            } else {
                decryptData(
                    data = data,
                )
            }

            return encryptedData
        }
    }
}
