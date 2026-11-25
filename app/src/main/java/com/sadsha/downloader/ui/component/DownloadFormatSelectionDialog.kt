package com.sadsha.downloader.ui.component

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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadsha.downloader.util.CONVERT_M4A
import com.sadsha.downloader.util.CONVERT_MP3
import com.sadsha.downloader.util.DownloadUtil
import com.sadsha.downloader.util.FORMAT_COMPATIBILITY
import com.sadsha.downloader.util.FORMAT_QUALITY
import com.sadsha.downloader.util.RES_1080P
import com.sadsha.downloader.util.RES_1440P
import com.sadsha.downloader.util.RES_2160P
import com.sadsha.downloader.util.RES_360P
import com.sadsha.downloader.util.RES_480P
import com.sadsha.downloader.util.RES_720P
import com.sadsha.downloader.util.RES_HIGHEST

enum class MediaTypeSelection {
    VIDEO,
    AUDIO,
}

data class VideoQualityOption(
    val title: String,
    val subtitle: String,
    val resolutionValue: Int,
    val badge: String? = null,
)

data class AudioFormatOption(
    val title: String,
    val subtitle: String,
    val formatValue: Int, // CONVERT_MP3 or CONVERT_M4A
    val qualityValue: Int, // 1: Best, 2: Standard
    val badge: String? = null,
)

val VideoQualityOptions =
    listOf(
        VideoQualityOption("Best Available", "Maximum resolution & bitrate", RES_HIGHEST, "AUTO"),
        VideoQualityOption("1080p Full HD", "Recommended for smartphones", RES_1080P, "1080p"),
        VideoQualityOption("720p HD", "Balanced size & great quality", RES_720P, "720p"),
        VideoQualityOption("480p SD", "Fast download & smaller file", RES_480P, "480p"),
        VideoQualityOption("4K Ultra HD", "Crisp for large screens (2160p)", RES_2160P, "4K"),
    )

val AudioOptions =
    listOf(
        AudioFormatOption(
            title = "MP3 (High Quality)",
            subtitle = "320 kbps • Maximum compatibility",
            formatValue = CONVERT_MP3,
            qualityValue = 1,
            badge = "MP3 320k",
        ),
        AudioFormatOption(
            title = "MP3 (Standard Quality)",
            subtitle = "192 kbps • Fast download",
            formatValue = CONVERT_MP3,
            qualityValue = 2,
            badge = "MP3 192k",
        ),
        AudioFormatOption(
            title = "M4A / AAC (High Quality)",
            subtitle = "Native AAC audio • Crystal clear sound",
            formatValue = CONVERT_M4A,
            qualityValue = 1,
            badge = "M4A AAC",
        ),
        AudioFormatOption(
            title = "M4A / AAC (Standard Quality)",
            subtitle = "Standard bitrate • Very compact",
            formatValue = CONVERT_M4A,
            qualityValue = 2,
            badge = "M4A",
        ),
    )

