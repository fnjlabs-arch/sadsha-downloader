package com.sadsha.downloader.ui.page.settings.interaction

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sadsha.downloader.R
import com.sadsha.downloader.ui.common.booleanState
import com.sadsha.downloader.ui.component.BackButton
import com.sadsha.downloader.ui.component.HistoryCleaningDialog
import com.sadsha.downloader.ui.component.PreferenceItem
import com.sadsha.downloader.ui.component.PreferenceSubtitle
import com.sadsha.downloader.ui.component.PreferenceSwitch
import com.sadsha.downloader.util.DatabaseUtil
import com.sadsha.downloader.util.DOWNLOAD_TYPE_INITIALIZATION
import com.sadsha.downloader.util.MONITOR_CLIPBOARD
import com.sadsha.downloader.util.PreferenceUtil.getInt
import com.sadsha.downloader.util.PreferenceUtil.updateBoolean
import com.sadsha.downloader.util.PreferenceUtil.updateInt
import com.sadsha.downloader.util.USE_PREVIOUS_SELECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionPreferencePage(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var showDownloadTypeDialog by remember { mutableStateOf(false) }
    var showCleanHistoryDialog by remember { mutableStateOf(false) }

    var isClipboardMonitorEnabled by MONITOR_CLIPBOARD.booleanState

    val initialType by
        remember(showDownloadTypeDialog) {
            mutableIntStateOf(DOWNLOAD_TYPE_INITIALIZATION.getInt())
        }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.interface_and_interaction)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
        snackbarHost = { SnackbarHost(hostState) },
    ) {
        LazyColumn(modifier = Modifier, contentPadding = it) {
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.settings_before_download))
            }

            item {
                PreferenceItem(
                    title = stringResource(id = R.string.download_type),
                    description =
                        when (initialType) {
                            USE_PREVIOUS_SELECTION ->
                                stringResource(id = R.string.use_previous_selection)
                            else -> stringResource(id = R.string.none)
                        },
                ) {
                    showDownloadTypeDialog = true
                }
            }

            item {
                PreferenceSubtitle(text = stringResource(R.string.monitor_clipboard))
            }

            item {
                PreferenceSwitch(
                    title = stringResource(R.string.monitor_clipboard),
                    description = stringResource(R.string.monitor_clipboard_desc),
                    icon = Icons.Outlined.ContentPaste,
                    isChecked = isClipboardMonitorEnabled,
                    onClick = {
                        val newValue = !isClipboardMonitorEnabled
                        isClipboardMonitorEnabled = newValue
                        MONITOR_CLIPBOARD.updateBoolean(newValue)
                    },
                )
            }

            item {
                PreferenceSubtitle(text = stringResource(R.string.clean_history))
            }

            item {
                PreferenceItem(
                    title = stringResource(R.string.clean_history),
                    description = stringResource(R.string.clean_history_desc),
                    icon = Icons.Outlined.CleaningServices,
                    onClick = { showCleanHistoryDialog = true },
                )
            }
        }
    }

    if (showDownloadTypeDialog) {
        DownloadTypeCustomizationDialog(
            onDismissRequest = { showDownloadTypeDialog = false },
            selectedItem = initialType,
        ) {
            DOWNLOAD_TYPE_INITIALIZATION.updateInt(it)
            showDownloadTypeDialog = false
        }
    }

    if (showCleanHistoryDialog) {
        HistoryCleaningDialog(
            onDismissRequest = { showCleanHistoryDialog = false },
            onCleanMissingFiles = {
                scope.launch(Dispatchers.IO) {
                    val count = DatabaseUtil.cleanMissingFiles()
                    withContext(Dispatchers.Main) {
                        if (count > 0) {
                            hostState.showSnackbar(
                                context.getString(R.string.history_cleaned_msg, count)
                            )
                        } else {
                            hostState.showSnackbar(
                                context.getString(R.string.no_missing_files_msg)
                            )
                        }
                    }
                }
            },
            onClearAllHistory = { deleteFiles ->
                scope.launch(Dispatchers.IO) {
                    val count = DatabaseUtil.clearAllHistory(deleteFiles)
                    withContext(Dispatchers.Main) {
                        hostState.showSnackbar(
                            context.getString(R.string.history_cleaned_msg, count)
                        )
                    }
                }
            },
        )
    }
}
