package com.core.data.security

import android.util.Base64
import com.core.data.BaseTest
import com.core.data.network.model.ApiResult
import com.core.data.security.cryptography.ICryptographyType
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.manager.SecurityManagerImpl
import com.core.data.security.model.CipherType
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.KeyPairGenerator
import javax.inject.Inject

private const val AES_KEY = "0123456789abcdef"
private const val AES_DATA = "Test AES encryption"
private const val RSA_DATA = "Test RSA encryption"

@HiltAndroidTest
class SecurityManagerTest : BaseTest() {

    @Inject
    lateinit var securityManager: ISecurityManager

    override fun initDependencies() = Unit

    @Test
    fun encryptsAndDecryptsAesData() = test {
        val encryptedResult = securityManager.encryptData(
            type = CipherType.AES,
            data = AES_DATA,
            publicKey = AES_KEY
        )

        assertTrue(encryptedResult is ApiResult.Success)

        val decryptedResult = securityManager.decryptData(
            type = CipherType.AES,
            data = (encryptedResult as ApiResult.Success).data,
            publicKey = AES_KEY
        )

        assertEquals(ApiResult.Success(data = AES_DATA), decryptedResult)
    }

    @Test
    fun encryptsAndDecryptsRsaData() = test {
        val keyPair = KeyPairGenerator.getInstance("RSA").run {
            initialize(2048)
            generateKeyPair()
        }

        val publicKey = Base64.encodeToString(keyPair.public.encoded, 0)
        val privateKey = Base64.encodeToString(keyPair.private.encoded, 0)

        val encryptedResult = securityManager.encryptData(
            type = CipherType.RSA,
            data = RSA_DATA,
            publicKey = publicKey
        )

        assertTrue(encryptedResult is ApiResult.Success)

        val decryptedResult = securityManager.decryptData(
            type = CipherType.RSA,
            data = (encryptedResult as ApiResult.Success).data,
            publicKey = privateKey
        )

        assertEquals(ApiResult.Success(data = RSA_DATA), decryptedResult)
    }

    @Test
    fun encryptsAndDecryptsAesDataWithoutPublicKey() = test {
        val cryptography = mockk<ICryptographyType>()

        val manager = SecurityManagerImpl(
            cryptography = mapOf(CipherType.AES to cryptography)
        )

        val encryptedData = "encrypted AES data"

        coEvery {
            cryptography.encryptData(data = AES_DATA)
        } returns ApiResult.Success(data = encryptedData)
        coEvery {
            cryptography.decryptData(data = encryptedData)
        } returns ApiResult.Success(data = AES_DATA)

        assertEquals(
            ApiResult.Success(data = encryptedData),
            manager.encryptData(
                type = CipherType.AES,
                data = AES_DATA,
                publicKey = null
            )
        )

        assertEquals(
            ApiResult.Success(data = AES_DATA),
            manager.decryptData(
                type = CipherType.AES,
                data = encryptedData,
                publicKey = null
            )
        )

        coVerify(exactly = 1) {
            cryptography.encryptData(data = AES_DATA)
        }

        coVerify(exactly = 1) {
            cryptography.decryptData(data = encryptedData)
        }
    }

    @Test
    fun encryptsAndDecryptsRsaDataWithoutPublicKey() = test {
        val cryptography = mockk<ICryptographyType>()

        val manager = SecurityManagerImpl(
            cryptography = mapOf(CipherType.RSA to cryptography)
        )

        val encryptedData = "encrypted RSA data"

        coEvery {
            cryptography.encryptData(data = RSA_DATA)
        } returns ApiResult.Success(data = encryptedData)

        coEvery {
            cryptography.decryptData(data = encryptedData)
        } returns ApiResult.Success(data = RSA_DATA)

        assertEquals(
            ApiResult.Success(data = encryptedData),
            manager.encryptData(
                type = CipherType.RSA,
                data = RSA_DATA,
                publicKey = null
            )
        )

        assertEquals(
            ApiResult.Success(data = RSA_DATA),
            manager.decryptData(
                type = CipherType.RSA,
                data = encryptedData,
                publicKey = null
            )
        )

        coVerify(exactly = 1) {
            cryptography.encryptData(data = RSA_DATA)
        }

        coVerify(exactly = 1) {
            cryptography.decryptData(data = encryptedData)
        }
    }
}
