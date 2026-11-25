package com.sadsha.downloader.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadsha.downloader.R

private const val CLEAN_MISSING_FILES = 0
private const val CLEAR_ALL_HISTORY = 1

@Composable
fun HistoryCleaningDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onCleanMissingFiles: () -> Unit,
    onClearAllHistory: (deleteFiles: Boolean) -> Unit,
) {
    var selectedOption by remember { mutableIntStateOf(CLEAN_MISSING_FILES) }
    var deleteFilesFromStorage by remember { mutableStateOf(false) }

    AlertDialog(
        modifier = modifier.testTag("history_cleaning_dialog"),
        shape = RoundedCornerShape(28.dp),
        icon = {
            Icon(
                imageVector = Icons.Outlined.CleaningServices,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.clean_history),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.clean_history_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 0: Clean Missing Files
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedOption = CLEAN_MISSING_FILES }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("clean_missing_radio"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == CLEAN_MISSING_FILES,
                        onClick = { selectedOption = CLEAN_MISSING_FILES },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.clean_missing_files),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.clean_missing_files_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option 1: Clear All History
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedOption = CLEAR_ALL_HISTORY }
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .testTag("clear_all_radio"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOption == CLEAR_ALL_HISTORY,
                        onClick = { selectedOption = CLEAR_ALL_HISTORY },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.clear_all_history),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(R.string.clear_all_history_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // Checkbox: Also delete files from device storage (only when clear all is selected)
                AnimatedVisibility(
                    visible = selectedOption == CLEAR_ALL_HISTORY,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    Row(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(start = 28.dp, top = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    deleteFilesFromStorage = !deleteFilesFromStorage
                                }
                                .padding(vertical = 4.dp)
                                .testTag("delete_files_checkbox"),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = deleteFilesFromStorage,
                            onCheckedChange = { deleteFilesFromStorage = it },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.delete_files_also),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        confirmButton = {
            FilledTonalButton(
                modifier = Modifier.testTag("history_clean_confirm_btn"),
                onClick = {
                    if (selectedOption == CLEAN_MISSING_FILES) {
                        onCleanMissingFiles()
                    } else {
                        onClearAllHistory(deleteFilesFromStorage)
                    }
                    onDismissRequest()
                },
                colors =
                    if (selectedOption == CLEAR_ALL_HISTORY && deleteFilesFromStorage)
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    else
                        ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteSweep,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text =
                        if (selectedOption == CLEAN_MISSING_FILES)
                            stringResource(R.string.clean_missing_files)
                        else stringResource(R.string.clear_all_history)
                )
            }
        },
        dismissButton = {
            TextButton(
                modifier = Modifier.testTag("history_clean_cancel_btn"),
                onClick = onDismissRequest,
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismissRequest,
    )
}
