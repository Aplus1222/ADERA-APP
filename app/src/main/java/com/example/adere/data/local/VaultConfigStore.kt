package com.example.adere.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Stores non-secret vault metadata and protected/encrypted key material.
 *
 * Strictly adheres to rule:
 * - Master password is NEVER stored.
 * - DEK is NEVER stored in plaintext.
 * - Only stored as ciphertext encrypted with MasterKey or KeystoreKey.
 */
class VaultConfigStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adere_vault_config", Context.MODE_PRIVATE)

    init {
        // Ensure screenshot protection is disabled so that browser streaming displays properly
        prefs.edit().putBoolean(KEY_SCREENSHOT_PROTECTION, false).apply()
    }

    companion object {
        private const val KEY_INITIALIZED = "vault_initialized"
        private const val KEY_MASTER_SALT = "master_salt"
        private const val KEY_ENCRYPTED_DEK = "encrypted_dek"
        private const val KEY_ENCRYPTED_DEK_IV = "encrypted_dek_iv"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_ENCRYPTED_DEK = "biometric_encrypted_dek"
        private const val KEY_BIOMETRIC_ENCRYPTED_DEK_IV = "biometric_encrypted_dek_iv"
        private const val KEY_AUTO_LOCK_SECONDS = "auto_lock_seconds"
        private const val KEY_SCREENSHOT_PROTECTION = "screenshot_protection"
        private const val KEY_CLIPBOARD_CLEAR_SECONDS = "clipboard_clear_seconds"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until_ms"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_THEME_PALETTE = "selected_theme_palette"
    }

    var isInitialized: Boolean
        get() = prefs.getBoolean(KEY_INITIALIZED, false)
        set(value) = prefs.edit().putBoolean(KEY_INITIALIZED, value).apply()

    var masterSaltBase64: String
        get() = prefs.getString(KEY_MASTER_SALT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MASTER_SALT, value).apply()

    var encryptedDekBase64: String
        get() = prefs.getString(KEY_ENCRYPTED_DEK, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ENCRYPTED_DEK, value).apply()

    var encryptedDekIvBase64: String
        get() = prefs.getString(KEY_ENCRYPTED_DEK_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ENCRYPTED_DEK_IV, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var biometricEncryptedDekBase64: String
        get() = prefs.getString(KEY_BIOMETRIC_ENCRYPTED_DEK, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIOMETRIC_ENCRYPTED_DEK, value).apply()

    var biometricDekIvBase64: String
        get() = prefs.getString(KEY_BIOMETRIC_ENCRYPTED_DEK_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIOMETRIC_ENCRYPTED_DEK_IV, value).apply()

    var autoLockSeconds: Int
        get() = prefs.getInt(KEY_AUTO_LOCK_SECONDS, 60) // 1 minute default
        set(value) = prefs.edit().putInt(KEY_AUTO_LOCK_SECONDS, value).apply()

    var isScreenshotProtectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCREENSHOT_PROTECTION, false) // disabled by default so streaming emulator works
        set(value) = prefs.edit().putBoolean(KEY_SCREENSHOT_PROTECTION, value).apply()

    var clipboardClearSeconds: Int
        get() = prefs.getInt(KEY_CLIPBOARD_CLEAR_SECONDS, 30) // 30 seconds default
        set(value) = prefs.edit().putInt(KEY_CLIPBOARD_CLEAR_SECONDS, value).apply()

    var failedAttempts: Int
        get() = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        set(value) = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, value).apply()

    var lockoutUntilMs: Long
        get() = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_LOCKOUT_UNTIL, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var themePalette: String
        get() = prefs.getString(KEY_THEME_PALETTE, "OBSIDIAN_EMERALD") ?: "OBSIDIAN_EMERALD"
        set(value) = prefs.edit().putString(KEY_THEME_PALETTE, value).apply()

    fun resetVault() {
        prefs.edit().clear().apply()
    }
}
