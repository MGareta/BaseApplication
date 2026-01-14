package com.core.data.security

import android.security.keystore.KeyProperties
import android.util.Base64
import com.core.data.BaseTest
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.security.cryptography.aes.CryptographyAesType
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.manager.SecurityManagerImpl
import com.core.data.security.model.CipherType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val DATA = "Test AES encryption"

class AesSecurityManagerTest : BaseTest() {
    private lateinit var securityManager: ISecurityManager
    private lateinit var secretKey: SecretKey

    override fun initDependencies() {
        securityManager = SecurityManagerImpl(
            cryptography = mapOf(Pair(CipherType.AES, CryptographyAesType())),
        )

        secretKey = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES).run {
            init(128)
            generateKey()
        }
    }

    @Test
    fun encryptDataWithPublicKey() {
        test {
            val result = securityManager.encryptData(
                type = CipherType.AES,
                data = DATA,
                publicKey = Base64.encodeToString(secretKey.encoded, 0),
            )

            assert(result is ApiResult.Success)
        }
    }

    @Test
    fun decryptDataWithPublicKey() {
        test {
            val encodedKey = Base64.encodeToString(secretKey.encoded, 0)

            val encryptedData = securityManager
                .encryptData(
                    type = CipherType.AES,
                    data = DATA,
                    publicKey = encodedKey,
                ).getOrNull()

            assertNotNull(encryptedData)

            val result = securityManager.decryptData(
                type = CipherType.AES,
                data = encryptedData!!,
                publicKey = encodedKey,
            )

            assert(result is ApiResult.Success)
            assertEquals((result as ApiResult.Success).data, DATA)
        }
    }
}
