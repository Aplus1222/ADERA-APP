# Adere — Security Architecture & Threat Model

## 1. Overview
Adere is an offline-first, zero-knowledge encrypted vault application designed for Android. It stores passwords, credit cards, bank accounts, crypto wallets, seed phrases, private keys, TOTP 2FA secrets, Wi-Fi credentials, and secure notes.

## 2. Cryptographic Design
- **Key Derivation (KDF)**: PBKDF2-HMAC-SHA256 with 100,000 iterations and a cryptographically secure 256-bit random salt (`SecureRandom`) generated at initialization.
- **Authenticated Encryption**: AES-256-GCM (Galois/Counter Mode) with 128-bit authentication tags and unique 96-bit (12-byte) nonces/IVs per encryption. Nonces are never reused.
- **Key Hierarchy**:
  - `Master Password` -> PBKDF2(Salt, 100,000) -> `Master Key` (256-bit).
  - `Data Encryption Key (DEK)` (256-bit random AES key).
  - `Master Key` encrypts the DEK using AES-256-GCM.
  - Active DEK is kept only in ephemeral JVM memory during an unlocked session and is securely zeroed (`Arrays.fill(0)`) upon auto-lock, panic lock, or app backgrounding.
- **Hardware Protection (Android KeyStore)**:
  - When Biometrics are enabled, an AES-256-GCM hardware key is generated inside the Android KeyStore (`AndroidKeyStore` provider).
  - The Keystore key wraps the DEK, enabling rapid biometric unlocking without persisting the Master Password in plaintext.
- **2FA / TOTP**: RFC 6238 compliant HMAC-SHA1 calculation with Base32 decoding and dynamic truncation.

## 3. Storage Security
- Zero plaintext database columns for sensitive values. All secrets reside in AES-GCM ciphertext payloads.
- Local SQLite database files (`adere_secure_vault.db`) are excluded from Android OS cloud auto-backup in `backup_rules.xml` and `data_extraction_rules.xml`.
- App-private internal storage only (`/data/user/0/...`).

## 4. UI & Memory Defense
- `WindowManager.LayoutParams.FLAG_SECURE` blocks screen captures, screenshot leaks, and recent-app previews.
- Revealed passwords feature an automatic 30-second countdown before re-obfuscating.
- Copying sensitive data marks the clipboard as sensitive (Android 13+ `ClipDescription.EXTRA_IS_SENSITIVE`) and triggers an automatic timer to wipe the clipboard after 30 seconds.
- Navigation routes never accept secrets or passwords as URL parameters.

## 5. Encrypted Backup Specification (.adere)
Format: `ADERE_VAULT_BACKUP:v1:<saltBase64>:<ivBase64>:<ciphertextBase64>`
- Derived using dedicated user-provided Backup Passphrase with PBKDF2-HMAC-SHA256.
- Encrypted with AES-256-GCM. Corrupted or wrong-password attempts are rejected before parsing.

## 6. Permissions & Network Isolation
- **No `INTERNET` permission**: Adere has zero network access declared in its manifest, rendering remote exfiltration mathematically impossible at the OS sandbox level.
