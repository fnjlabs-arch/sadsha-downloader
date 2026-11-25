package com.sadsha.downloader.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sadsha.downloader.ui.common.HapticFeedback.slightHapticFeedback
import com.sadsha.downloader.util.DownloadUtil
import com.sadsha.downloader.util.TrendingVideo

@Composable
fun BatchDownloadDialog(
    selectedVideos: List<TrendingVideo>,
    basePreferences: DownloadUtil.DownloadPreferences,
    onDismissRequest: () -> Unit,
    onConfirmBatchDownload: (DownloadUtil.DownloadPreferences) -> Unit,
) {
    val view = LocalView.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Video, 1 = Audio

    // Video resolution: 0 = Best, 1 = 1080p, 2 = 720p, 3 = 4K
    var selectedVideoResolution by remember { mutableIntStateOf(1) } // default 1080p

    // Audio format: 0 = MP3, 1 = M4A
    var selectedAudioFormat by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier.size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Batch Download",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "${selectedVideos.size} items selected",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
        text = {
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Select quality and format to process all selected items in a single batch request.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                // Tab Row: Video vs Audio
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            view.slightHapticFeedback()
                            selectedTab = 0
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Video (MP4)")
                            }
                        },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            view.slightHapticFeedback()
                            selectedTab = 1
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Audio (MP3)")
                            }
                        },
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Options based on tab
                if (selectedTab == 0) {
                    Text(
                        text = "Resolution Preset:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    val resolutions =
                        listOf(
                            Triple(0, "Auto (Best Available)", "High speed best resolution"),
                            Triple(1, "1080p Full HD", "Recommended for smartphones"),
                            Triple(2, "720p HD", "Faster download & less data"),
                            Triple(3, "4K UHD (2160p)", "Maximum Ultra HD fidelity"),
                        )

                    resolutions.forEach { (index, title, desc) ->
                        val isSelected = selectedVideoResolution == index
                        Surface(
                            onClick = {
                                view.slightHapticFeedback()
                                selectedVideoResolution = index
                            },
                            shape = RoundedCornerShape(10.dp),
                            color =
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector =
                                        if (isSelected) Icons.Default.RadioButtonChecked
                                        else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint =
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        style =
                                            MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight =
                                                    if (isSelected) FontWeight.Bold
                                                    else FontWeight.Normal
                                            ),
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Audio Extraction Format:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    val formats =
                        listOf(
                            Triple(0, "MP3 (320kbps High Quality)", "Compatible with all players"),
                            Triple(1, "M4A / AAC", "Native crystal-clear audio"),
                        )

                    formats.forEach { (index, title, desc) ->
                        val isSelected = selectedAudioFormat == index
                        Surface(
                            onClick = {
                                view.slightHapticFeedback()
                                selectedAudioFormat = index
                            },
                            shape = RoundedCornerShape(10.dp),
                            color =
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector =
                                        if (isSelected) Icons.Default.RadioButtonChecked
                                        else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint =
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        style =
                                            MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight =
                                                    if (isSelected) FontWeight.Bold
                                                    else FontWeight.Normal
                                            ),
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Preview list of selected items
                Text(
                    text = "Selected Videos Queue (${selectedVideos.size}):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp),
                )

                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        selectedVideos.take(4).forEach { video ->
                            Text(
                                text = "• ${video.title}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(vertical = 2.dp),
                            )
                        }
                        if (selectedVideos.size > 4) {
                            Text(
                                text = "+ ${selectedVideos.size - 4} more items",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    view.slightHapticFeedback()
                    val isAudio = selectedTab == 1
                    val chosenPrefs =
                        if (isAudio) {
                            basePreferences.copy(
                                extractAudio = true,
                                audioFormat = selectedAudioFormat,
                            )
                        } else {
                            basePreferences.copy(
                                extractAudio = false,
                                videoResolution =
                                    when (selectedVideoResolution) {
                                        3 -> 2160
                                        1 -> 1080
                                        2 -> 720
                                        else -> 0
                                    },
                            )
                        }
                    onConfirmBatchDownload(chosenPrefs)
                },
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
                Text("Start Batch (${selectedVideos.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
    )
}
