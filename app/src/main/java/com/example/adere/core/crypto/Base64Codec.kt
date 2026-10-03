package com.example.adere.core.crypto

import android.os.Build
import java.util.Base64

/**
 * Robust Base64 encoder/decoder that works seamlessly on Android device runtime (API 24+)
 * and host JVM unit tests.
 */
object Base64Codec {

    fun encode(bytes: ByteArray): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(bytes)
        } else {
            try {
                android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
            } catch (_: Throwable) {
                val encoderClass = Class.forName("java.util.Base64")
                val getEncoderMethod = encoderClass.getMethod("getEncoder")
                val encoder = getEncoderMethod.invoke(null)
                val encodeMethod = encoder.javaClass.getMethod("encodeToString", ByteArray::class.java)
                encodeMethod.invoke(encoder, bytes) as String
            }
        }
    }

    fun decode(str: String): ByteArray {
        val clean = str.replace("\\s+".toRegex(), "")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(clean)
        } else {
            try {
                android.util.Base64.decode(clean, android.util.Base64.NO_WRAP)
            } catch (_: Throwable) {
                val decoderClass = Class.forName("java.util.Base64")
                val getDecoderMethod = decoderClass.getMethod("getDecoder")
                val decoder = getDecoderMethod.invoke(null)
                val decodeMethod = decoder.javaClass.getMethod("decode", String::class.java)
                decodeMethod.invoke(decoder, clean) as ByteArray
            }
        }
    }
}
