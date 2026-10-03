package com.example.adere.core.crypto

import java.security.SecureRandom
import kotlin.math.log2

/**
 * Cryptographically Secure Password & Passphrase Generator.
 *
 * Uses java.security.SecureRandom exclusively.
 */
object PasswordGenerator {

    private val secureRandom = SecureRandom()

    private const val UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ" // minus ambiguous I, O by default or switchable
    private const val UPPERCASE_FULL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE = "abcdefghijkmnopqrstuvwxyz" // minus ambiguous l
    private const val LOWERCASE_FULL = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "23456789" // minus ambiguous 0, 1
    private const val DIGITS_FULL = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    private val WORD_LIST = listOf(
        "anchor", "beacon", "citadel", "delta", "ember", "falcon", "granite", "horizon",
        "iron", "jasper", "kinetic", "lunar", "matrix", "nexus", "orbit", "phoenix",
        "quantum", "radar", "shield", "titan", "unity", "vortex", "zenith", "aurora",
        "boulder", "cipher", "dynamo", "echo", "frost", "glacier", "harbor", "island",
        "javelin", "knight", "legacy", "monolith", "nebula", "onyx", "pioneer", "quasar",
        "relic", "summit", "timber", "uptime", "valiant", "wildcat", "apex", "bastion",
    )

    data class GeneratorOptions(
        val length: Int = 20,
        val includeUppercase: Boolean = true,
        val includeLowercase: Boolean = true,
        val includeDigits: Boolean = true,
        val includeSymbols: Boolean = true,
        val excludeAmbiguous: Boolean = true,
        val isPassphraseMode: Boolean = false,
        val passphraseWordCount: Int = 4,
        val passphraseSeparator: String = "-",
    )

    enum class PasswordStrength(val label: String) {
        VERY_WEAK("Very Weak"),
        WEAK("Weak"),
        FAIR("Fair"),
        STRONG("Strong"),
        VERY_STRONG("Very Strong"),
    }

    data class GenerationResult(
        val password: String,
        val strength: PasswordStrength,
        val entropyBits: Double,
    )

    fun generate(options: GeneratorOptions): GenerationResult {
        if (options.isPassphraseMode) {
            val words = mutableListOf<String>()
            for (i in 0 until options.passphraseWordCount) {
                val index = secureRandom.nextInt(WORD_LIST.size)
                var word = WORD_LIST[index]
                if (options.includeUppercase && (i == 0)) {
                    word = word.replaceFirstChar { it.uppercase() }
                }
                words.add(word)
            }
            if (options.includeDigits) {
                words.add((secureRandom.nextInt(90) + 10).toString())
            }
            val passphrase = words.joinToString(options.passphraseSeparator)
            val entropy = calculateEntropy(passphrase)
            return GenerationResult(passphrase, evaluateStrength(entropy, passphrase.length), entropy)
        }

        val charPool = StringBuilder()
        val guaranteedChars = mutableListOf<Char>()

        val uppers = if (options.excludeAmbiguous) UPPERCASE else UPPERCASE_FULL
        val lowers = if (options.excludeAmbiguous) LOWERCASE else LOWERCASE_FULL
        val digits = if (options.excludeAmbiguous) DIGITS else DIGITS_FULL
        val symbols = SYMBOLS

        if (options.includeUppercase) {
            charPool.append(uppers)
            guaranteedChars.add(uppers[secureRandom.nextInt(uppers.length)])
        }
        if (options.includeLowercase) {
            charPool.append(lowers)
            guaranteedChars.add(lowers[secureRandom.nextInt(lowers.length)])
        }
        if (options.includeDigits) {
            charPool.append(digits)
            guaranteedChars.add(digits[secureRandom.nextInt(digits.length)])
        }
        if (options.includeSymbols) {
            charPool.append(symbols)
            guaranteedChars.add(symbols[secureRandom.nextInt(symbols.length)])
        }

        // Fallback if none selected
        if (charPool.isEmpty()) {
            charPool.append(lowers)
            guaranteedChars.add(lowers[secureRandom.nextInt(lowers.length)])
        }

        val pool = charPool.toString()
        val passwordChars = ArrayList<Char>(options.length)
        passwordChars.addAll(guaranteedChars)

        while (passwordChars.size < options.length) {
            passwordChars.add(pool[secureRandom.nextInt(pool.length)])
        }

        // Fisher-Yates shuffle with SecureRandom
        for (i in (passwordChars.size - 1) downTo 1) {
            val j = secureRandom.nextInt(i + 1)
            val temp = passwordChars[i]
            passwordChars[i] = passwordChars[j]
            passwordChars[j] = temp
        }

        val password = passwordChars.joinToString("")
        val entropy = calculateEntropy(password)
        val strength = evaluateStrength(entropy, password.length)
        return GenerationResult(password, strength, entropy)
    }

    fun calculateEntropy(password: String): Double {
        if (password.isEmpty()) return 0.0
        var poolSize = 0
        if (password.any { it.isUpperCase() }) poolSize += 26
        if (password.any { it.isLowerCase() }) poolSize += 26
        if (password.any { it.isDigit() }) poolSize += 10
        if (password.any { !it.isLetterOrDigit() }) poolSize += 32
        if (poolSize == 0) poolSize = 26
        return password.length * log2(poolSize.toDouble())
    }

    fun evaluateStrength(entropy: Double, length: Int): PasswordStrength {
        return when {
            (length < 8) || (entropy < 28.0) -> PasswordStrength.VERY_WEAK
            (length < 12) || (entropy < 45.0) -> PasswordStrength.WEAK
            (length < 16) || (entropy < 65.0) -> PasswordStrength.FAIR
            entropy < 85.0 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }
}
