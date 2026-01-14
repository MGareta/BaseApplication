package com.core.data.security.cryptography.aes

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.core.data.network.model.ApiResult
import com.core.data.security.cryptography.ICryptographyType
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val ALIAS = "alias"
private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
private const val IV_LENGTH_BYTES = 12
private const val TAG_LENGTH_BYTES = 16

class CryptographyAesType : ICryptographyType {
    /**
     * The cipher instance used for encryption and decryption.
     *
     * This property is lazily initialized, meaning the Cipher object is only created
     * when it's first accessed. It utilizes the transformation specified by [TRANSFORMATION].
     * Subsequent accesses will return the same initialized instance.
     *
     * @see TRANSFORMATION
     */
    private val cipher by lazy {
        Cipher.getInstance(TRANSFORMATION)
    }

    /**
     * [KeyStore] instance for interacting with the Android KeyStore system.
     *
     * This property is lazily initialized, meaning the [KeyStore] instance is only created
     * when it is accessed for the first time. Subsequent accesses will return the same
     * instance.
     *
     * The `AndroidKeyStore` type is used, which provides secure storage for cryptographic keys
     * within the Android operating system.
     *
     * The [KeyStore] is loaded with `null` as the parameter, which means it is initialized
     * with the default settings and does not require a password or input stream.
     *
     * @see KeyStore
     * @see <a href="https://developer.android.com/training/articles/keystore">Android KeyStore System</a>
     */
    private val keyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }
    }

    /**
     * **How it works:**
     * This function performs the following steps:
     * 1. Initializes the cipher in ENCRYPT_MODE with a secret key derived from the provided `publicKey`.
     * 2. Encrypts the input data (converted to bytes) using the cipher.
     * 3. Encodes the encrypted data using Base64 encoding.
     * 4. Returns the Base64-encoded encrypted data within an ApiResult.Success.
     *
     * **Security Considerations:**
     *  - **Key Derivation:** In this implementation, the public key is directly used as the secret key and for IV generation, which is **highly discouraged** in production environments.  A proper key derivation function (KDF) should be used to generate the secret key and IV from the public key.
     *  - **Public Key as Secret:**  Treating the public key as a secret key significantly reduces security.  Public keys are meant to be shared, and using it as a secret compromises the entire security model.  This is likely not a true public key, but rather a pre-shared secret.
     *  - **IV Reuse:** Deriving the IV from the key can lead to IV reuse if the same key is used multiple times, especially if the encryption process is done deterministically. IV reuse is a serious security vulnerability in GCM mode.
     *
     * **Intended Use:**
     * This implementation seems intended for cases where a simple pre-shared secret is used for symmetric encryption/decryption, likely NOT a true public-private key pair scenario.
     *
     * @see cipher
     */
    override suspend fun encryptData(data: String, publicKey: String): ApiResult<String> {
        val encryptedData = cipher.run {
            val ivParameterSpec = IvParameterSpec(publicKey.toByteArray(StandardCharsets.UTF_8))
            val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BYTES * 8, ivParameterSpec.iv)

            val secretKeySpec = SecretKeySpec(
                publicKey.toByteArray(StandardCharsets.UTF_8),
                ALGORITHM,
            )

            init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec)

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT),
        )
    }

    /**
     * **How it works:**
     * This function performs the following steps:
     * 1. Initializes the cipher in ENCRYPT_MODE with a secret key.
     * 2. Encrypts the input data (converted to bytes) using the cipher.
     * 3. Prepends the Initialization Vector (IV) used during encryption to the encrypted data.
     * 4. Encodes the combined IV and encrypted data using Base64 encoding.
     * 5. Returns the Base64-encoded encrypted data within an ApiResult.Success.
     *
     * @see cipher
     * @see getKey
     */
    override suspend fun encryptData(data: String): ApiResult<String> {
        val encryptedData = cipher.run {
            init(Cipher.ENCRYPT_MODE, getKey())
            doFinal(data.toByteArray())
        }

        val encryptedDataWithIv = ByteBuffer
            .allocate(IV_LENGTH_BYTES + encryptedData.size)
            .put(cipher.iv)
            .put(encryptedData)
            .array()

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedDataWithIv, Base64.DEFAULT),
        )
    }

    /**
     * **How it works:**
     * This function decrypts a Base64 encoded string using AES in GCM mode.
     * It uses the provided `publicKey` as the secret key for decryption,
     * and also derives the Initialization Vector (IV) from the public key.
     *
     * **Security Considerations:**
     *  - **Key Derivation:** In this implementation, the public key is directly used as the secret key and for IV generation, which is **highly discouraged** in production environments.  A proper key derivation function (KDF) should be used to generate the secret key and IV from the public key.
     *  - **Public Key as Secret:**  Treating the public key as a secret key significantly reduces security.  Public keys are meant to be shared, and using it as a secret compromises the entire security model.  This is likely not a true public key, but rather a pre-shared secret.
     *  - **IV Reuse:** Deriving the IV from the key can lead to IV reuse if the same key is used multiple times, especially if the encryption process is done deterministically. IV reuse is a serious security vulnerability in GCM mode.
     *
     * **Intended Use:**
     * This implementation seems intended for cases where a simple pre-shared secret is used for symmetric encryption/decryption, likely NOT a true public-private key pair scenario.
     *
     * @see cipher
     */
    override suspend fun decryptData(data: String, publicKey: String): ApiResult<String> {
        val decryptedData = cipher.run {
            val ivParameterSpec = IvParameterSpec(publicKey.toByteArray(StandardCharsets.UTF_8))
            val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BYTES * 8, ivParameterSpec.iv)
            val secretKeySpec = SecretKeySpec(
                publicKey.toByteArray(StandardCharsets.UTF_8),
                ALGORITHM,
            )

            init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec)

            val encryptedData = Base64.decode(data, Base64.DEFAULT)
            doFinal(encryptedData).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData,
        )
    }

    /**
     * **How it works:**
     * This function takes a Base64 encoded string containing encrypted data (along with its Initialization Vector)
     * and decrypts it using AES/GCM/NoPadding with a derived secret key.
     *
     * The encrypted data is expected to be structured as follows:
     *   - The first `IV_LENGTH_BYTES` bytes represent the Initialization Vector (IV).
     *   - The remaining bytes represent the actual encrypted data.
     *
     * The decryption process involves the following steps:
     *   1. Decode the Base64 encoded input string.
     *   2. Extract the IV from the beginning of the decoded data.
     *   3. Create a `GCMParameterSpec` using the extracted IV and the tag length.
     *   4. Retrieve the secret key using the `getKey()` function.
     *   5. Initialize the `Cipher` in decryption mode using the secret key and the `GCMParameterSpec`.
     *   6. Extract the encrypted data (excluding the IV) from the decoded data.
     *   7. Perform the decryption using `doFinal()`.
     *   8. Decode the decrypted bytes to a UTF-8 string.
     *   9. Return the decrypted string wrapped in an `ApiResult.Success`.
     */
    override suspend fun decryptData(data: String): ApiResult<String> {
        val decryptedData = cipher.run {
            val encryptedDataWithIV = Base64.decode(data, Base64.DEFAULT)
            with(ByteBuffer.wrap(encryptedDataWithIV)) {
                val ivParameterSpec = IvParameterSpec(encryptedDataWithIV.copyOfRange(0, IV_LENGTH_BYTES))
                val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BYTES * 8, ivParameterSpec.iv)
                val secretKey = getKey()

                init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

                val encryptedDataWithoutIV = encryptedDataWithIV.copyOfRange(IV_LENGTH_BYTES, encryptedDataWithIV.size)
                doFinal(encryptedDataWithoutIV).decodeToString()
            }
        }

        return ApiResult.Success(
            data = decryptedData,
        )
    }

    /**
     * Retrieves the secret key from the KeyStore.
     *
     * This function attempts to retrieve an existing secret key from the KeyStore using the predefined alias.
     * If a key with the specified alias exists, it is returned. If no such key exists, a new key is
     * generated, stored in the KeyStore, and then returned.
     *
     * @return The secret key, either retrieved from the KeyStore or newly generated.
     *
     * @throws Exception if there is an error accessing the keystore or creating the key
     *
     * @see createKey
     */
    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    /**
     * Creates a new secret key for AES encryption and decryption.
     *
     * This function generates a new AES key and stores it in the Android KeyStore.
     * The key is generated using the following parameters:
     * - Alias: [ALIAS]
     * - Algorithm: [ALGORITHM] (AES)
     * - Block mode: [BLOCK_MODE] (GCM)
     * - Padding: [PADDING] (None)
     * - User authentication: Not required
     * - Randomized encryption: Required
     *
     * The generated key is intended for use with the [cipher] instance.
     *
     * @return The newly generated secret key.
     * @throws Exception if there is an error during key generation or storage.
     *
     * @see ALIAS
     * @see ALGORITHM
     * @see BLOCK_MODE
     * @see PADDING
     * @see cipher
     */
    private fun createKey(): SecretKey = KeyGenerator
        .getInstance(
            ALGORITHM,
            ANDROID_KEY_STORE,
        ).run {
            init(
                KeyGenParameterSpec
                    .Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )

            generateKey()
        }
}
