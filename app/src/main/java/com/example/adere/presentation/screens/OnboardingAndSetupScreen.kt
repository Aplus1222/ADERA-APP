package com.example.adere.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.components.PasswordStrengthBar
import com.example.adere.presentation.components.TotalSecurityHeroIllustration
import com.example.ui.theme.CleanBg
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.CleanSurface
import com.example.ui.theme.CleanSurfaceVariant
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer

private data class OnboardingSlideData(
    val categoryBadge: String,
    val title: String,
    val description: String,
    val chips: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingAndSetupScreen(
    onSetupSuccess: () -> Unit,
    onSetupVault: (password: String, enableBiometrics: Boolean, (Result<Unit>) -> Unit) -> Unit,
    onGetActiveRecoveryKey: () -> Result<String> = { Result.failure(IllegalStateException()) }
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showSetupForm by remember { mutableStateOf(false) }
    var currentSlide by remember { mutableIntStateOf(0) }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var enableBiometrics by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var activeRecoveryKey by remember { mutableStateOf<String?>(null) }
    var userConfirmedSavedKey by remember { mutableStateOf(false) }

    val entropy = remember(password) { PasswordGenerator.calculateEntropy(password) }
    val strength = remember(password, entropy) { PasswordGenerator.evaluateStrength(entropy, password.length) }

    val scrollState = rememberScrollState()

    val slides = remember {
        listOf(
            OnboardingSlideData(
                categoryBadge = "MILITARY-GRADE VAULT",
                title = "Enhance safety with\nTotal security",
                description = "Stop using unsecure passwords for your online accounts. Protect your private credentials with hardware-level AES-256-GCM encryption.",
                chips = listOf("🔒 AES-256-GCM", "🛡️ 100% Offline", "⚡ Zero Knowledge")
            ),
            OnboardingSlideData(
                categoryBadge = "HARDWARE BIOMETRICS",
                title = "Instant Fingerprint\n& Face Unlock",
                description = "Authenticate in milliseconds using Android Biometric sensors and KeyStore keys. Your plaintext secrets are never written to disk.",
                chips = listOf("👆 Fingerprint Auth", "👤 Face Unlock", "🔑 KeyStore Backed")
            ),
            OnboardingSlideData(
                categoryBadge = "TOTAL VISIBILITY",
                title = "Security Health\n& Built-In 2FA",
                description = "Audit weak and reused passwords instantly. Generate strong high-entropy passphrases and offline TOTP two-factor codes.",
                chips = listOf("📊 Health Audit", "⏱️ TOTP Codes", "🎲 Passphrase Engine")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanBg)
    ) {
        AnimatedContent(
            targetState = showSetupForm,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            },
            label = "setup_form_anim"
        ) { isFormVisible ->
            if (!isFormVisible) {
                // Professional Onboarding Carousel with Animated Illustration
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Top Navigation Bar (Badge & Skip Button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = TotalSecurityPrimaryContainer,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = TotalSecurityPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TOTAL SECURITY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TotalSecurityPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }

                        TextButton(
                            onClick = { showSetupForm = true },
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text(
                                text = "Skip",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Fluid Animated Vector Illustration
                    TotalSecurityHeroIllustration(slideIndex = currentSlide)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic Slide Content with animated slide transition
                    AnimatedContent(
                        targetState = currentSlide,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() togetherWith
                                        slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut()
                            } else {
                                slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn() togetherWith
                                        slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                            }
                        },
                        label = "slide_content_anim"
                    ) { index ->
                        val slide = slides[index]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Category Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TotalSecurityPrimaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = slide.categoryBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TotalSecurityPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Title
                            Text(
                                text = slide.title,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkPrimary,
                                    fontSize = 26.sp,
                                    lineHeight = 34.sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Subtitle description
                            Text(
                                text = slide.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkSecondary,
                                    lineHeight = 21.sp,
                                    fontSize = 13.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Feature chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                slide.chips.forEach { chipText ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CleanSurface)
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = chipText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextDarkPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Animated Pill Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        slides.indices.forEach { index ->
                            val isSelected = currentSlide == index
                            val targetWidth by animateDpAsState(
                                targetValue = if (isSelected) 28.dp else 8.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "dot_width"
                            )
                            val targetColor by animateColorAsState(
                                targetValue = if (isSelected) TotalSecurityPrimary else Color(0xFFD1D5DB),
                                animationSpec = tween(300),
                                label = "dot_color"
                            )

                            Box(
                                modifier = Modifier
                                    .size(width = targetWidth, height = 8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(targetColor)
                                    .clickable { currentSlide = index }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Primary Action Button (Continue or Get Started)
                    Button(
                        onClick = {
                            if (currentSlide < slides.size - 1) {
                                currentSlide++
                            } else {
                                showSetupForm = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = TotalSecurityPrimary)
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("onboarding_get_started_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentSlide == slides.size - 1) "Get Started" else "Continue",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (currentSlide == slides.size - 1) Icons.Default.Lock else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary existing vault restore link
                    Text(
                        text = "Have an existing vault backup? Restore",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkMuted,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .clickable { showSetupForm = true }
                            .padding(8.dp)
                            .testTag("onboarding_existing_account_button")
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }
            } else {
                // Master Password Setup View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showSetupForm = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextDarkPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create Master Key",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TotalSecurityPrimaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = TotalSecurityPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Set Master Password",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkPrimary
                                        )
                                    )
                                    Text(
                                        text = "Derives zero-knowledge cryptographic keys",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    errorMessage = null
                                },
                                label = { Text("Master Password") },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextDarkSecondary
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CleanSurfaceVariant,
                                    unfocusedContainerColor = CleanSurfaceVariant,
                                    focusedBorderColor = TotalSecurityPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_master_password_input")
                            )

                            if (password.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                PasswordStrengthBar(strength = strength, entropyBits = entropy)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Confirm Master Password") },
                                singleLine = true,
                                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                        Icon(
                                            imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextDarkSecondary
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CleanSurfaceVariant,
                                    unfocusedContainerColor = CleanSurfaceVariant,
                                    focusedBorderColor = TotalSecurityPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_confirm_password_input")
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Biometrics Toggle
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { enableBiometrics = !enableBiometrics }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = enableBiometrics,
                                    onCheckedChange = { enableBiometrics = it },
                                    colors = CheckboxDefaults.colors(checkedColor = TotalSecurityPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Fingerprint,
                                    contentDescription = null,
                                    tint = TotalSecurityPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Enable Fingerprint / Face Unlock",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDarkPrimary
                                        )
                                    )
                                    Text(
                                        text = "Require biometric verification on app entry",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextDarkSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = HealthRiskRed,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val trimmed = password.trim()
                                    if (trimmed.length < 8) {
                                        errorMessage = "Master password must be at least 8 characters."
                                        return@Button
                                    }
                                    if (trimmed != confirmPassword.trim()) {
                                        errorMessage = "Passwords do not match."
                                        return@Button
                                    }
                                    isProcessing = true
                                    errorMessage = null
                                    onSetupVault(trimmed, enableBiometrics) { result ->
                                        isProcessing = false
                                        if (result.isSuccess) {
                                            val keyRes = onGetActiveRecoveryKey()
                                            if (keyRes.isSuccess && !keyRes.getOrNull().isNullOrBlank()) {
                                                activeRecoveryKey = keyRes.getOrNull()
                                            } else {
                                                onSetupSuccess()
                                            }
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message ?: "Failed to initialize vault."
                                        }
                                    }
                                },
                                enabled = !isProcessing,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = TotalSecurityPrimary)
                                    .testTag("setup_initialize_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TotalSecurityPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Encrypt & Open Vault",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Setup Complete: Master Recovery Key Confirmation Dialog
        if (activeRecoveryKey != null) {
            AlertDialog(
                onDismissRequest = { /* Must explicitly acknowledge key */ },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(TotalSecurityPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = TotalSecurityPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Your Master Recovery Key",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Save this 24-character key in a safe physical place. If you ever forget your Master Password, this key is the ONLY way to regain access to your encrypted vault.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary)
                        )

                        // Key display box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CleanSurfaceVariant),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CleanBorder))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = activeRecoveryKey!!,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextDarkPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Copy Button
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(activeRecoveryKey!!))
                                Toast.makeText(context, "Recovery key copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("setup_copy_recovery_key_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TotalSecurityPrimary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy Recovery Key", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Confirmation checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { userConfirmedSavedKey = !userConfirmedSavedKey }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = userConfirmedSavedKey,
                                onCheckedChange = { userConfirmedSavedKey = it },
                                colors = CheckboxDefaults.colors(checkedColor = TotalSecurityPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "I have written down or safely saved this recovery key",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            activeRecoveryKey = null
                            onSetupSuccess()
                        },
                        enabled = userConfirmedSavedKey,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("setup_finish_vault_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Continue to Vault", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = CleanSurface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
