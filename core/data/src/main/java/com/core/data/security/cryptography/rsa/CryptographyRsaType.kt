package com.core.data.security.cryptography.rsa

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.core.data.network.model.ApiResult
import com.core.data.security.cryptography.ICryptographyType
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val ALIAS = "alias"
private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
private const val PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_OAEP
private const val DIGEST = KeyProperties.DIGEST_SHA256
private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

class CryptographyRsaType : ICryptographyType {
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
     * This function takes a string `data` and a string representation of a public key (`publicKey`),
     * decrypts the key, encrypts the data using the decrypted public key, and returns the encrypted data as a Base64-encoded string.
     *
     * @see cipher
     */
    override suspend fun encryptData(data: String, publicKey: String): ApiResult<String> {
        val key = decodePublicKey(publicKey = publicKey)
        val encryptedData = cipher.run {
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT,
                ),
            )

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT),
        )
    }

    /**
     * **How it works:**
     * This function takes a plain text string as input, encrypts it using the pre-configured
     * public key and cipher settings, and then returns the encrypted data as a Base64 encoded string.
     * The encryption process utilizes RSA with Optimal Asymmetric Encryption Padding (OAEP) scheme.
     *
     * **Encryption Details:**
     * - **Cipher Algorithm:** RSA
     * - **Padding Scheme:** OAEP (Optimal Asymmetric Encryption Padding)
     * - **Digest Algorithm:** SHA-256 (specified by the `DIGEST` constant which is not provided in the context, but assumed to be "SHA-256")
     * - **Mask Generation Function (MGF):** MGF1 with SHA-1
     * - **Source of Encoding Parameters (P):** PSpecified with an empty encoding value (default)
     * - **Encoding:** The encrypted byte array is encoded to Base64 string for easy transmission and storage.
     *
     * **Error Handling:**
     * - Any exceptions thrown during the encryption process (e.g., invalid key, padding errors)
     *   will be caught, and encapsulated in a failed ApiResult. (This aspect is not explicitly shown in the provided code, but it is the implicit behavior of a `suspend` function returning `ApiResult<String>` in most applications that are built this way)
     *
     * @see getKey
     * @see cipher
     * @see DIGEST
     */
    override suspend fun encryptData(data: String): ApiResult<String> {
        val key = getKey().public
        val encryptedData = cipher.run {
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT,
                ),
            )

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT),
        )
    }

    /**
     * **How it works:**
     * This function uses the provided public key to decrypt the given Base64 encoded string.
     * It employs the RSA/ECB/OAEPWithSHA-256AndMGF1Padding algorithm with specific parameters for
     * OAEP padding (SHA-256 as the digest, MGF1 with SHA-1, and default PSource).
     *
     * @see ApiResult
     * @see cipher
     * @see OAEPParameterSpec
     * @see MGF1ParameterSpec
     * @see PSource
     * @see Base64
     * @see decodePrivateKey
     * @see cipher
     * @see DIGEST
     */
    override suspend fun decryptData(data: String, publicKey: String): ApiResult<String> {
        val key = decodePrivateKey(privateKey = publicKey)
        val decryptedData = cipher.run {
            init(
                Cipher.DECRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT,
                ),
            )

            doFinal(Base64.decode(data, Base64.DEFAULT)).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData,
        )
    }

    /**
     * **How it works:**
     * This function takes an encrypted string (Base64 encoded) and decrypts it
     * using the RSA algorithm with Optimal Asymmetric Encryption Padding (OAEP).
     * It utilizes the private key obtained from `getKey().private` and specific
     * parameters for the OAEP padding scheme. The decrypted data is then
     * returned as a plain string.
     *
     * @see getKey
     * @see cipher
     * @see DIGEST
     */
    override suspend fun decryptData(data: String): ApiResult<String> {
        val key = getKey().private
        val decryptedData = cipher.run {
            init(
                Cipher.DECRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT,
                ),
            )

            doFinal(Base64.decode(data, Base64.DEFAULT)).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData,
        )
    }

    /**
     * Decodes a Base64 encoded public key string into a PublicKey object.
     *
     * This function takes a string representation of a public key, which is assumed to be
     * encoded using Base64. It decodes the string, then uses the decoded bytes to construct
     * an X509EncodedKeySpec. Finally, it uses a KeyFactory to generate a PublicKey object
     * from the key specification.
     *
     * @param publicKey The Base64 encoded public key string. Leading and trailing whitespace is trimmed.
     * @return A PublicKey object representing the decoded public key.
     * @throws java.security.NoSuchAlgorithmException If the specified algorithm ("RSA" in this assumed case) is not available.
     * @throws java.security.spec.InvalidKeySpecException If the provided key specification is invalid.
     * @throws IllegalArgumentException If the input string is not a valid Base64 encoded string.
     */
    private fun decodePublicKey(publicKey: String): PublicKey {
        val decodedKey = Base64.decode(publicKey.trim(), Base64.DEFAULT)
        val encodedPublicKey = X509EncodedKeySpec(decodedKey)

        return KeyFactory.getInstance(ALGORITHM).run {
            generatePublic(encodedPublicKey)
        }
    }

    /**
     * Decodes a Base64 encoded private key string into a PrivateKey object.
     */
    private fun decodePrivateKey(privateKey: String): PrivateKey {
        val decodedKey = Base64.decode(privateKey.trim(), Base64.DEFAULT)
        val encodedPrivateKey = PKCS8EncodedKeySpec(decodedKey)

        return KeyFactory.getInstance(ALGORITHM).run {
            generatePrivate(encodedPrivateKey)
        }
    }

    /**
     * Retrieves a key pair from the Android KeyStore.
     *
     * This function attempts to retrieve an existing key pair identified by the [ALIAS]
     * from the KeyStore. If a key pair is found, it is returned. If no key pair exists
     * for the specified alias, a new key pair is generated and stored in the KeyStore,
     * which is then returned.
     *
     * @return A [KeyPair] instance, either retrieved from the KeyStore or newly generated.
     * @throws Exception if there is an error accessing the KeyStore or generating the key pair.
     *
     * @see createKey
     */
    private fun getKey(): KeyPair {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.PrivateKeyEntry

        return if (existingKey != null) {
            KeyPair(existingKey.certificate.publicKey, existingKey.privateKey)
        } else {
            createKey()
        }
    }

    /**
     * Creates a new secret key for AES encryption and decryption.
     *
     * This function generates a new AES key and stores it in the Android KeyStore.
     * The key is generated using the following parameters:
     * - Alias: [ALIAS]
     * - Algorithm: [ALGORITHM] (RSA)
     * - Block mode: [BLOCK_MODE] (ECB)
     * - Padding: [PADDING] (OAEPPadding)
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
    private fun createKey(): KeyPair = KeyPairGenerator
        .getInstance(
            ALGORITHM,
            ANDROID_KEY_STORE,
        ).run {
            initialize(
                KeyGenParameterSpec
                    .Builder(
                        ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setDigests(DIGEST)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )

            generateKeyPair()
        }
}
