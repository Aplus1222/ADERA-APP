package com.example.adere.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityOrange

@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Help & Support",
                subtitle = "Frequently asked questions & contact support",
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
            // Section 1: FAQ
            SettingsSectionTitle("Frequently Asked Questions")

            FaqExpandableCard(
                question = "How is my Master Password protected?",
                answer = "Adera uses Zero-Knowledge architecture. Your master password is never stored anywhere—neither on disk nor in memory. We derive a 256-bit key using cryptographic PBKDF2/Argon2id and a unique salt to decrypt your vault key."
            )

            FaqExpandableCard(
                question = "What if I forget my Master Password?",
                answer = "You can recover your vault using your 32-character Master Recovery Key. Go to Settings > Security to view or regenerate your recovery key. Without either your password or recovery key, nobody can access your vault."
            )

            FaqExpandableCard(
                question = "How does Zero-Knowledge encryption work?",
                answer = "All encryption and decryption happens exclusively on your local device using AES-256-GCM. Unencrypted data never touches disk or external servers."
            )

            FaqExpandableCard(
                question = "Are my passwords backed up to the cloud?",
                answer = "Adera operates 100% offline by default. You can create encrypted .adere backup files or printable PDF master sheets in Settings > Backup & Restore."
            )

            // Section 2: Contact Support
            SettingsSectionTitle("Contact Support & Feedback")

            SettingsItemCard(
                icon = Icons.Default.Email,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Contact Support Email",
                subtitle = "Send an email inquiry or support request to Adera engineers",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@adera.app")
                            putExtra(Intent.EXTRA_SUBJECT, "Adera Vault Support Request")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "help_contact_support"
            )

            SettingsItemCard(
                icon = Icons.Default.BugReport,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Report a Problem",
                subtitle = "Report a bug, technical glitch, or suggest a new feature",
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:bugs@adera.app")
                            putExtra(Intent.EXTRA_SUBJECT, "Adera Bug Report")
                        }
                        context.startActivity(Intent.createChooser(intent, "Report Bug"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "help_report_bug"
            )

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun FaqExpandableCard(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .testTag("faq_card_${question.take(15).lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}
