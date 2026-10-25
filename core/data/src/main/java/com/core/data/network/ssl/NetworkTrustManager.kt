package com.core.data.network.ssl

import android.annotation.SuppressLint
import android.content.Context
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class NetworkTrustManager(
    context: Context
) {

    val whitelist = listOf<String>()

    val secureTrustManager: X509TrustManager by lazy {
        val certificates = emptyMap<String, InputStream>()

        val keyStore = with(CertificateFactory.getInstance("X.509")) {
            KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)

                certificates.forEach {
                    setCertificateEntry(
                        it.key,
                        generateCertificate(it.value)
                    )
                }
            }
        }

        val trustManager: X509TrustManager by lazy {
            TrustManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm())
                .apply {
                    init(keyStore)
                }.trustManagers[0] as X509TrustManager
        }

        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = trustManager.acceptedIssuers

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                trustManager.checkClientTrusted(
                    certs,
                    authType
                )
            }

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                trustManager.checkServerTrusted(
                    certs,
                    authType
                )

                for (certificate in certs) {
                    certificate.checkValidity()
                }
            }
        }
    }

    val insecureTrustManager: X509TrustManager by lazy {

        @SuppressWarnings("kotlin:S4830")
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }
    }
}
