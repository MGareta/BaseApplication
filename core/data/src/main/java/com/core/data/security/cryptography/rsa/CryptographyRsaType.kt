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

    private val cipher by lazy {
        Cipher.getInstance(TRANSFORMATION)
    }

    private val keyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE).apply {
            load(null)
        }
    }

    override suspend fun encryptData(
        data: String,
        publicKey: String
    ): ApiResult<String> {
        val key = decodePublicKey(publicKey = publicKey)
        val encryptedData = cipher.run {
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
            )

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        )
    }

    override suspend fun encryptData(
        data: String
    ): ApiResult<String> {
        val key = getKey().public
        val encryptedData = cipher.run {
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
            )

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        )
    }

    override suspend fun decryptData(
        data: String,
        publicKey: String
    ): ApiResult<String> {
        val key = decodePrivateKey(privateKey = publicKey)
        val decryptedData = cipher.run {
            init(
                Cipher.DECRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
            )

            doFinal(Base64.decode(data, Base64.DEFAULT)).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData,
        )
    }

    override suspend fun decryptData(
        data: String
    ): ApiResult<String> {
        val key = getKey().private
        val decryptedData = cipher.run {
            init(
                Cipher.DECRYPT_MODE,
                key,
                OAEPParameterSpec(
                    DIGEST,
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
            )

            doFinal(Base64.decode(data, Base64.DEFAULT)).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData
        )
    }

    private fun decodePublicKey(
        publicKey: String
    ): PublicKey {
        val decodedKey = Base64.decode(publicKey.trim(), Base64.DEFAULT)
        val encodedPublicKey = X509EncodedKeySpec(decodedKey)

        return KeyFactory.getInstance(ALGORITHM).run {
            generatePublic(encodedPublicKey)
        }
    }

    private fun decodePrivateKey(
        privateKey: String
    ): PrivateKey {
        val decodedKey = Base64.decode(privateKey.trim(), Base64.DEFAULT)
        val encodedPrivateKey = PKCS8EncodedKeySpec(decodedKey)

        return KeyFactory.getInstance(ALGORITHM).run {
            generatePrivate(encodedPrivateKey)
        }
    }

    private fun getKey(): KeyPair {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.PrivateKeyEntry

        return if (existingKey != null) {
            KeyPair(existingKey.certificate.publicKey, existingKey.privateKey)
        } else {
            createKey()
        }
    }

    private fun createKey(): KeyPair = KeyPairGenerator
        .getInstance(
            ALGORITHM,
            ANDROID_KEY_STORE
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
                    .build()
            )

            generateKeyPair()
        }
}
