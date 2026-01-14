package com.core.data.network.ssl

import android.annotation.SuppressLint
import android.security.keystore.KeyProperties
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext

/**
 * Disables SSL pinning for the OkHttpClient.
 *
 * This function configures the OkHttpClient to trust any certificate, effectively disabling SSL pinning.
 * It should only be used in development or testing environments, and **never in production**.
 * Disabling SSL pinning opens up the application to man-in-the-middle attacks.
 *
 * This function does the following:
 * 1. Creates an insecure `SSLSocketFactory` using a custom `TrustManager` that trusts all certificates.
 * 2. Sets this insecure `SSLSocketFactory` on the `OkHttpClient.Builder`.
 * 3. Sets a `HostnameVerifier` that accepts any hostname.
 *
 * @param networkTrustManager The `NetworkTrustManager` instance containing the insecure trust manager to use.
 *                             This should be an instance that trusts all certificates.
 * @return The modified `OkHttpClient.Builder` instance with SSL pinning disabled.
 *
 * @throws Exception If there is an issue creating the SSL context or socket factory.
 * @SuppressLint("CustomX509TrustManager") Suppresses the warning about using a custom TrustManager. This is intentional for disabling SSL pinning.
 * @see NetworkTrustManager.insecureTrustManager
 * @see javax.net.ssl.TrustManager
 * @see javax.net.ssl.SSLSocketFactory
 * @see okhttp3.OkHttpClient.Builder
 */
@SuppressWarnings("kotlin:S5527")
@SuppressLint("CustomX509TrustManager")
fun OkHttpClient.Builder.disableSSLPinning(networkTrustManager: NetworkTrustManager): OkHttpClient.Builder {
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

/**
 * Enables SSL Pinning for the OkHttpClient.
 *
 * This function configures the OkHttpClient to perform SSL pinning, ensuring that
 * only connections to servers with specific, trusted certificates are allowed. It uses
 * a custom [NetworkTrustManager] to manage the trust anchors and certificate validation.
 *
 * **How it works:**
 * 1. **Creates an SSL Context:** It initializes an SSL context using the provided
 *    [NetworkTrustManager]'s secure trust manager.
 * 2. **Sets SSL Socket Factory:** It configures the OkHttpClient to use the newly
 *    created SSL socket factory, which is responsible for establishing secure connections.
 * 3. **Sets Hostname Verifier:** It configures a custom hostname verifier that performs
 *    two crucial checks:
 *    - **Certificate Chain Validation:** It verifies that the server's certificate chain
 *      is trusted by the provided [NetworkTrustManager]'s secure trust manager using `checkServerTrusted`.
 *    - **Hostname Whitelisting:** It checks if the server's hostname is present in the
 *      [NetworkTrustManager]'s whitelist. Only whitelisted hostnames are allowed.
 * 4. **Error Handling:** If any exception occurs during the certificate validation, the
 *    hostname verification fails.
 *
 * **Requirements:**
 * - A properly configured [NetworkTrustManager] instance, which contains the set of
 *   trusted certificates and the hostname whitelist.
 * - The [NetworkTrustManager.secureTrustManager] must be a valid `X509TrustManager`.
 *
 * **Usage:**
 * ```kotlin
 * val networkTrustManager = NetworkTrustManager(...) // Initialize your NetworkTrustManager
 * val client = OkHttpClient.Builder()
 *     .enableSSLPinning(networkTrustManager)
 *     .build()
 * ```
 *
 * **Note:**
 * - This function overrides the default SSL socket factory and hostname verifier
 *   of the OkHttpClient.
 * - Any errors during certificate verification will be caught and treated as a
 *   failed hostname verification.
 */
@Suppress("UNCHECKED_CAST")
fun OkHttpClient.Builder.enableSSLPinning(networkTrustManager: NetworkTrustManager): OkHttpClient.Builder {
    val secureTrustManager = networkTrustManager.secureTrustManager
    val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_3.javaName).apply {
        init(null, arrayOf(secureTrustManager), SecureRandom())
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
