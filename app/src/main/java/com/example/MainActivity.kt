package com.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.adere.AdereApplication
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.presentation.navigation.AdereApp
import com.example.adere.presentation.viewmodels.AdereViewModel
import com.example.ui.theme.AdereTheme
import java.io.File
import java.io.FileOutputStream

class MainActivity : FragmentActivity() {

    private val viewModel: AdereViewModel by viewModels {
        val container = (application as AdereApplication).container
        AdereViewModel.Factory(
            repository = container.vaultRepository,
            sessionManager = container.sessionManager,
            configStore = container.configStore,
        )
    }

    private var pendingPdfExportOptions: PdfExportManager.ExportOptions? = null
    private var isSystemPickerActive = false

    private val createPdfDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        isSystemPickerActive = false
        if (uri != null) {
            val options = pendingPdfExportOptions ?: PdfExportManager.ExportOptions()
            try {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    viewModel.exportToPdf(options, outputStream) { result ->
                        try {
                            outputStream.flush()
                            outputStream.close()
                        } catch (_: Exception) {}

                        if (result.isSuccess) {
                            Toast.makeText(
                                this,
                                "PDF exported successfully (${result.getOrNull()} items)",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to write PDF: ${result.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error opening file for writing", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)

        setContent {
            val themePalette by viewModel.themePalette.collectAsStateWithLifecycle()
            val screenshotProtection by viewModel.screenshotProtection.collectAsStateWithLifecycle()

            LaunchedEffect(screenshotProtection) {
                if (screenshotProtection) {
                    window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            AdereTheme(palette = themePalette) {
                AdereApp(
                    viewModel = viewModel,
                    onTriggerBiometrics = ::triggerBiometricPrompt,
                    onExportPdfSave = { options ->
                        pendingPdfExportOptions = options
                        isSystemPickerActive = true
                        createPdfDocumentLauncher.launch("adere_vault_passwords_${System.currentTimeMillis()}.pdf")
                    },
                    onExportPdfShare = ::sharePdfDocument,
                )
            }
        }
    }

    private fun sharePdfDocument(options: PdfExportManager.ExportOptions) {
        try {
            PdfExportManager.cleanTemporaryExports(cacheDir)
            val exportDir = File(cacheDir, "exports").apply { mkdirs() }
            val pdfFile = File(exportDir, "adere_vault_passwords_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            viewModel.exportToPdf(options, outputStream) { result ->
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (_: Exception) {}

                if (result.isSuccess) {
                    val fileUri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        pdfFile
                    )
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        putExtra(Intent.EXTRA_SUBJECT, "Adere Vault Passwords Export")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    isSystemPickerActive = true
                    startActivity(Intent.createChooser(sendIntent, "Print or Share Vault PDF"))
                } else {
                    Toast.makeText(
                        this,
                        "Failed to generate PDF: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error preparing PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.sessionManager.checkAutoLock()
    }

    override fun onStop() {
        super.onStop()
        // If immediate auto-lock is configured, lock right away (unless system picker is active)
        if (!isSystemPickerActive && (viewModel.autoLockSeconds.value == 0)) {
            viewModel.lockVault()
        }
        isSystemPickerActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        PdfExportManager.cleanTemporaryExports(cacheDir)
    }

    private fun triggerBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        val hasCredential = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        val authenticators = if (hasCredential) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        val canAuth = biometricManager.canAuthenticate(authenticators)
        val canStrongOnly = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS && canStrongOnly != BiometricManager.BIOMETRIC_SUCCESS) {
            val reason = when {
                canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED || canStrongOnly == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    "No fingerprint or face unlock enrolled on device. Please unlock with your Master Password."
                canAuth == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE || canStrongOnly == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    "Biometric hardware not detected. Please unlock with your Master Password."
                canAuth == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE || canStrongOnly == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    "Biometric sensor currently unavailable. Please unlock with your Master Password."
                else -> "Biometric authentication unavailable. Please unlock with your Master Password."
            }
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)
        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_prompt_title))
            .setSubtitle(getString(R.string.biometric_prompt_subtitle))

        if (canAuth == BiometricManager.BIOMETRIC_SUCCESS && hasCredential) {
            promptBuilder.setAllowedAuthenticators(authenticators)
        } else {
            promptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            promptBuilder.setNegativeButtonText(getString(R.string.biometric_use_password))
        }

        val promptInfo = promptBuilder.build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.unlockWithBiometric { unlockResult ->
                        if (unlockResult.isSuccess) {
                            Toast.makeText(this@MainActivity, "Vault unlocked with biometrics", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                unlockResult.exceptionOrNull()?.message ?: "Biometric unlock failed. Please use Master Password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                    ) {
                        Toast.makeText(this@MainActivity, errString, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@MainActivity, "Biometric authentication failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
