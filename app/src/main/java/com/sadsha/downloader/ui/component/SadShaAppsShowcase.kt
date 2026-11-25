package com.sadsha.downloader.ui.component

import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadsha.downloader.R
import com.sadsha.downloader.util.makeToast

data class SadShaAppItem(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val badge: String,
    val badgeColor: Color,
    @DrawableRes val logoRes: Int,
    val isCurrentApp: Boolean = false,
    val description: String,
    val features: List<String>,
)

val SADSHA_ECOSYSTEM_APPS =
    listOf(
        SadShaAppItem(
            id = "keyboard",
            name = "Sadsha Keyboard",
            subtitle = "Smart Custom RGB Keyboard",
            category = "Tools & Personalization",
            badge = "🔥 POPULAR",
            badgeColor = Color(0xFFE040FB),
            logoRes = R.drawable.img_sadsha_keyboard,
            description =
                "A next-gen custom keyboard featuring radiant RGB dynamic backlight animations, precision AI typing predictions, mechanical switch tactile sounds, and multi-language support.",
            features =
                listOf(
                    "Dynamic RGB Backlight Animations",
                    "Smart AI Predictive Text & Gestures",
                    "Mechanical Switch Key Sound Effects",
                    "Extensive Custom Themes & Font Engine",
                ),
        ),
        SadShaAppItem(
            id = "netguard",
            name = "Sadsha Net Guard",
            subtitle = "Data Security & Firewall Shield",
            category = "Security & Privacy",
            badge = "🛡️ SECURE",
            badgeColor = Color(0xFF00F5FF),
            logoRes = R.drawable.img_sadsha_netguard,
            description =
                "The ultimate no-root firewall and device privacy shield. Control network internet access on a per-app basis, prevent background bandwidth drain, and block tracker beacons.",
            features =
                listOf(
                    "Zero-Root Mobile Firewall Protection",
                    "Per-App Wi-Fi & Mobile Data Blocking",
                    "Real-Time Data Consumption Live Graphs",
                    "Anti-Tracker & Spyware Privacy Guard",
                ),
        ),
        SadShaAppItem(
            id = "downloader",
            name = "Sadsha Downloader",
            subtitle = "All-in-One High Speed Media Downloader",
            category = "Media & Utility",
            badge = "⚡ CURRENT APP",
            badgeColor = Color(0xFF00FF88),
            logoRes = R.drawable.img_sadsha_logo,
            isCurrentApp = true,
            description =
                "Ultra-fast multi-threaded video and audio downloader supporting HD, 2K, 4K resolutions and high-bitrate MP3 audio extraction with built-in background playback.",
            features =
                listOf(
                    "Multi-Threaded 4K & Full HD Downloading",
                    "High-Fidelity MP3 Audio Converter",
                    "Background Audio Streaming Player",
                    "Private Encrypted Download Storage",
                ),
        ),
        SadShaAppItem(
            id = "picshrink",
            name = "Sadsha Pic Shrink",
            subtitle = "Lossless Image Compressor & Resizer",
            category = "Utility & Photography",
            badge = "📉 90% SAVE",
            badgeColor = Color(0xFFFFB703),
            logoRes = R.drawable.img_sadsha_picshrink,
            description =
                "Smart lossless photo compressor that slashes image file size up to 90% while maintaining crisp optical clarity. Ideal for sharing on WhatsApp, web portals, and saving phone space.",
            features =
                listOf(
                    "Lossless Quality Preservation Algorithm",
                    "Compress up to 90% Storage Footprint",
                    "Batch Processing for Entire Albums",
                    "Custom Resolution Scaling & EXIF Stripper",
                ),
        ),
        SadShaAppItem(
            id = "stickcricket",
            name = "Sadsha Stick Cricket",
            subtitle = "Arcade Premier Cricket Game",
            category = "Sports & Arcade Games",
            badge = "🏏 ARCADE HIT",
            badgeColor = Color(0xFFFF0055),
            logoRes = R.drawable.img_sadsha_cricket,
            description =
                "Fast, addictive arcade cricket experience! Smash towering sixes, conquer international premier league cups, and outsmart world-class bowlers in thrilling run chases.",
            features =
                listOf(
                    "One-Tap Precision Batting Mechanics",
                    "Premier T20 World Tournaments",
                    "Dynamic Crowd Audio & Atmospheric Stadiums",
                    "Leaderboards & Multiplayer Challenges",
                ),
        ),
        SadShaAppItem(
            id = "imgtrail",
            name = "SadSha Image Trail",
            subtitle = "Dynamic Photo Viewer & Gallery",
            category = "Media & Gallery",
            badge = "📸 ULTRA HDR",
            badgeColor = Color(0xFF7000FF),
            logoRes = R.drawable.img_sadsha_imgtrail,
            description =
                "A next-level gallery app with fluid kinetic motion transitions, smart chronological timeline sorting, HDR color rendering, and private vault folders.",
            features =
                listOf(
                    "Ultra-Fluid 3D Spatial Scroll Experience",
                    "HDR Display Optimization for Photos",
                    "Private Fingerprint Protected Vault",
                    "Smart Album Deduplication & Tagging",
                ),
        ),
        SadShaAppItem(
            id = "graviblock",
            name = "SadSha GraviBlock 1010",
            subtitle = "Addictive Jelly Block Puzzle Game",
            category = "Puzzle & Casual Games",
            badge = "🧩 BRAIN FUN",
            badgeColor = Color(0xFF00E5FF),
            logoRes = R.drawable.img_sadsha_graviblock,
            description =
                "The most satisfying jelly block puzzle! Drop vibrating colorful blocks onto the 10x10 gravity board, trigger multi-row explosive combo clears, and train your cognitive agility.",
            features =
                listOf(
                    "Satisfying Jelly Physics & Haptic Feedback",
                    "10x10 Grid Endless Strategy Puzzle",
                    "Vibrant Neon Cyber Glow Visuals",
                    "Offline Play Anywhere Without Internet",
                ),
        ),
    )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SadShaAppsShowcase(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedAppForDialog by remember { mutableStateOf<SadShaAppItem?>(null) }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hero Header Badge
        Box(
            modifier =
                Modifier
                    .shadow(16.dp, RoundedCornerShape(24.dp), ambientColor = NeonCyan, spotColor = NeonMagenta)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors =
                                listOf(
                                    Color(0xFF0B132B),
                                    Color(0xFF1C1133),
                                    Color(0xFF0F172A),
                                )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonCyan, NeonMagenta, NeonCyan)),
                        RoundedCornerShape(24.dp),
                    )
                    .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonMagenta.copy(alpha = 0.2f))
                                .border(1.dp, NeonMagenta, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "7 OFFICIAL APPS",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.8.sp,
                                    fontSize = 11.sp,
                                ),
                            color = NeonMagenta,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREMIUM ECOSYSTEM",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.8.sp,
                                    fontSize = 11.sp,
                                ),
                            color = NeonCyan,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "More Apps by SadSha",
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                        ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                // The User Provided Message Block
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF090E1A).copy(alpha = 0.85f),
                    border =
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            NeonCyan.copy(alpha = 0.35f),
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FormatQuote,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text =
                                "Discover more powerful tools and exciting games crafted by SadSha! Try these apps to supercharge your daily mobile experience — from smart typing & security to high-speed media tools and arcade gaming. Tap any app below to explore!",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Normal,
                                ),
                            color = Color(0xFFE2E8F0),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Grid / List of the 7 Apps
        SADSHA_ECOSYSTEM_APPS.forEach { appItem ->
            SadShaAppCard(
                appItem = appItem,
                onClick = {
                    selectedAppForDialog = appItem
                },
                modifier = Modifier.padding(bottom = 14.dp),
            )
        }
    }

    // App Detail Modal Dialog
    selectedAppForDialog?.let { app ->
        AlertDialog(
            onDismissRequest = { selectedAppForDialog = null },
            containerColor = Color(0xFF0D1322),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFCBD5E1),
            icon = {
                Image(
                    painter = painterResource(id = app.logoRes),
                    contentDescription = app.name,
                    modifier =
                        Modifier.size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(2.dp, app.badgeColor, RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = app.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = app.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = app.badgeColor,
                        textAlign = TextAlign.Center,
                    )
                }
            },
            text = {
                Column {

                    Text(
                        text = app.description,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = Color(0xFFCBD5E1),
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Key Highlights:",
                        style =
                            MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                            ),
                    )
                    Spacer(Modifier.height(6.dp))
                    app.features.forEach { feat ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(6.dp)
                                        .clip(CircleShape)
                                        .background(app.badgeColor)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = feat,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE2E8F0),
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedAppForDialog = null
                        if (app.isCurrentApp) {
                            context.makeToast("You are currently enjoying SadSha Downloader!")
                        } else {
                            try {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out ${app.name} (${app.subtitle}) by SadSha: Crafted for high performance mobile experience!"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Explore ${app.name}"))
                            } catch (e: Exception) {
                                context.makeToast("Opening ${app.name}...")
                            }
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = app.badgeColor,
                            contentColor = if (app.badgeColor == NeonGreen || app.badgeColor == NeonCyan) Color.Black else Color.White,
                        ),
                ) {
                    if (app.isCurrentApp) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Active App", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Explore App", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAppForDialog = null }) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            },
        )
    }
}

