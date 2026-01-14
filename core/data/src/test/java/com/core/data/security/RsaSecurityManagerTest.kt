package com.core.data.security

import android.security.keystore.KeyProperties
import android.util.Base64
import com.core.data.BaseTest
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.security.cryptography.rsa.CryptographyRsaType
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.manager.SecurityManagerImpl
import com.core.data.security.model.CipherType
import junit.framework.TestCase.assertNotNull
import org.junit.Assert
import org.junit.Test
import java.security.KeyPair
import java.security.KeyPairGenerator

private const val DATA = "Test RSA encryption"

class RsaSecurityManagerTest : BaseTest() {
    private lateinit var securityManager: ISecurityManager
    private lateinit var keyPair: KeyPair

    override fun initDependencies() {
        securityManager = SecurityManagerImpl(
            cryptography = mapOf(Pair(CipherType.RSA, CryptographyRsaType())),
        )

        keyPair = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA).run {
            initialize(1024)
            genKeyPair()
        }
    }

    @Test
    fun encryptDataWithPublicKey() {
        test {
            val result = securityManager.encryptData(
                type = CipherType.RSA,
                data = DATA,
                publicKey = Base64.encodeToString(keyPair.public.encoded, 0),
            )

            assert(result is ApiResult.Success)
        }
    }

    @Test
    fun decryptDataWithPrivateKey() {
        test {
            val encryptedData = securityManager
                .encryptData(
                    type = CipherType.RSA,
                    data = DATA,
                    publicKey = Base64.encodeToString(keyPair.public.encoded, 0),
                ).getOrNull()

            assertNotNull(encryptedData)

            val result = securityManager.decryptData(
                type = CipherType.RSA,
                data = encryptedData!!,
                publicKey = Base64.encodeToString(keyPair.private.encoded, 0),
            )

            assert(result is ApiResult.Success)
            Assert.assertEquals((result as ApiResult.Success).data, DATA)
        }
    }
}
