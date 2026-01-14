package com.core.data.security.manager

import com.core.data.network.model.ApiResult
import com.core.data.security.model.CipherType

interface ISecurityManager {
    /**
     * Encrypts the provided data using the specified cipher type.
     *
     * This function encrypts the given string data using either symmetric or asymmetric encryption,
     * depending on the provided `CipherType`. If asymmetric encryption is selected (`CipherType.RSA`),
     * a public key is required.
     *
     * @param type The type of cipher to use for encryption (e.g., `CipherType.AES` for symmetric, `CipherType.RSA` for asymmetric).
     * @param data The string data to be encrypted.
     * @param publicKey The public key used encryption.
     * @return An [ApiResult] containing the encrypted data as a string if successful, or an error if the encryption fails.
     *         - On success: `ApiResult.Success(encryptedString)` where `encryptedString` is the base64 encoded encrypted data.
     *         - On failure: `ApiResult.Failure(exception)` where `exception` indicates the cause of the failure.
     *
     * @throws NoSuchElementException if the specified [CipherType] does not have a corresponding cryptography implementation.
     * @throws Exception if any other error occurs during the encryption process.
     *
     * Example Usage:
     * ```kotlin
     * // Symmetric encryption (AES)
     * val resultAES = encryptData(CipherType.AES, "my secret data", null)
     * when (resultAES) {
     *     is ApiResult.Success -> println("Encrypted AES data: ${resultAES.data}")
     *     is ApiResult.Failure -> println("AES Encryption failed: ${resultAES.exception}")
     * }
     *
     * // Asymmetric encryption (RSA)
     * val resultRSA = encryptData(CipherType.RSA, "my secret data", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...")
     * when (resultRSA) {
     *     is ApiResult.Success -> println("Encrypted RSA data: ${result ${resultRSA.data}")
     *     is ApiResult.Failure -> println("RSA Encryption failed: ${resultRSA.exception}")
     * }
     */
    suspend fun encryptData(type: CipherType, data: String, publicKey: String?): ApiResult<String>

    /**
     * Decrypts data based on the specified cipher type.
     *
     * This function performs decryption of a provided string using either RSA or AES algorithms.
     * It handles the complexity of selecting the right cipher based on the provided [CipherType].
     * For RSA, it requires a public key. For AES, it expects the key to be managed internally.
     *
     * @param type The type of cipher to use for decryption ([CipherType.RSA] or [CipherType.AES]).
     * @param data The encrypted data string to decrypt.
     * @param publicKey The public key to use for RSA decryption. This parameter is only required if [type] is [CipherType.RSA].
     *                  If [type] is [CipherType.AES], this parameter is ignored and should be passed as null.
     * @return An [ApiResult] object containing either the decrypted string on success or an error in case of failure.
     *         - [ApiResult.Success]: Contains the decrypted string.
     *         - [ApiResult.Failure]: Contains an exception indicating the reason for decryption failure.
     *
     * @throws NoSuchElementException if the specified [CipherType] does not have a corresponding cryptography implementation.
     * @throws Exception if any error occurs during the decryption process.
     *
     * @sample
     * ```kotlin
     * // Example of decrypting data with RSA
     * val decryptedDataRSA = decryptData(CipherType.RSA, encryptedData, "your_public_key_here")
     * if (decryptedDataRSA is ApiResult.Success) {
     *     println("Decrypted data (RSA): ${decryptedDataRSA.data}")
     * } else if (decryptedDataRSA is ApiResult.Failure) {
     *     println("Decryption failed (RSA): ${decryptedDataRSA.exception}")
     * }
     *
     * // Example of decrypting data with AES
     * val decryptedDataAES = decryptData(CipherType.AES, encryptedData, null)
     * if (decryptedDataAES is ApiResult.Success) {
     *     println("Decrypted data (AES): ${decryptedDataAES.data}")
     * } else if (decryptedDataAES is ApiResult. */
    suspend fun decryptData(type: CipherType, data: String, publicKey: String?): ApiResult<String>
}
