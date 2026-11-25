package com.sadsha.downloader.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadsha.downloader.ui.common.HapticFeedback.slightHapticFeedback
import com.sadsha.downloader.util.matchUrlFromSharedText

@Composable
fun HomeUrlInputCard(
    modifier: Modifier = Modifier,
    onDownloadUrl: (String) -> Unit = {},
    onQuickAudioDownload: ((String) -> Unit)? = null,
    onPlayUrl: ((String) -> Unit)? = null,
) {
    var urlText by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val view = LocalView.current

    val onPasteClicked: () -> Unit = {
        view.slightHapticFeedback()
        val clipText = clipboardManager.getText()?.text?.trim()
        if (!clipText.isNullOrEmpty()) {
            val extracted = matchUrlFromSharedText(clipText)
            urlText = if (extracted.isNotEmpty()) extracted else clipText
        }
    }

    val onSubmit: () -> Unit = {
        val trimmed = urlText.trim()
        if (trimmed.isNotEmpty()) {
            val matched = matchUrlFromSharedText(trimmed)
            val finalUrl = if (matched.isNotEmpty()) matched else trimmed
            focusManager.clearFocus()
            view.slightHapticFeedback()
            onDownloadUrl(finalUrl)
        }
    }

    val onAudioSubmit: () -> Unit = {
        val trimmed = urlText.trim()
        if (trimmed.isNotEmpty()) {
            val matched = matchUrlFromSharedText(trimmed)
            val finalUrl = if (matched.isNotEmpty()) matched else trimmed
            focusManager.clearFocus()
            view.slightHapticFeedback()
            if (onQuickAudioDownload != null) {
                onQuickAudioDownload(finalUrl)
            } else {
                onDownloadUrl(finalUrl)
            }
        }
    }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .border(
                    width = 1.2.dp,
                    brush =
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF00F5FF).copy(alpha = 0.8f),
                                Color(0xFFE040FB).copy(alpha = 0.8f),
                                Color(0xFF00F5FF).copy(alpha = 0.5f),
                            )
                        ),
                    shape = RoundedCornerShape(22.dp),
                ),
        shape = RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF0D1424),
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Link Icon & Title + Paste button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            Modifier.size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF00F5FF),
                                            Color(0xFFE040FB),
                                        )
                                    )
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Download Video or Audio",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                ),
                            color = Color.White,
                        )
                        Text(
                            text = "SadSha High-Speed Engine • 4K & MP3",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF00F5FF),
                        )
                    }
                }

                // Quick Paste pill button
                Surface(
                    onClick = onPasteClicked,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF241438),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE040FB)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = "Paste",
                            tint = Color(0xFFE040FB),
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Paste",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            color = Color(0xFFE040FB),
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // URL Input field
            OutlinedTextField(
                value = urlText,
                onValueChange = { urlText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Paste link from YouTube, Instagram, TikTok...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B),
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00F5FF),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF080C16),
                        unfocusedContainerColor = Color(0xFF080C16),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go,
                    ),
                keyboardActions = KeyboardActions(onGo = { onSubmit() }),
                trailingIcon = {
                    if (urlText.isNotEmpty()) {
                        IconButton(onClick = { urlText = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                },
            )

            Spacer(Modifier.height(12.dp))

            // Action Buttons: "Download Video", "Play / Listen", & "MP3 Audio"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Primary Video Download Button
                Button(
                    onClick = onSubmit,
                    enabled = urlText.isNotBlank(),
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00F5FF),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF1E293B),
                            disabledContentColor = Color(0xFF64748B),
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Download",
                        style =
                            MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                    )
                }

                // Play / In-App Player Button
                if (onPlayUrl != null) {
                    Button(
                        onClick = {
                            val trimmed = urlText.trim()
                            val matched = matchUrlFromSharedText(trimmed)
                            val finalUrl = if (matched.isNotEmpty()) matched else trimmed
                            focusManager.clearFocus()
                            view.slightHapticFeedback()
                            onPlayUrl(finalUrl)
                        },
                        enabled = urlText.isNotBlank(),
                        modifier = Modifier.height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFF1E293B),
                                disabledContentColor = Color(0xFF64748B),
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play in player",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Play",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                ),
                        )
                    }
                }

                // Audio extraction shortcut button
                OutlinedButton(
                    onClick = onAudioSubmit,
                    enabled = urlText.isNotBlank(),
                    modifier = Modifier.height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE040FB),
                            disabledContentColor = Color(0xFF64748B),
                        ),
                    border =
                        androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (urlText.isNotBlank()) Color(0xFFE040FB) else Color(0xFF334155),
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Audiotrack,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "MP3",
                        style =
                            MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            ),
                    )
                }
            }
        }
    }
}
