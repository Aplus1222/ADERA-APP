package com.example.adere.core.common

import android.content.Context
import com.example.adere.core.crypto.KeystoreManager
import com.example.adere.data.local.AdereDatabase
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.repository.VaultRepository
import com.example.adere.domain.repository.VaultSessionManager

class AdereAppContainer(context: Context) {
    val database: AdereDatabase = AdereDatabase.getDatabase(context)
    val configStore: VaultConfigStore = VaultConfigStore(context)
    val keystoreManager: KeystoreManager = KeystoreManager()
    val sessionManager: VaultSessionManager = VaultSessionManager(configStore, keystoreManager)
    val vaultRepository: VaultRepository = VaultRepository(database.vaultDao(), sessionManager)
}