@Composable
fun DownloadFormatSelectionDialog(
    url: String,
    basePreferences: DownloadUtil.DownloadPreferences = DownloadUtil.DownloadPreferences.createFromPreferences(),
    onDismissRequest: () -> Unit,
    onConfirmDownload: (DownloadUtil.DownloadPreferences) -> Unit,
) {
    var selectedMediaType by remember { mutableStateOf(MediaTypeSelection.VIDEO) }
    var selectedVideoQualityIndex by remember { mutableIntStateOf(0) }
    var selectedAudioOptionIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier =
                            Modifier.size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector =
                                if (selectedMediaType == MediaTypeSelection.VIDEO)
                                    Icons.Default.Movie
                                else Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Column {
                        Text(
                            text = "Download Options",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Choose media format & quality",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Display target URL preview
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                ) {
                    Text(
                        text = url,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }

                // Tab Switcher: Video (MP4) vs Audio (MP3/M4A)
                TabRow(
                    selectedTabIndex = selectedMediaType.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(14.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedMediaType.ordinal]),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    divider = {},
                ) {
                    Tab(
                        selected = selectedMediaType == MediaTypeSelection.VIDEO,
                        onClick = { selectedMediaType = MediaTypeSelection.VIDEO },
                        modifier = Modifier.height(44.dp),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "Video (MP4)",
                                    fontWeight =
                                        if (selectedMediaType == MediaTypeSelection.VIDEO)
                                            FontWeight.Bold
                                        else FontWeight.Medium,
                                )
                            }
                        },
                    )

                    Tab(
                        selected = selectedMediaType == MediaTypeSelection.AUDIO,
                        onClick = { selectedMediaType = MediaTypeSelection.AUDIO },
                        modifier = Modifier.height(44.dp),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = "Audio (MP3/M4A)",
                                    fontWeight =
                                        if (selectedMediaType == MediaTypeSelection.AUDIO)
                                            FontWeight.Bold
                                        else FontWeight.Medium,
                                )
                            }
                        },
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Options list based on selected media type
                if (selectedMediaType == MediaTypeSelection.VIDEO) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        VideoQualityOptions.forEachIndexed { index, option ->
                            val isSelected = selectedVideoQualityIndex == index
                            Surface(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedVideoQualityIndex = index },
                                color =
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surfaceContainerLow,
                                border =
                                    if (isSelected)
                                        androidx.compose.foundation.BorderStroke(
                                            1.5.dp,
                                            MaterialTheme.colorScheme.primary,
                                        )
                                    else
                                        androidx.compose.foundation.BorderStroke(
                                            0.5.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.5f
                                            ),
                                        ),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (isSelected) Icons.Default.RadioButtonChecked
                                                else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint =
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = option.title,
                                                style =
                                                    MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight =
                                                            if (isSelected) FontWeight.Bold
                                                            else FontWeight.Medium
                                                    ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Text(
                                                text = option.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }

                                    option.badge?.let { badgeText ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color =
                                                if (isSelected)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                        ) {
                                            Text(
                                                text = badgeText,
                                                style =
                                                    MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                    ),
                                                color =
                                                    if (isSelected)
                                                        MaterialTheme.colorScheme.onPrimary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 3.dp,
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Audio selection options (MP3 & M4A)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        AudioOptions.forEachIndexed { index, option ->
                            val isSelected = selectedAudioOptionIndex == index
                            Surface(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedAudioOptionIndex = index },
                                color =
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surfaceContainerLow,
                                border =
                                    if (isSelected)
                                        androidx.compose.foundation.BorderStroke(
                                            1.5.dp,
                                            MaterialTheme.colorScheme.primary,
                                        )
                                    else
                                        androidx.compose.foundation.BorderStroke(
                                            0.5.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(
                                                alpha = 0.5f
                                            ),
                                        ),
                                shape = RoundedCornerShape(14.dp),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        Icon(
                                            imageVector =
                                                if (isSelected) Icons.Default.RadioButtonChecked
                                                else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint =
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp),
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = option.title,
                                                style =
                                                    MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight =
                                                            if (isSelected) FontWeight.Bold
                                                            else FontWeight.Medium
                                                    ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Text(
                                                text = option.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }

                                    option.badge?.let { badgeText ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color =
                                                if (isSelected)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.surfaceContainerHighest,
                                        ) {
                                            Text(
                                                text = badgeText,
                                                style =
                                                    MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                    ),
                                                color =
                                                    if (isSelected)
                                                        MaterialTheme.colorScheme.onPrimary
                                                    else
                                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 3.dp,
                                                    ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalPreferences =
                        if (selectedMediaType == MediaTypeSelection.VIDEO) {
                            val chosenOption = VideoQualityOptions[selectedVideoQualityIndex]
                            basePreferences.copy(
                                extractAudio = false,
                                videoResolution = chosenOption.resolutionValue,
                                videoFormat = FORMAT_COMPATIBILITY, // Ensures MP4 compatibility
                            )
                        } else {
                            val chosenAudio = AudioOptions[selectedAudioOptionIndex]
                            basePreferences.copy(
                                extractAudio = true,
                                convertAudio = true,
                                audioConvertFormat = chosenAudio.formatValue,
                                audioQuality = chosenAudio.qualityValue,
                            )
                        }
                    onConfirmDownload(finalPreferences)
                },
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Start Download",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Cancel")
            }
        },
    )
}
