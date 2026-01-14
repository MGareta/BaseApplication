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

/**
 * A custom trust manager for network connections.
 *
 * This class provides two trust managers:
 * - [secureTrustManager]: A trust manager that validates server certificates against a provided keystore.
 *   It also performs basic validity checks on the certificates.
 * - [insecureTrustManager]: A trust manager that trusts all server certificates without validation.
 *   **Use with extreme caution, as it bypasses all security checks.**
 *
 * The class also contains a [whitelist] of hostnames.
 *
 * @property Context The application context.
 */
class NetworkTrustManager(context: Context) {
    /**
     * A list of strings representing a whitelist.
     *
     * This property defines a set of allowed values. Any value not present in this
     * list should be considered outside the whitelist.
     */
    val whitelist = listOf<String>()

    /**
     * A custom [X509TrustManager] that performs server certificate validity checks
     * in addition to relying on the underlying trust manager's verification.
     *
     * This trust manager ensures that:
     *   1. Server certificates are within their validity period.
     *   2. The certificate chain can be trusted based on the provided keystore (if any).
     *
     * The `checkServerTrusted` method iterates through the provided server certificate chain and calls
     * `checkValidity()` on each certificate, throwing an exception if any certificate is expired or not yet valid.
     * The `checkClientTrusted` and `getAcceptedIssuers` methods are delegated to the underlying system trust manager,
     * which manages client-side trust and the list of accepted certificate issuers.
     *
     * **Note:** This trust manager defaults to an empty keystore meaning that it relies on the system's default trust store for certificate chain verification,
     * and it only performs the extra validity check. If custom CA certificates are needed to be trusted, add them to the `certificates` map as a key-value pair
     * where key is an alias of the certificate and value is the `InputStream` for the certificate data.
     *
     * @property secureTrustManager An instance of [X509TrustManager] configured for server certificate validity checks.
     */
    val secureTrustManager: X509TrustManager by lazy {
        val certificates = emptyMap<String, InputStream>()

        val keyStore = with(CertificateFactory.getInstance("X.509")) {
            KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                certificates.forEach {
                    setCertificateEntry(it.key, generateCertificate(it.value))
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
                trustManager.checkClientTrusted(certs, authType)
            }

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                trustManager.checkServerTrusted(certs, authType)

                for (certificate in certs) {
                    certificate.checkValidity()
                }
            }
        }
    }

    /**
     * An insecure [X509TrustManager] that trusts all certificates without validation.
     *
     * **WARNING:** This trust manager should be used with **extreme caution** as it disables all certificate
     * verification and makes your app vulnerable to man-in-the-middle attacks.
     * It should **never** be used in production environments.
     *
     * This trust manager is intended for testing and development purposes only, where bypassing certificate
     * validation might be necessary.
     *
     * **How it works:**
     * This trust manager implements the [X509TrustManager] interface but does not perform any actual
     * certificate validation. It simply returns an empty array for `getAcceptedIssuers` and does nothing
     * in `checkClientTrusted` and `checkServerTrusted`.
     *
     * **Alternatives:**
     * For secure communication, use a proper trust manager that validates certificates against a trusted
     * certificate authority (CA). Consider using the `secureTrustManager` provided in this class or
     * another secure implementation.
     *
     * @see secureTrustManager
     */
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
