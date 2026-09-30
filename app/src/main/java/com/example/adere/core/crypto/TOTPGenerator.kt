package com.example.adere.core.crypto

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

/**
 * Standard RFC 6238 Time-based One-Time Password (TOTP) generator.
 *
 * Implements HMAC-SHA1 calculation, Base32 decoding, and countdown tracking.
 */
object TOTPGenerator {

    private const val DEFAULT_TIME_STEP_SECONDS = 30L
    private const val DEFAULT_DIGITS = 6
    private const val HMAC_ALGORITHM = "HmacSHA1"

    data class TotpResult(
        val code: String,
        val formattedCode: String,
        val secondsRemaining: Int,
        val progress: Float // 1.0f (full) down to 0.0f (expiring)
    )

    fun generateCurrentTotp(
        secretBase32: String,
        timeStepSeconds: Long = DEFAULT_TIME_STEP_SECONDS,
        digits: Int = DEFAULT_DIGITS,
        currentTimeMillis: Long = System.currentTimeMillis()
    ): TotpResult? {
        val cleanSecret = secretBase32.replace(" ", "").replace("-", "").uppercase()
        val keyBytes = decodeBase32(cleanSecret) ?: return null

        val currentEpochSeconds = currentTimeMillis / 1000L
        val counter = currentEpochSeconds / timeStepSeconds
        val secondsRemaining = (timeStepSeconds - (currentEpochSeconds % timeStepSeconds)).toInt()
        val progress = secondsRemaining.toFloat() / timeStepSeconds.toFloat()

        val rawCode = generateOtp(keyBytes, counter, digits)
        val formatted = if (rawCode.length == 6) {
            "${rawCode.substring(0, 3)} ${rawCode.substring(3)}"
        } else {
            rawCode
        }

        return TotpResult(rawCode, formatted, secondsRemaining, progress)
    }

    private fun generateOtp(key: ByteArray, counter: Long, digits: Int): String {
        val buffer = ByteBuffer.allocate(8).putLong(counter).array()
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(key, HMAC_ALGORITHM))
        val hash = mac.doFinal(buffer)

        // Dynamic truncation (RFC 4226)
        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % (10.0.pow(digits.toDouble()).toInt())
        return otp.toString().padStart(digits, '0')
    }

    /**
     * Decodes RFC 4648 Base32 alphabet.
     */
    fun decodeBase32(input: String): ByteArray? {
        val base32Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val cleanInput = input.trim().uppercase().replace("=", "")
        if (cleanInput.isEmpty()) return null

        val output = mutableListOf<Byte>()
        var buffer = 0
        var bitsLeft = 0

        for (char in cleanInput) {
            val charValue = base32Chars.indexOf(char)
            if (charValue < 0) return null // Invalid Base32 char

            buffer = (buffer shl 5) or charValue
            bitsLeft += 5

            if (bitsLeft >= 8) {
                bitsLeft -= 8
                output.add(((buffer shr bitsLeft) and 0xFF).toByte())
            }
        }
        return output.toByteArray()
    }
}