@Composable
fun SadShaAppCard(
    appItem: SadShaAppItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = appItem.badgeColor.copy(alpha = 0.25f),
                    spotColor = appItem.badgeColor.copy(alpha = 0.35f),
                )
                .border(
                    width = 1.2.dp,
                    brush =
                        Brush.horizontalGradient(
                            colors =
                                listOf(
                                    appItem.badgeColor.copy(alpha = 0.6f),
                                    Color(0xFF26324A),
                                    appItem.badgeColor.copy(alpha = 0.2f),
                                )
                        ),
                    shape = RoundedCornerShape(18.dp),
                ),
        shape = RoundedCornerShape(18.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF0F1728),
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // App Logo
            Box(
                modifier =
                    Modifier.size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, appItem.badgeColor, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = appItem.logoRes),
                    contentDescription = appItem.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(Modifier.width(14.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                // App Name - Prominent and never squeezed
                Text(
                    text = appItem.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            letterSpacing = 0.2.sp,
                        ),
                    color = Color.White,
                    maxLines = 2,
                )

                Spacer(Modifier.height(4.dp))

                // Badge Chip & Category Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(appItem.badgeColor.copy(alpha = 0.22f))
                                .border(1.dp, appItem.badgeColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = appItem.badge,
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.4.sp,
                                ),
                            color = appItem.badgeColor,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = appItem.category,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        maxLines = 1,
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = appItem.subtitle,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    color = Color(0xFF94A3B8),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

            }

            Spacer(Modifier.width(8.dp))

            // Action Chevron / Button
            Surface(
                shape = CircleShape,
                color =
                    if (appItem.isCurrentApp) NeonGreen.copy(alpha = 0.2f)
                    else appItem.badgeColor.copy(alpha = 0.15f),
                border =
                    androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (appItem.isCurrentApp) NeonGreen else appItem.badgeColor,
                    ),
                modifier = Modifier.size(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector =
                            if (appItem.isCurrentApp) Icons.Default.CheckCircle
                            else Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = if (appItem.isCurrentApp) NeonGreen else appItem.badgeColor,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
