package com.example.adere.domain.model

import java.util.UUID

/**
 * High-level domain model representing a decrypted vault item.
 *
 * Placed in memory only during active vault session inspection.
 */
data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    val category: VaultCategory = VaultCategory.OTHER,
    val title: String,
    val username: String = "",
    val payload: VaultItemPayload = VaultItemPayload(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val passwordLastChanged: Long = System.currentTimeMillis()
)
