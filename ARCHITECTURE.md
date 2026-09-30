# Adere — System Architecture

```text
app
│
├── core
│   ├── backup       // Encrypted .adere serialization & restore
│   ├── common       // AdereAppContainer dependency injection
│   ├── crypto       // CryptoEngine (AES-GCM, PBKDF2), KeystoreManager, TOTP, PasswordGenerator
│   └── utilities    // ClipboardSecurityHelper (sensitive tag, timed scrubber)
│
├── data
│   └── local        // Room AdereDatabase, VaultDao, VaultItemEntity, VaultConfigStore
│
├── domain
│   ├── model        // VaultCategory, VaultItem, VaultItemPayload
│   └── repository   // VaultRepository, VaultSessionManager (DEK lifecycle & lock state)
│
└── presentation
    ├── components   // AdereTopBar, PasswordStrengthBar, SecurityWarningCard, CategoryChip
    ├── navigation   // AdereNavHost, bottom bar tabs
    ├── screens      // OnboardingAndSetup, Lock, Dashboard, VaultList, ItemDetail, AddEdit, Generator, SecurityHealth, Settings
    ├── theme        // Color (Emerald, Gold, Charcoal), Theme (Dark-first M3), Type
    └── viewmodels   // AdereViewModel
```
