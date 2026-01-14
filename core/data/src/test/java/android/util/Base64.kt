package android.util

import java.util.Base64

@Suppress("unused")
object Base64 {
    @JvmStatic
    fun encodeToString(input: ByteArray?, flags: Int): String = Base64.getEncoder().encodeToString(input)

    @JvmStatic
    fun decode(str: String?, flags: Int): ByteArray = Base64.getDecoder().decode(str)
}
