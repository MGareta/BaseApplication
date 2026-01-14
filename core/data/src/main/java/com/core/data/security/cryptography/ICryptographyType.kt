package com.core.data.security.cryptography

import com.core.data.network.model.ApiResult

interface ICryptographyType {
    /**
     * Encrypts the provided data using the given public key.
     *
     * This function performs asynchronous encryption of the input string `data` using the
     * provided `publicKey`. The encryption process is handled within a coroutine, allowing
     * it to be called from other suspending functions or coroutine scopes.
     *
     * @param data The string data to be encrypted.
     * @param publicKey The public key used for encryption.
     * @return An [ApiResult] containing the encrypted data as a String if successful, or an error
     *         if the encryption process failed.
     *
     * Example Usage:
     * ```
     * val result = encryptData("MySecretData", "myPublicKey")
     * when(result){
     *     is ApiResult.Success -> {
     *         println("Encrypted data: ${result.data}")
     *     }
     *     is ApiResult.Error -> {
     *         println("Encryption failed: ${result.exception.message}")
     *     }
     * }
     * ```
     */
    suspend fun encryptData(data: String, publicKey: String): ApiResult<String>

    /**
     * Encrypts the provided data string.
     *
     * This function takes a plain text string as input and returns an encrypted
     * string wrapped in an [ApiResult]. The encryption process is handled
     * internally and may involve various cryptographic techniques.
     *
     * Note that this function is a suspend function, indicating it may perform
     * long-running operations asynchronously and should be called within a
     * coroutine scope.
     *
     * @param data The plain text string to be encrypted.
     * @return An [ApiResult] containing the encrypted string if successful, or an
     *         error state if the encryption process fails.
     *         - [ApiResult.Success]: Contains the encrypted data as a [String].
     *         - [ApiResult.Failure]: Indicates an error occurred during encryption.
     *           The specific error can be determined by inspecting the `exception`
     *           or `errorCode` property of the [ApiResult.Failure] object.
     *
     * @sample
     *   // Example of how to use encryptData:
     *   GlobalScope.launch {
     *       val result = encryptData("My secret data")
     *       when (result) {
     *           is ApiResult.Success -> {
     *               println("Encrypted data: ${result.data}")
     *           }
     *           is ApiResult.Failure -> {
     *               println("Encryption failed: ${result.exception?.message ?: result.errorCode}")
     *           }
     *       }
     *   }
     */
    suspend fun encryptData(data: String): ApiResult<String>

    /**
     * Decrypts data using a provided public key.
     *
     * This function is a suspend function, meaning it should be called within a coroutine scope.
     * It takes encrypted data as a string and a public key as a string, and attempts to decrypt
     * the data using the provided key.  It handles potential errors during the decryption process
     * and returns an [ApiResult] indicating success or failure.
     *
     * **Important Security Considerations:**
     *
     *   - **Public Key Usage:** This function's name implies the use of a public key for decryption,
     *     which is **highly unusual and incorrect** in standard cryptography. Typically, public keys
     *     are used for *encryption*, and private keys are used for *decryption*.  Using a public
     *     key for decryption would mean anyone could decrypt the data, defeating the purpose of
     *     encryption.
     *   - **Asymmetric vs. Symmetric:** The code context suggests an intention of using asymmetric
     *     cryptography (public/private key pair). If the true intention is to decrypt, you should
     *     be using a *private key* and likely perform *encryption* elsewhere using the public key.
     *   - **Key Management:** The security of this function entirely depends on the secure management
     *     of the keys (likely a private key that is not shown here). Never hardcode keys directly in
     *     your source code. Implement a robust key management system.
     *
     * @param data The encrypted data to be decrypted, represented as a String. This should
     *             typically be a Base64 or similar encoded string.
     * @param publicKey The public key, represented as a String.  **Again, be very cautious about the correct use of public/private keys.**
     * @return An [ApiResult] that either contains the decrypted data as a String
     */
    suspend fun decryptData(data: String, publicKey: String): ApiResult<String>

    /**
     * Decrypts the provided data string.
     *
     * This function takes an encrypted data string as input and attempts to decrypt it.
     *
     * This is a suspending function, meaning it should be called within a coroutine or
     * another suspending function.
     *
     * @param data The encrypted data string to decrypt.
     * @return An [ApiResult] containing either the decrypted data string on success,
     *         or an error indicating the reason for decryption failure.
     *         - On success: [ApiResult.Success] containing the decrypted string.
     *         - On failure: [ApiResult.Error] containing an error code and/or message.
     * @throws Exception if any unexpected error occurs during the decryption process. This is in addition to the error states included in the ApiResult.
     *
     * Example:
     * ```kotlin
     *  val encryptedData = "someEncryptedString"
     *  val result = decryptData(encryptedData)
     *  when (result) {
     *      is ApiResult.Success -> {
     *          val decryptedString = result.data
     *          println("Decrypted data: $decryptedString")
     *      }
     *      is ApiResult.Error -> {
     *          println("Decryption failed with error: ${result.errorCode} - ${result.errorMessage}")
     *      }
     *  }
     * ```
     */
    suspend fun decryptData(data: String): ApiResult<String>
}
