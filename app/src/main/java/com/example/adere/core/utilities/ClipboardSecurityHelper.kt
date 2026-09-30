package com.example.adere.core.utilities

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.PersistableBundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Handles secure clipboard copying and timed auto-clearing.
 *
 * Marks sensitive data on Android 13+ so recent clips UI doesn't leak secrets.
 */
object ClipboardSecurityHelper {

    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        autoClearSeconds: Int = 30,
        coroutineScope: CoroutineScope
    ) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
        val clip = ClipData.newPlainText(label, text)

        // Mark clip as sensitive on API 33+ (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clip.description.extras = PersistableBundle().apply {
                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }

        clipboard.setPrimaryClip(clip)

        if (autoClearSeconds > 0) {
            coroutineScope.launch(Dispatchers.Main) {
                delay(autoClearSeconds * 1000L)
                // Clear clipboard if primary clip matches what we set
                try {
                    val primaryClip = clipboard.primaryClip
                    if (primaryClip != null && primaryClip.itemCount > 0) {
                        val currentText = primaryClip.getItemAt(0)?.text?.toString()
                        if (currentText == text) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                clipboard.clearPrimaryClip()
                            } else {
                                clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignored
                }
            }
        }
    }
}
