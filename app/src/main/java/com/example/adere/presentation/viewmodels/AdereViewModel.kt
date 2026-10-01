package com.example.adere.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.domain.repository.VaultRepository
import com.example.adere.domain.repository.VaultSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdereViewModel(
    val repository: VaultRepository,
    val sessionManager: VaultSessionManager,
    val configStore: VaultConfigStore
) : ViewModel() {

    val lockState: StateFlow<VaultLockState> = sessionManager.lockState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(VaultCategory.ALL)
    val selectedCategory: StateFlow<VaultCategory> = _selectedCategory.asStateFlow()

    val rawItems: StateFlow<List<VaultItem>> = repository.allItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredItems: StateFlow<List<VaultItem>> = combine(
        rawItems,
        _searchQuery,
        _selectedCategory
    ) { items, query, category ->
        items.filter { item ->
            val matchesCategory = when (category) {
                VaultCategory.ALL -> true
                VaultCategory.FAVORITES -> item.isFavorite
                else -> item.category == category
            }
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.category.title.contains(query, ignoreCase = true) ||
                    item.category.name.contains(query, ignoreCase = true) ||
                    item.username.contains(query, ignoreCase = true) ||
                    item.payload.url.contains(query, ignoreCase = true) ||
                    item.payload.cryptoNetwork.contains(query, ignoreCase = true) ||
                    item.payload.wifiSsid.contains(query, ignoreCase = true) ||
                    item.payload.notes.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _healthReport = MutableStateFlow(
        PasswordHealthAnalyzer.VaultHealthReport(
            0, 0, 0, 0, 0, 0,
            PasswordHealthAnalyzer.SecurityStatus.STRONG,
            100
        )
    )
    val healthReport: StateFlow<PasswordHealthAnalyzer.VaultHealthReport> = _healthReport.asStateFlow()

    private val _totpMap = MutableStateFlow<Map<String, TOTPGenerator.TotpResult>>(emptyMap())
    val totpMap: StateFlow<Map<String, TOTPGenerator.TotpResult>> = _totpMap.asStateFlow()

    // Generator state
    private val _generatorOptions = MutableStateFlow(PasswordGenerator.GeneratorOptions())
    val generatorOptions: StateFlow<PasswordGenerator.GeneratorOptions> = _generatorOptions.asStateFlow()

    private val _generatedResult = MutableStateFlow(PasswordGenerator.generate(PasswordGenerator.GeneratorOptions()))
    val generatedResult: StateFlow<PasswordGenerator.GenerationResult> = _generatedResult.asStateFlow()

    // Settings observable state
    private val _autoLockSeconds = MutableStateFlow(configStore.autoLockSeconds)
    val autoLockSeconds: StateFlow<Int> = _autoLockSeconds.asStateFlow()

    private val _screenshotProtection = MutableStateFlow(configStore.isScreenshotProtectionEnabled)
    val screenshotProtection: StateFlow<Boolean> = _screenshotProtection.asStateFlow()

    private val _clipboardClearSeconds = MutableStateFlow(configStore.clipboardClearSeconds)
    val clipboardClearSeconds: StateFlow<Int> = _clipboardClearSeconds.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(configStore.isBiometricEnabled)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _themePalette = MutableStateFlow(
        try {
            com.example.ui.theme.VaultThemePalette.valueOf(configStore.themePalette)
        } catch (e: Exception) {
            com.example.ui.theme.VaultThemePalette.TOTAL_SECURITY
        }
    )
    val themePalette: StateFlow<com.example.ui.theme.VaultThemePalette> = _themePalette.asStateFlow()

    init {
        // Periodic TOTP refresh ticker
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                updateTotpCodes()
                delay(1000)
            }
        }

        // Recalculate health report when items change
        viewModelScope.launch {
            rawItems.collect {
                refreshHealthReport()
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        sessionManager.recordInteraction()
    }

    fun selectCategory(category: VaultCategory) {
        _selectedCategory.value = category
        sessionManager.recordInteraction()
    }

    private fun updateTotpCodes() {
        val currentItems = rawItems.value
        val newMap = mutableMapOf<String, TOTPGenerator.TotpResult>()
        for (item in currentItems) {
            if (item.payload.totpSecret.isNotBlank()) {
                val res = TOTPGenerator.generateCurrentTotp(
                    secretBase32 = item.payload.totpSecret,
                    timeStepSeconds = item.payload.totpPeriod.toLong(),
                    digits = item.payload.totpDigits
                )
                if (res != null) {
                    newMap[item.id] = res
                }
            }
        }
        _totpMap.value = newMap
    }

    fun refreshHealthReport() {
        viewModelScope.launch {
            _healthReport.value = repository.computeHealthReport()
        }
    }

    fun initializeMasterPassword(password: String, enableBiometrics: Boolean, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.initializeVault(password.toCharArray(), enableBiometrics)
            _biometricEnabled.value = configStore.isBiometricEnabled
            onResult(res)
        }
    }

    fun unlockWithPassword(password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.unlockWithMasterPassword(password.toCharArray())
            onResult(res)
        }
    }

    fun unlockWithBiometric(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.unlockWithBiometric()
            onResult(res)
        }
    }

    fun recoverVaultWithKey(recoveryKey: String, newMasterPassword: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.recoverVaultWithKey(recoveryKey, newMasterPassword.toCharArray())
            onResult(res)
        }
    }

    fun getActiveRecoveryKey(): Result<String> {
        return sessionManager.getActiveRecoveryKey()
    }

    fun regenerateRecoveryKey(onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.regenerateRecoveryKey()
            onResult(res)
        }
    }

    fun lockVault() {
        sessionManager.lock()
    }

    fun saveItem(item: VaultItem, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.saveItem(item)
            onResult(res)
        }
    }

    fun deleteItem(id: String, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.deleteItem(id)
            onResult(res)
        }
    }

    fun toggleFavorite(id: String, currentFav: Boolean) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            repository.toggleFavorite(id, !currentFav)
        }
    }

    // Generator methods
    fun updateGeneratorOptions(newOptions: PasswordGenerator.GeneratorOptions) {
        _generatorOptions.value = newOptions
        regeneratePassword()
    }

    fun regeneratePassword() {
        _generatedResult.value = PasswordGenerator.generate(_generatorOptions.value)
    }

    // Backup & Restore
    fun exportBackup(passphrase: String, onResult: (Result<BackupManager.BackupExportResult>) -> Unit) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.exportEncryptedBackup(passphrase.toCharArray())
            onResult(res)
        }
    }

    fun exportToPdf(
        options: com.example.adere.core.backup.PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream,
        onResult: (Result<Int>) -> Unit
    ) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.exportToPdf(options, outputStream)
            onResult(res)
        }
    }

    fun restoreBackup(content: String, passphrase: String, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.restoreFromEncryptedBackup(content, passphrase.toCharArray())
            onResult(res)
        }
    }

    fun changeMasterPassword(oldPass: String, newPass: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.changeMasterPassword(oldPass.toCharArray(), newPass.toCharArray())
            onResult(res)
        }
    }

    fun setBiometric(enabled: Boolean, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.setBiometricEnabled(enabled)
            _biometricEnabled.value = configStore.isBiometricEnabled
            onResult(res)
        }
    }

    fun setAutoLockSeconds(seconds: Int) {
        configStore.autoLockSeconds = seconds
        _autoLockSeconds.value = seconds
    }

    fun setScreenshotProtection(enabled: Boolean) {
        configStore.isScreenshotProtectionEnabled = enabled
        _screenshotProtection.value = enabled
    }

    fun setClipboardClearSeconds(seconds: Int) {
        configStore.clipboardClearSeconds = seconds
        _clipboardClearSeconds.value = seconds
    }

    fun setThemePalette(palette: com.example.ui.theme.VaultThemePalette) {
        configStore.themePalette = palette.name
        _themePalette.value = palette
    }

    fun resetVault(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllItems()
            configStore.resetVault()
            sessionManager.lock()
            onComplete()
        }
    }

    class Factory(
        private val repository: VaultRepository,
        private val sessionManager: VaultSessionManager,
        private val configStore: VaultConfigStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdereViewModel(repository, sessionManager, configStore) as T
        }
    }
}
