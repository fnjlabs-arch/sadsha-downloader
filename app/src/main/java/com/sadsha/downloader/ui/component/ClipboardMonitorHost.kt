package com.sadsha.downloader.ui.component

import android.content.ClipboardManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.sadsha.downloader.App
import com.sadsha.downloader.ui.page.downloadv2.configure.DownloadDialogViewModel
import com.sadsha.downloader.util.ClipboardMonitor
import com.sadsha.downloader.util.DownloadUtil
import com.sadsha.downloader.util.MONITOR_CLIPBOARD
import com.sadsha.downloader.util.PreferenceUtil.getBoolean
import com.sadsha.downloader.util.ToastUtil
import com.sadsha.downloader.R

@Composable
fun ClipboardMonitorHost(dialogViewModel: DownloadDialogViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detectedUrl by remember { mutableStateOf<String?>(null) }

    fun checkClipboardForVideoUrl() {
        if (!MONITOR_CLIPBOARD.getBoolean()) return

        runCatching {
            val clip = App.clipboard.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val clipText = clip.getItemAt(0)?.text?.toString()
                val url = ClipboardMonitor.extractVideoUrl(clipText)
                if (url != null && ClipboardMonitor.shouldPrompt(url)) {
                    detectedUrl = url
                }
            }
        }
    }

    // Observe lifecycle ON_RESUME (e.g. returning to Seal after copying link in another app)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkClipboardForVideoUrl()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Also observe clipboard changes while the app is actively running
    DisposableEffect(Unit) {
        val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
            checkClipboardForVideoUrl()
        }
        App.clipboard.addPrimaryClipChangedListener(clipListener)
        onDispose {
            App.clipboard.removePrimaryClipChangedListener(clipListener)
        }
    }

    detectedUrl?.let { url ->
        ClipboardDownloadDialog(
            url = url,
            onDismissRequest = {
                ClipboardMonitor.markAsDismissed(url)
                detectedUrl = null
            },
            onDownloadVideo = { targetUrl ->
                ClipboardMonitor.markAsProcessed(targetUrl)
                detectedUrl = null
                dialogViewModel.postAction(
                    DownloadDialogViewModel.Action.DownloadWithPreset(
                        urlList = listOf(targetUrl),
                        preferences = DownloadUtil.DownloadPreferences.createFromPreferences(),
                    )
                )
                ToastUtil.makeToast(R.string.download_start_msg)
            },
            onDownloadAudio = { targetUrl ->
                ClipboardMonitor.markAsProcessed(targetUrl)
                detectedUrl = null
                dialogViewModel.postAction(
                    DownloadDialogViewModel.Action.DownloadWithPreset(
                        urlList = listOf(targetUrl),
                        preferences =
                            DownloadUtil.DownloadPreferences.createFromPreferences().copy(
                                extractAudio = true
                            ),
                    )
                )
                ToastUtil.makeToast(R.string.download_start_msg)
            },
            onConfigure = { targetUrl ->
                ClipboardMonitor.markAsProcessed(targetUrl)
                detectedUrl = null
                dialogViewModel.postAction(
                    DownloadDialogViewModel.Action.ShowSheet(listOf(targetUrl))
                )
            },
        )
    }
}
