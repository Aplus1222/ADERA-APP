package com.example.adere.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.TotalSecurityPrimary

@Composable
fun GeneratorSettingsScreen(
    generatorOptions: PasswordGenerator.GeneratorOptions,
    onUpdateGeneratorOptions: (PasswordGenerator.GeneratorOptions) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Generate live preview sample password
    val sampleResult = remember(generatorOptions) { PasswordGenerator.generate(generatorOptions) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Password Generator Defaults",
                subtitle = "Configure default length & character set preferences",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generator_sample_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sample Output Preview",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${sampleResult.strength.label} (${sampleResult.entropyBits.toInt()} bits)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sampleResult.password,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Section 1: Default Length
            SettingsSectionTitle("Default Password Length")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Length",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${generatorOptions.length} characters",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var sliderVal by remember(generatorOptions.length) { mutableFloatStateOf(generatorOptions.length.toFloat()) }
                    Slider(
                        value = sliderVal,
                        onValueChange = { sliderVal = it },
                        onValueChangeFinished = {
                            onUpdateGeneratorOptions(generatorOptions.copy(length = sliderVal.toInt()))
                        },
                        valueRange = 8f..64f,
                        steps = 55,
                        colors = SliderDefaults.colors(
                            thumbColor = EmeraldPrimary,
                            activeTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("generator_length_slider")
                    )
                }
            }

            // Section 2: Character Sets
            SettingsSectionTitle("Allowed Character Sets")

            SettingsSwitchItem(
                icon = Icons.Default.TextFields,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Uppercase Characters (A-Z)",
                subtitle = "Include capital letters in generated passwords",
                checked = generatorOptions.includeUppercase,
                onCheckedChange = { checked ->
                    validateAndApply(
                        currentOptions = generatorOptions,
                        newUppercase = checked,
                        onUpdate = onUpdateGeneratorOptions,
                        onValidationError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                testTag = "gen_switch_uppercase"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Abc,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Lowercase Characters (a-z)",
                subtitle = "Include small letters in generated passwords",
                checked = generatorOptions.includeLowercase,
                onCheckedChange = { checked ->
                    validateAndApply(
                        currentOptions = generatorOptions,
                        newLowercase = checked,
                        onUpdate = onUpdateGeneratorOptions,
                        onValidationError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                testTag = "gen_switch_lowercase"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Numbers,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Digits & Numbers (0-9)",
                subtitle = "Include numerical digits in generated passwords",
                checked = generatorOptions.includeDigits,
                onCheckedChange = { checked ->
                    validateAndApply(
                        currentOptions = generatorOptions,
                        newDigits = checked,
                        onUpdate = onUpdateGeneratorOptions,
                        onValidationError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                testTag = "gen_switch_digits"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Tag,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Special Symbols (!@#$)",
                subtitle = "Include punctuation and special characters",
                checked = generatorOptions.includeSymbols,
                onCheckedChange = { checked ->
                    validateAndApply(
                        currentOptions = generatorOptions,
                        newSymbols = checked,
                        onUpdate = onUpdateGeneratorOptions,
                        onValidationError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                testTag = "gen_switch_symbols"
            )

            // Section 3: Readability
            SettingsSectionTitle("Readability Rules")

            SettingsSwitchItem(
                icon = Icons.Default.Psychology,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Exclude Ambiguous Characters",
                subtitle = "Omit look-alike characters such as 0, O, I, 1, l",
                checked = generatorOptions.excludeAmbiguous,
                onCheckedChange = { checked ->
                    onUpdateGeneratorOptions(generatorOptions.copy(excludeAmbiguous = checked))
                },
                testTag = "gen_switch_ambiguous"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

private fun validateAndApply(
    currentOptions: PasswordGenerator.GeneratorOptions,
    newUppercase: Boolean = currentOptions.includeUppercase,
    newLowercase: Boolean = currentOptions.includeLowercase,
    newDigits: Boolean = currentOptions.includeDigits,
    newSymbols: Boolean = currentOptions.includeSymbols,
    onUpdate: (PasswordGenerator.GeneratorOptions) -> Unit,
    onValidationError: (String) -> Unit
) {
    if (!newUppercase && !newLowercase && !newDigits && !newSymbols) {
        onValidationError("At least one character type must remain enabled.")
        return
    }

    onUpdate(
        currentOptions.copy(
            includeUppercase = newUppercase,
            includeLowercase = newLowercase,
            includeDigits = newDigits,
            includeSymbols = newSymbols
        )
    )
}
