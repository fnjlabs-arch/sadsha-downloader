package com.sadsha.downloader.ui.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadsha.downloader.util.makeToast

// Neon Palette Constants
val NeonCyan = Color(0xFF00F5FF)
val NeonMagenta = Color(0xFFE040FB)
val NeonPurple = Color(0xFF8B5CF6)
val NeonGreen = Color(0xFF00FF88)
val NeonDarkBg = Color(0xFF0A0F1D)
val NeonCardBg = Color(0xFF11182B)
val NeonCardBorder = Color(0xFF1E2942)

@Composable
fun SadShaFooter(
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showApkDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = if (isCompact) 12.dp else 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Glowing Neon "Powered by SadSha" Badge
        Box(
            modifier =
                Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = NeonCyan.copy(alpha = 0.5f),
                        spotColor = NeonMagenta.copy(alpha = 0.5f),
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    Color(0xFF0D1B2A),
                                    Color(0xFF1B1B3A),
                                    Color(0xFF190F2C),
                                )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush =
                            Brush.linearGradient(
                                colors = listOf(NeonCyan, NeonMagenta, NeonCyan)
                            ),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier =
                        Modifier.size(8.dp)
                            .clip(CircleShape)
                            .background(NeonGreen)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "POWERED BY SADSHA",
                    style =
                        MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.5.sp,
                            fontSize = 12.sp,
                        ),
                    color = Color.White,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = NeonMagenta,
                    modifier = Modifier.size(15.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Official SadSha Studios Ecosystem • Premium Digital Experience",
            style =
                MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                ),
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        // Shortcuts Row: Privacy Policy & APK Direct Download Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Privacy Policy Shortcut
            Surface(
                onClick = { showPrivacyDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Privacy Policy",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            ),
                        color = NeonCyan,
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Direct APK Download Link Button
            Surface(
                onClick = { showApkDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF231435),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta.copy(alpha = 0.6f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = NeonMagenta,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "APK Direct Link",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            ),
                        color = NeonMagenta,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Copyright Notice
        Text(
            text = "© 2026 SadSha Software Studios. All rights reserved.\nPackage: com.sadsha.downloader",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 14.sp,
                ),
            textAlign = TextAlign.Center,
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = Color(0xFF0F172A),
            titleContentColor = NeonCyan,
            textContentColor = Color(0xFFCBD5E1),
            icon = {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(32.dp),
                )
            },
            title = {
                Text(
                    text = "SadSha Privacy Commitment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            text = {
                Column {
                    Text(
                        text =
                            "SadSha Downloader is committed to user transparency and data privacy:\n\n" +
                                "• No Advertisements: This app shows no ads and contains no advertising SDKs. It is 100% free.\n\n" +
                                "• Local & Secure Downloads: Your downloaded videos, extracted MP3 audio, and media files are stored locally in your device's private storage. We never upload or share your media files.\n\n" +
                                "• Zero Personal Data Selling: We do not sell, rent, or trade your personal data or clipboard content.\n\n" +
                                "• Permissions: Network access is used solely for downloading media and loading ads; notification permissions are used for download progress and background audio playback.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = Color.Black,
                        ),
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
        )
    }

    // Direct APK Download / Share Dialog
    if (showApkDialog) {
        AlertDialog(
            onDismissRequest = { showApkDialog = false },
            containerColor = Color(0xFF0F172A),
            titleContentColor = NeonMagenta,
            textContentColor = Color(0xFFCBD5E1),
            icon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = NeonMagenta,
                    modifier = Modifier.size(32.dp),
                )
            },
            title = {
                Text(
                    text = "SadSha Downloader APK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            },
            text = {
                Column {
                    Text(
                        text =
                            "Direct verified standalone build ready for fast installation on any Android device.\n\n" +
                                "• App Name: SadSha Downloader\n" +
                                "• Package: com.sadsha.downloader\n" +
                                "• Signatures: V1 + V2 + V3 Verified\n" +
                                "• Location: apk/SadSha_Downloader.apk\n\n" +
                                "Tap 'Share APK' to send this app installer to your phone or friends via WhatsApp, Telegram, or Nearby Share.",
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showApkDialog = false
                        try {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Download SadSha Downloader (All-in-One High Speed Media Downloader by SadSha): apk/SadSha_Downloader.apk"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share SadSha Downloader APK")
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            context.makeToast("APK ready in file explorer: apk/ folder")
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = NeonMagenta,
                            contentColor = Color.White,
                        ),
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Share APK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApkDialog = false }) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            },
        )
    }
}
