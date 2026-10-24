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
        val encryptedData = cipher.run {
            val ivParameterSpec = IvParameterSpec(publicKey.toByteArray(StandardCharsets.UTF_8))
            val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BYTES * 8, ivParameterSpec.iv)

            val secretKeySpec = SecretKeySpec(
                publicKey.toByteArray(StandardCharsets.UTF_8),
                ALGORITHM
            )

            init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec)

            doFinal(data.toByteArray())
        }

        return ApiResult.Success(
            data = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        )
    }

    override suspend fun encryptData(
        data: String
    ): ApiResult<String> {
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
            data = Base64.encodeToString(encryptedDataWithIv, Base64.DEFAULT)
        )
    }

    override suspend fun decryptData(
        data: String,
        publicKey: String
    ): ApiResult<String> {
        val decryptedData = cipher.run {
            val ivParameterSpec = IvParameterSpec(publicKey.toByteArray(StandardCharsets.UTF_8))
            val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BYTES * 8, ivParameterSpec.iv)
            val secretKeySpec = SecretKeySpec(
                publicKey.toByteArray(StandardCharsets.UTF_8),
                ALGORITHM
            )

            init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec)

            val encryptedData = Base64.decode(data, Base64.DEFAULT)
            doFinal(encryptedData).decodeToString()
        }

        return ApiResult.Success(
            data = decryptedData
        )
    }

    override suspend fun decryptData(
        data: String
    ): ApiResult<String> {
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
            data = decryptedData
        )
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

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
                    .build()
            )

            generateKey()
        }
}
