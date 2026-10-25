package com.core.data.network.ssl

import android.annotation.SuppressLint
import android.security.keystore.KeyProperties
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext

@SuppressLint("CustomX509TrustManager")
fun OkHttpClient.Builder.disableSSLPinning(
    networkTrustManager: NetworkTrustManager
): OkHttpClient.Builder {
    val naiveTrustManager = networkTrustManager.insecureTrustManager
    val insecureSocketFactory = SSLContext
        .getInstance(TlsVersion.TLS_1_3.javaName)
        .apply {
            val trustAllCerts = arrayOf(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

    sslSocketFactory(
        sslSocketFactory = insecureSocketFactory,
        trustManager = naiveTrustManager,
    )

    hostnameVerifier(
        hostnameVerifier = { _, _ -> true },
    )

    return this
}

@Suppress("UNCHECKED_CAST")
fun OkHttpClient.Builder.enableSSLPinning(
    networkTrustManager: NetworkTrustManager
): OkHttpClient.Builder {
    val secureTrustManager = networkTrustManager.secureTrustManager
    val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_3.javaName).apply {
        init(
            null,
            arrayOf(secureTrustManager),
            SecureRandom()
        )
    }

    sslSocketFactory(
        sslSocketFactory = sslContext.socketFactory,
        trustManager = secureTrustManager,
    )

    hostnameVerifier { hostname, session ->
        try {
            secureTrustManager.checkServerTrusted(
                session.peerCertificates as Array<X509Certificate>,
                KeyProperties.KEY_ALGORITHM_RSA,
            )

            networkTrustManager.whitelist.contains(hostname)
        } catch (e: Exception) {
            false
        }
    }

    return this
}
