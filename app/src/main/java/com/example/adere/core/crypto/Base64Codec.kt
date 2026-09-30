package com.example.adere.core.crypto

/**
 * Robust Base64 encoder/decoder that works seamlessly on Android device runtime (API 24+)
 * and host JVM unit tests.
 */
object Base64Codec {

    fun encode(bytes: ByteArray): String {
        return try {
            java.util.Base64.getEncoder().encodeToString(bytes)
        } catch (e: Throwable) {
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        }
    }

    fun decode(str: String): ByteArray {
        val clean = str.trim()
        return try {
            java.util.Base64.getDecoder().decode(clean)
        } catch (e: Throwable) {
            android.util.Base64.decode(clean, android.util.Base64.NO_WRAP)
        }
    }
}
