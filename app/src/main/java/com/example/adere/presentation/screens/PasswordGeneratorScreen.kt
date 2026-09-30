package com.example.adere.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.components.PasswordStrengthBar
import com.example.ui.theme.CharcoalBg
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    generatedResult: PasswordGenerator.GenerationResult,
    options: PasswordGenerator.GeneratorOptions,
    onOptionsChange: (PasswordGenerator.GeneratorOptions) -> Unit,
    onRegenerate: () -> Unit,
    onCopyPassword: (String) -> Unit,
    onSaveToVault: (String) -> Unit,
    onLockClick: () -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Generator",
                subtitle = "Cryptographically Secure Entropy",
                onLockClick = onLockClick
            )
        },
        containerColor = CharcoalBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Output Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generator_output_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldLight))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Generated Secret",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPasswordVisible) generatedResult.password else "••••••••••••••••••••",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("generated_password_text")
                        )
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle visibility",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    PasswordStrengthBar(
                        strength = generatedResult.strength,
                        entropyBits = generatedResult.entropyBits
                    )
                }
            }

            // Action Buttons Row: Generate & Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRegenerate,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("generator_refresh_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CharcoalSurface,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = EmeraldLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Regenerate")
                }

                Button(
                    onClick = { onCopyPassword(generatedResult.password) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("generator_copy_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF003824)
                    )
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy", fontWeight = FontWeight.Bold)
                }
            }

            // Save to Vault Button
            OutlinedButton(
                onClick = { onSaveToVault(generatedResult.password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generator_save_to_vault_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EmeraldLight)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Vault", color = EmeraldLight, fontWeight = FontWeight.SemiBold)
            }

            // Controls Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CharcoalBorder))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Configuration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Switch: Random Characters vs Passphrase
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Memorable Passphrase Mode",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                        )
                        Switch(
                            checked = options.isPassphraseMode,
                            onCheckedChange = { onOptionsChange(options.copy(isPassphraseMode = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldPrimary,
                                checkedTrackColor = CharcoalBg
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (options.isPassphraseMode) {
                        // Word Count Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Word Count", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                            Text("${options.passphraseWordCount} words", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldLight))
                        }
                        Slider(
                            value = options.passphraseWordCount.toFloat(),
                            onValueChange = { onOptionsChange(options.copy(passphraseWordCount = it.toInt())) },
                            valueRange = 3f..8f,
                            steps = 4,
                            colors = SliderDefaults.colors(thumbColor = EmeraldLight, activeTrackColor = EmeraldPrimary)
                        )
                    } else {
                        // Length Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Password Length", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                            Text("${options.length} chars", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldLight))
                        }
                        Slider(
                            value = options.length.toFloat(),
                            onValueChange = { onOptionsChange(options.copy(length = it.toInt())) },
                            valueRange = 6f..64f,
                            colors = SliderDefaults.colors(thumbColor = EmeraldLight, activeTrackColor = EmeraldPrimary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Character Type Checkboxes
                        GeneratorOptionRow(
                            title = "Uppercase Letters (A-Z)",
                            checked = options.includeUppercase,
                            onCheckedChange = { onOptionsChange(options.copy(includeUppercase = it)) }
                        )
                        GeneratorOptionRow(
                            title = "Lowercase Letters (a-z)",
                            checked = options.includeLowercase,
                            onCheckedChange = { onOptionsChange(options.copy(includeLowercase = it)) }
                        )
                        GeneratorOptionRow(
                            title = "Numbers (0-9)",
                            checked = options.includeDigits,
                            onCheckedChange = { onOptionsChange(options.copy(includeDigits = it)) }
                        )
                        GeneratorOptionRow(
                            title = "Symbols (!@#$%)",
                            checked = options.includeSymbols,
                            onCheckedChange = { onOptionsChange(options.copy(includeSymbols = it)) }
                        )
                        GeneratorOptionRow(
                            title = "Exclude Ambiguous (0, O, 1, l)",
                            checked = options.excludeAmbiguous,
                            onCheckedChange = { onOptionsChange(options.copy(excludeAmbiguous = it)) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GeneratorOptionRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary))
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = EmeraldPrimary,
                checkmarkColor = CharcoalBg
            )
        )
    }
}
