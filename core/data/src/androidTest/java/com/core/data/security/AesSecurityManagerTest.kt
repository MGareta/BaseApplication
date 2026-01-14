package com.core.data.security

import com.core.data.BaseTest
import com.core.data.network.model.ApiResult
import com.core.data.network.utils.getOrNull
import com.core.data.security.manager.ISecurityManager
import com.core.data.security.model.CipherType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.junit4.MockKRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import javax.inject.Inject

private const val DATA = "Test AES encryption"

@HiltAndroidTest
class AesSecurityManagerTest : BaseTest() {
    @Inject
    lateinit var securityManager: ISecurityManager

    override val hiltRule: HiltAndroidRule
        get() = HiltAndroidRule(this)

    override val mockkRule: MockKRule
        get() = MockKRule(this)

    @Test
    fun encryptDataWithAndroidKeystore() {
        test {
            val result = securityManager.encryptData(
                type = CipherType.AES,
                data = DATA,
                publicKey = null,
            )

            assert(result is ApiResult.Success)
        }
    }

    @Test
    fun decryptDataWithAndroidKeystore() {
        test {
            val encryptedData = securityManager
                .encryptData(
                    type = CipherType.AES,
                    data = DATA,
                    publicKey = null,
                ).getOrNull()

            assertNotNull(encryptedData)

            val result = securityManager.decryptData(
                type = CipherType.AES,
                data = encryptedData!!,
                publicKey = null,
            )

            assert(result is ApiResult.Success)
            assertEquals((result as ApiResult.Success).data, DATA)
        }
    }
}
