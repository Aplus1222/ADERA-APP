package com.example.adere.presentation.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.core.utilities.ClipboardSecurityHelper
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.presentation.screens.AddEditItemScreen
import com.example.adere.presentation.screens.CategoryAddScreen
import com.example.adere.presentation.screens.DashboardScreen
import com.example.adere.presentation.screens.ItemDetailScreen
import com.example.adere.presentation.screens.LockScreen
import com.example.adere.presentation.screens.OnboardingAndSetupScreen
import com.example.adere.presentation.screens.SecurityHealthScreen
import com.example.adere.presentation.screens.SettingsScreen
import com.example.adere.presentation.viewmodels.AdereViewModel
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavDestination(val label: String) {
    HOME("Home"),
    VAULT("Vault"),
    ADD("Add"),
    SECURITY("Security"),
    SETTINGS("Settings")
}

@Composable
fun AdereApp(
    viewModel: AdereViewModel,
    onTriggerBiometrics: () -> Unit,
    onExportPdfSave: (PdfExportManager.ExportOptions) -> Unit = {},
    onExportPdfShare: (PdfExportManager.ExportOptions) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val lockState by viewModel.lockState.collectAsStateWithLifecycle()
    val rawItems by viewModel.rawItems.collectAsStateWithLifecycle()
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val healthReport by viewModel.healthReport.collectAsStateWithLifecycle()
    val totpMap by viewModel.totpMap.collectAsStateWithLifecycle()

    val biometricEnabled by viewModel.biometricEnabled.collectAsStateWithLifecycle()
    val autoLockSeconds by viewModel.autoLockSeconds.collectAsStateWithLifecycle()
    val screenshotProtection by viewModel.screenshotProtection.collectAsStateWithLifecycle()
    val clipboardClearSeconds by viewModel.clipboardClearSeconds.collectAsStateWithLifecycle()
    val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()
    val generatorOptions by viewModel.generatorOptions.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val hideRecentAppsContent by viewModel.hideRecentAppsContent.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavDestination.HOME) }
    var selectedItemIdForDetail by remember { mutableStateOf<String?>(null) }
    var isAddingItem by remember { mutableStateOf(value = false) }
    var itemCategoryForAdd by remember { mutableStateOf(VaultCategory.SOCIAL) }
    var prefilledPasswordForAdd by remember { mutableStateOf<String?>(null) }
    var prefilledTitleForAdd by remember { mutableStateOf<String?>(null) }
    var prefilledUrlForAdd by remember { mutableStateOf<String?>(null) }
    var editingItemId by remember { mutableStateOf<String?>(null) }

    fun copySecret(label: String, secret: String) {
        ClipboardSecurityHelper.copyToClipboard(
            context = context,
            label = label,
            text = secret,
            autoClearSeconds = clipboardClearSeconds,
            coroutineScope = coroutineScope,
        )
        val clearMsg = if (clipboardClearSeconds > 0) " (Auto-clears in ${clipboardClearSeconds}s)" else ""
        Toast.makeText(context, "$label copied to clipboard$clearMsg", Toast.LENGTH_SHORT).show()
    }

    when (lockState) {
        is VaultLockState.Uninitialized -> {
            OnboardingAndSetupScreen(
                onSetupSuccess = {
                    Toast.makeText(context, "Encrypted vault initialized!", Toast.LENGTH_SHORT).show()
                },
                onSetupVault = { pass, bio, cb ->
                    viewModel.initializeMasterPassword(pass, bio, cb)
                },
                onGetActiveRecoveryKey = viewModel::getActiveRecoveryKey,
            )
        }

        is VaultLockState.Locked -> {
            LockScreen(
                isBiometricAvailable = biometricEnabled,
                onTriggerBiometrics = onTriggerBiometrics,
                onUnlockWithPassword = { pass, cb ->
                    viewModel.unlockWithPassword(pass, cb)
                },
                onRecoverWithKey = { key, newPass, cb ->
                    viewModel.recoverVaultWithKey(key, newPass) { res ->
                        if (res.isSuccess) {
                            Toast.makeText(context, "Vault recovered with Master Key!", Toast.LENGTH_SHORT).show()
                        }
                        cb(res)
                    }
                },
                onResetVault = {
                    viewModel.resetVault {
                        Toast.makeText(context, "Vault has been reset", Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        is VaultLockState.Unlocked -> {
            // Check sub-destinations
            if (isAddingItem) {
                BackHandler {
                    isAddingItem = false
                    prefilledPasswordForAdd = null
                    prefilledTitleForAdd = null
                    prefilledUrlForAdd = null
                }
                val initialItem = if ((prefilledPasswordForAdd != null) || (prefilledTitleForAdd != null)) {
                    com.example.adere.domain.model.VaultItem(
                        title = prefilledTitleForAdd ?: "",
                        category = itemCategoryForAdd,
                        payload = com.example.adere.domain.model.VaultItemPayload(
                            password = prefilledPasswordForAdd ?: "",
                            url = prefilledUrlForAdd ?: ""
                        )
                    )
                } else null

                AddEditItemScreen(
                    initialItem = initialItem,
                    initialCategory = itemCategoryForAdd,
                    onBackClick = {
                        isAddingItem = false
                        prefilledPasswordForAdd = null
                        prefilledTitleForAdd = null
                        prefilledUrlForAdd = null
                    },
                    onSaveItem = { newItem ->
                        viewModel.saveItem(newItem) { res ->
                            if (res.isSuccess) {
                                isAddingItem = false
                                prefilledPasswordForAdd = null
                                prefilledTitleForAdd = null
                                prefilledUrlForAdd = null
                                Toast.makeText(context, "Item saved securely", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            } else if (editingItemId != null) {
                BackHandler { editingItemId = null }
                val itemToEdit = rawItems.firstOrNull { it.id == editingItemId }
                if (itemToEdit != null) {
                    AddEditItemScreen(
                        initialItem = itemToEdit,
                        initialCategory = itemToEdit.category,
                        onBackClick = { editingItemId = null },
                        onSaveItem = { updatedItem ->
                            viewModel.saveItem(updatedItem) { res ->
                                if (res.isSuccess) {
                                    editingItemId = null
                                    Toast.makeText(context, "Item updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                } else {
                    editingItemId = null
                }
            } else if (selectedItemIdForDetail != null) {
                BackHandler { selectedItemIdForDetail = null }
                val detailItem = rawItems.firstOrNull { it.id == selectedItemIdForDetail }
                if (detailItem != null) {
                    ItemDetailScreen(
                        item = detailItem,
                        totpResult = totpMap[detailItem.id],
                        onBackClick = { selectedItemIdForDetail = null },
                        onEditClick = { id -> editingItemId = id },
                        onDeleteClick = { id ->
                            viewModel.deleteItem(id) { res ->
                                if (res.isSuccess) {
                                    selectedItemIdForDetail = null
                                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onToggleFavorite = { id, currentFav ->
                            viewModel.toggleFavorite(id, currentFav)
                        },
                        onCopySecret = { label, secret -> copySecret(label, secret) },
                        onLockClick = { viewModel.lockVault() },
                        onExportPdf = { options, isShare ->
                            if (isShare) onExportPdfShare(options) else onExportPdfSave(options)
                        }
                    )
                } else {
                    selectedItemIdForDetail = null
                }
            } else {
                // Main Bottom Tabbed Navigation
                Scaffold(
                    bottomBar = {
                        com.example.adere.presentation.components.AdereTotalSecurityBottomBar(
                            currentTab = currentTab,
                            onSelectTab = { currentTab = it },
                            onCenterAddClick = {
                                currentTab = NavDestination.ADD
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            NavDestination.HOME -> {
                                DashboardScreen(
                                    items = rawItems,
                                    healthReport = healthReport,
                                    onNavigateToItemDetail = { id -> selectedItemIdForDetail = id },
                                    onNavigateToAddItem = { cat ->
                                        itemCategoryForAdd = cat
                                        isAddingItem = true
                                    },
                                    onNavigateToSecurity = { currentTab = NavDestination.SECURITY },
                                    onNavigateToCategory = { cat ->
                                        viewModel.selectCategory(cat)
                                        currentTab = NavDestination.VAULT
                                    },
                                    onLockClick = { viewModel.lockVault() }
                                )
                            }

                            NavDestination.VAULT -> {
                                com.example.adere.presentation.screens.VaultListScreen(
                                    items = filteredItems,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    onSearchQueryChange = { q -> viewModel.setSearchQuery(q) },
                                    onCategorySelect = { c -> viewModel.selectCategory(c) },
                                    onItemClick = { id -> selectedItemIdForDetail = id },
                                    onAddItemClick = {
                                        currentTab = NavDestination.ADD
                                    },
                                    onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                                    onQuickCopy = { item ->
                                        val secretToCopy = when {
                                            item.payload.password.isNotBlank() -> item.payload.password
                                            item.payload.pin.isNotBlank() -> item.payload.pin
                                            item.payload.cryptoAddress.isNotBlank() -> item.payload.cryptoAddress
                                            item.payload.wifiPassword.isNotBlank() -> item.payload.wifiPassword
                                            else -> item.username
                                        }
                                        copySecret(item.title, secretToCopy)
                                    },
                                    onLockClick = { viewModel.lockVault() }
                                )
                            }

                            NavDestination.ADD -> {
                                CategoryAddScreen(
                                    onSelectCategory = { category ->
                                        itemCategoryForAdd = category
                                        prefilledTitleForAdd = null
                                        prefilledUrlForAdd = null
                                        isAddingItem = true
                                    },
                                    onSelectBrand = { brand ->
                                        itemCategoryForAdd = brand.category
                                        prefilledTitleForAdd = brand.name
                                        prefilledUrlForAdd = brand.defaultUrl
                                        isAddingItem = true
                                    },
                                    onLockClick = { viewModel.lockVault() }
                                )
                            }

                            NavDestination.SECURITY -> {
                                SecurityHealthScreen(
                                    healthReport = healthReport,
                                    items = rawItems,
                                    onItemClick = { id -> selectedItemIdForDetail = id },
                                    onLockClick = { viewModel.lockVault() },
                                    onBackClick = { currentTab = NavDestination.HOME }
                                )
                            }

                            NavDestination.SETTINGS -> {
                                SettingsScreen(
                                    totalCredentialsCount = rawItems.size,
                                    favoritesCount = rawItems.count { it.isFavorite },
                                    isVaultProtected = lockState == VaultLockState.Unlocked,
                                    biometricEnabled = biometricEnabled,
                                    autoLockSeconds = autoLockSeconds,
                                    screenshotProtection = screenshotProtection,
                                    clipboardClearSeconds = clipboardClearSeconds,
                                    hideRecentAppsContent = hideRecentAppsContent,
                                    notificationsEnabled = notificationsEnabled,
                                    themePalette = themePalette,
                                    generatorOptions = generatorOptions,
                                    onSelectThemePalette = { palette ->
                                        viewModel.setThemePalette(palette)
                                        Toast.makeText(context, "Theme updated: ${palette.title}", Toast.LENGTH_SHORT).show()
                                    },
                                    onToggleBiometric = { enable ->
                                        viewModel.setBiometric(enable) { res ->
                                            if (res.isSuccess) {
                                                Toast.makeText(context, "Biometric setting updated", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onSetAutoLockSeconds = { s -> viewModel.setAutoLockSeconds(s) },
                                    onToggleScreenshotProtection = { p -> viewModel.setScreenshotProtection(p) },
                                    onSetClipboardClearSeconds = { c -> viewModel.setClipboardClearSeconds(c) },
                                    onToggleHideRecentApps = { h -> viewModel.setHideRecentAppsContent(h) },
                                    onToggleNotifications = { n -> viewModel.setNotificationsEnabled(n) },
                                    onUpdateGeneratorOptions = { opts -> viewModel.updateGeneratorOptions(opts) },
                                    onChangeMasterPassword = { old, new, cb ->
                                        viewModel.changeMasterPassword(old, new, cb)
                                    },
                                    onExportBackup = { pass, cb ->
                                        viewModel.exportBackup(pass) { res ->
                                            cb(res.map { it.backupString })
                                        }
                                    },
                                    onRestoreBackup = { backupContent, passphrase, cb ->
                                        viewModel.restoreBackup(backupContent, passphrase, cb)
                                    },
                                    onExportPdf = { options, isShare ->
                                        if (isShare) {
                                            onExportPdfShare(options)
                                        } else {
                                            onExportPdfSave(options)
                                        }
                                    },
                                    onGetRecoveryKey = { viewModel.getActiveRecoveryKey() },
                                    onRegenerateRecoveryKey = { cb ->
                                        viewModel.regenerateRecoveryKey(cb)
                                    },
                                    onResetVault = {
                                        viewModel.resetVault {
                                            Toast.makeText(context, "Vault destroyed and reset", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
