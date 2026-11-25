package com.sadsha.downloader.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadsha.downloader.Downloader
import com.sadsha.downloader.util.PreferenceUtil
import com.sadsha.downloader.util.PreferenceUtil.getBoolean
import com.sadsha.downloader.util.PreferenceUtil.getLong
import com.sadsha.downloader.util.PreferenceUtil.getString
import com.sadsha.downloader.util.UpdateUtil
import com.sadsha.downloader.util.YT_DLP_AUTO_UPDATE
import com.sadsha.downloader.util.YT_DLP_UPDATE_INTERVAL
import com.sadsha.downloader.util.YT_DLP_UPDATE_TIME
import com.sadsha.downloader.util.YT_DLP_VERSION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun YtdlpUpdater() {

    val downloaderState by Downloader.downloaderState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (downloaderState !is Downloader.State.Idle) return@LaunchedEffect

        if (!YT_DLP_AUTO_UPDATE.getBoolean() && YT_DLP_VERSION.getString().isNotEmpty())
            return@LaunchedEffect

        if (!PreferenceUtil.isNetworkAvailableForDownload()) {
            return@LaunchedEffect
        }

        val lastUpdateTime = YT_DLP_UPDATE_TIME.getLong()
        val currentTime = System.currentTimeMillis()

        if (currentTime < lastUpdateTime + YT_DLP_UPDATE_INTERVAL.getLong()) {
            return@LaunchedEffect
        }

        runCatching {
                Downloader.updateState(state = Downloader.State.Updating)
                withContext(Dispatchers.IO) { UpdateUtil.updateYtDlp() }
            }
            .onFailure { it.printStackTrace() }
        Downloader.updateState(state = Downloader.State.Idle)
    }
}
