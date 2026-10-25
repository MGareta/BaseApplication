package com.core.data.network

import com.core.data.BaseTest
import com.core.data.environment.di.EnvironmentModule
import com.core.data.environment.manager.IEnvironmentManager
import com.core.data.environment.model.Environment
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLSession

@HiltAndroidTest
@UninstallModules(EnvironmentModule::class)
class SSLPinningTest : BaseTest() {

    @BindValue
    @JvmField
    val environmentManager: IEnvironmentManager = mockk {
        every {
            environment
        } returns Environment.PROD()
    }

    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var httpLoggingInterceptor: HttpLoggingInterceptor

    override fun initDependencies() = Unit

    @Suppress("UNCHECKED_CAST")
    @Test
    fun productionEnvironmentEnablesSslPinning() = test {
        val session = mockk<SSLSession>()
        every {
            session.peerCertificates
        } returns (
            emptyArray<X509Certificate>() as Array<java.security.cert.Certificate>
        )

        assertNotNull(okHttpClient.sslSocketFactory)
        assertTrue(httpLoggingInterceptor in okHttpClient.interceptors)
        assertFalse(okHttpClient.hostnameVerifier.verify("api.example.test", session))
    }
}
