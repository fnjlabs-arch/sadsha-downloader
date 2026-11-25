package com.sadsha.downloader.ui.page.downloadv2

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadsha.downloader.R
import com.sadsha.downloader.download.DownloaderV2
import com.sadsha.downloader.download.Task
import com.sadsha.downloader.download.Task.DownloadState.Canceled
import com.sadsha.downloader.download.Task.DownloadState.Completed
import com.sadsha.downloader.download.Task.DownloadState.Error
import com.sadsha.downloader.download.Task.DownloadState.FetchingInfo
import com.sadsha.downloader.download.Task.DownloadState.Idle
import com.sadsha.downloader.download.Task.DownloadState.Paused
import com.sadsha.downloader.download.Task.DownloadState.ReadyWithInfo
import com.sadsha.downloader.download.Task.DownloadState.Running
import com.sadsha.downloader.player.PlayableMedia
import com.sadsha.downloader.player.PlayerManager
import com.sadsha.downloader.ui.common.HapticFeedback.slightHapticFeedback
import com.sadsha.downloader.ui.common.LocalDarkTheme
import com.sadsha.downloader.ui.common.LocalFixedColorRoles
import com.sadsha.downloader.ui.common.LocalWindowWidthState
import com.sadsha.downloader.ui.component.DownloadFormatSelectionDialog
import com.sadsha.downloader.ui.component.HomeUrlInputCard
import com.sadsha.downloader.ui.component.SadShaAppsShowcase
import com.sadsha.downloader.ui.component.SadShaFooter
import com.sadsha.downloader.ui.component.SealModalBottomSheet
import com.sadsha.downloader.ui.component.SelectionGroupDefaults
import com.sadsha.downloader.ui.component.SelectionGroupItem
import com.sadsha.downloader.ui.component.SelectionGroupRow
import com.sadsha.downloader.ui.page.downloadv2.configure.Config
import com.sadsha.downloader.ui.page.downloadv2.configure.DownloadDialog
import com.sadsha.downloader.ui.page.downloadv2.configure.DownloadDialogViewModel
import com.sadsha.downloader.ui.page.downloadv2.configure.DownloadDialogViewModel.Action
import com.sadsha.downloader.ui.page.downloadv2.configure.FormatPage
import com.sadsha.downloader.ui.page.downloadv2.configure.PlaylistSelectionPage
import com.sadsha.downloader.ui.page.downloadv2.configure.PreferencesMock
import com.sadsha.downloader.ui.player.InAppVideoPlayerDialog
import com.sadsha.downloader.ui.player.MiniPlayerBar
import com.sadsha.downloader.ui.theme.SealTheme
import com.sadsha.downloader.util.DownloadUtil
import com.sadsha.downloader.util.FileUtil
import com.sadsha.downloader.util.getErrorReport
import com.sadsha.downloader.util.makeToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private const val TAG = "DownloadPageV2"

enum class Filter {
    All,
    Downloading,
    Canceled,
    Finished;

    @Composable
    @ReadOnlyComposable
    fun label(): String =
        when (this) {
            All -> stringResource(R.string.all)
            Downloading -> stringResource(R.string.status_downloading)
            Canceled -> stringResource(R.string.status_canceled)
            Finished -> stringResource(R.string.status_completed)
        }

    fun predict(entry: Pair<Task, Task.State>): Boolean {
        if (this == All) return true
        val state = entry.second.downloadState
        return when (this) {
            Downloading -> {
                when (state) {
                    is FetchingInfo,
                    Idle,
                    ReadyWithInfo,
                    is Paused,
                    is Running -> true
                    else -> false
                }
            }
            Canceled -> {
                state is Error || state is Task.DownloadState.Canceled || state is Paused
            }
            Finished -> {
                state is Completed
            }
            else -> {
                true
            }
        }
    }
}

enum class HomeSectionTab {
    Downloads,
    SadShaApps;
}

sealed interface UiAction {
    data class OpenFile(val filePath: String?) : UiAction

    data class ShareFile(val filePath: String?) : UiAction

    data class OpenThumbnailURL(val url: String) : UiAction

    data object CopyVideoURL : UiAction

    data class OpenVideoURL(val url: String) : UiAction

    data object Pause : UiAction

    data object Cancel : UiAction

    data object Delete : UiAction

    data object Resume : UiAction

    data class CopyErrorReport(val throwable: Throwable) : UiAction
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadPageV2(
    modifier: Modifier = Modifier,
    onMenuOpen: (() -> Unit) = {},
    dialogViewModel: DownloadDialogViewModel,
    downloader: DownloaderV2 = koinInject(),
) {
    val view = LocalView.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var pendingUrlForQualityDialog by remember { mutableStateOf<String?>(null) }
    var preferences by remember {
        mutableStateOf(DownloadUtil.DownloadPreferences.createFromPreferences())
    }

    val onQuickAudioDownload: (String) -> Unit = { url ->
        view.slightHapticFeedback()
        val audioPrefs = preferences.copy(extractAudio = true)
        downloader.enqueue(Task(url = url, preferences = audioPrefs))
        context.makeToast("Starting audio download (MP3)...")
    }

    val onPlayMedia: (PlayableMedia) -> Unit = { media ->
        view.slightHapticFeedback()
        PlayerManager.play(
            context = context,
            media = media,
            startInListenMode = false,
            showUi = true,
        )
    }

    DownloadPageImplV2(
        modifier = modifier,
        taskDownloadStateMap = downloader.getTaskStateMap(),
        basePreferences = preferences,
        downloadCallback = {
            view.slightHapticFeedback()
            dialogViewModel.postAction(Action.ShowSheet())
        },
        onUrlDownload = { url ->
            view.slightHapticFeedback()
            pendingUrlForQualityDialog = url
        },
        onQuickAudioDownload = onQuickAudioDownload,
        onBatchDownload = { urls, chosenPrefs ->
            view.slightHapticFeedback()
            urls.forEach { url ->
                downloader.enqueue(Task(url = url, preferences = chosenPrefs))
            }
            context.makeToast("Queued ${urls.size} items for batch download!")
        },
        onPlayMedia = onPlayMedia,
        onMenuOpen = onMenuOpen,
    ) { task, action ->
        view.slightHapticFeedback()
        when (action) {
            UiAction.Pause -> downloader.pause(task)
            UiAction.Cancel -> downloader.cancel(task)
            UiAction.Delete -> downloader.remove(task)
            UiAction.Resume -> downloader.resume(task)
            is UiAction.CopyErrorReport -> {
                clipboardManager.setText(
                    AnnotatedString(getErrorReport(action.throwable, task.url))
                )
                context.makeToast(R.string.error_copied)
            }
            UiAction.CopyVideoURL -> {
                clipboardManager.setText(AnnotatedString(task.url))
                context.makeToast(R.string.link_copied)
            }
            is UiAction.OpenFile -> {
                val resolvedPath =
                    action.filePath
                        ?: (downloader.getTaskStateMap()[task]?.downloadState as? Task.DownloadState.Completed)?.filePath
                if (!resolvedPath.isNullOrEmpty()) {
                    val taskState = downloader.getTaskStateMap()[task]
                    val title =
                        taskState?.videoInfo?.title?.takeIf { it.isNotBlank() }
                            ?: task.url.substringAfterLast("/").take(30)
                    val artist =
                        taskState?.videoInfo?.uploader?.takeIf { it.isNotBlank() }
                            ?: "Downloaded Media"
                    val thumb = taskState?.videoInfo?.thumbnail
                    PlayerManager.play(
                        context = context,
                        media =
                            PlayableMedia(
                                title = title,
                                artist = artist,
                                mediaUri = resolvedPath,
                                thumbnailUrl = thumb,
                                isLocal = true,
                            ),
                        showUi = true,
                    )
                } else {
                    context.makeToast("File not available or still downloading")
                }
            }
            is UiAction.OpenThumbnailURL -> {
                uriHandler.openUri(action.url)
            }
            is UiAction.OpenVideoURL -> {
                uriHandler.openUri(action.url)
            }
            is UiAction.ShareFile -> {
                val shareTitle = context.getString(R.string.share)
                FileUtil.createIntentForSharingFile(action.filePath)?.let {
                    context.startActivity(Intent.createChooser(it, shareTitle))
                }
            }
        }
    }

    val sheetValue by dialogViewModel.sheetValueFlow.collectAsStateWithLifecycle()
    val state by dialogViewModel.sheetStateFlow.collectAsStateWithLifecycle()
    val selectionState = dialogViewModel.selectionStateFlow.collectAsStateWithLifecycle().value

    var showDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(sheetValue) {
        if (sheetValue == DownloadDialogViewModel.SheetValue.Expanded) {
            showDialog = true
        } else {
            launch { sheetState.hide() }.invokeOnCompletion { showDialog = false }
        }
    }

    if (showDialog) {
        DownloadDialog(
            state = state,
            sheetState = sheetState,
            config = Config(),
            preferences = preferences,
            onPreferencesUpdate = { preferences = it },
            onActionPost = { dialogViewModel.postAction(it) },
        )
    }

    pendingUrlForQualityDialog?.let { url ->
        DownloadFormatSelectionDialog(
            url = url,
            basePreferences = preferences,
            onDismissRequest = { pendingUrlForQualityDialog = null },
            onConfirmDownload = { chosenPreferences ->
                pendingUrlForQualityDialog = null
                downloader.enqueue(Task(url = url, preferences = chosenPreferences))
            },
        )
    }

    // In-App Video Player Dialog
    if (PlayerManager.isPlayerVisible) {
        InAppVideoPlayerDialog(
            onDismissRequest = { PlayerManager.hidePlayer() }
        )
    }

    when (selectionState) {
        is DownloadDialogViewModel.SelectionState.FormatSelection ->
            FormatPage(
                state = selectionState,
                onDismissRequest = { dialogViewModel.postAction(Action.Reset) },
            )

        is DownloadDialogViewModel.SelectionState.PlaylistSelection -> {
            PlaylistSelectionPage(
                state = selectionState,
                onDismissRequest = { dialogViewModel.postAction(Action.Reset) },
            )
        }

        DownloadDialogViewModel.SelectionState.Idle -> {}
    }
}

@Composable
private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        top = calculateTopPadding() + other.calculateTopPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
        start =
            calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        end = calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
    )
}

private const val HeaderSpacingDp = 28

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadPageImplV2(
    modifier: Modifier = Modifier,
    taskDownloadStateMap: SnapshotStateMap<Task, Task.State>,
    basePreferences: DownloadUtil.DownloadPreferences = DownloadUtil.DownloadPreferences.createFromPreferences(),
    downloadCallback: () -> Unit = {},
    onUrlDownload: (String) -> Unit = {},
    onQuickAudioDownload: (String) -> Unit = {},
    onBatchDownload: (List<String>, DownloadUtil.DownloadPreferences) -> Unit = { _, _ -> },
    onPlayMedia: (PlayableMedia) -> Unit = {},
    onMenuOpen: (() -> Unit) = {},
    onActionPost: (Task, UiAction) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    // Active Home Tab: Downloads or SadShaApps
    var activeHomeTab by rememberSaveable { mutableStateOf(HomeSectionTab.Downloads) }

    // Multi-Selection State for Download Queue
    var isQueueSelectionMode by rememberSaveable { mutableStateOf(false) }
    val selectedTaskIds = remember { mutableStateListOf<String>() }

    // Downloads filter
    var activeFilter by remember { mutableStateOf(Filter.All) }
    val filteredMap by
        remember(activeFilter, taskDownloadStateMap.size) {
            derivedStateOf { taskDownloadStateMap.filter { activeFilter.predict(it.toPair()) } }
        }

    // Check if there are tasks currently active in background
    val hasActiveTasks = remember(taskDownloadStateMap.size) {
        taskDownloadStateMap.any {
            it.value.downloadState is Running ||
                it.value.downloadState is FetchingInfo ||
                it.value.downloadState is ReadyWithInfo
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    fun showActionSheet(task: Task) {
        view.slightHapticFeedback()
        scope.launch {
            selectedTask = task
            delay(50)
            sheetState.show()
        }
    }

    LaunchedEffect(selectedTask, taskDownloadStateMap.size) {
        if (!taskDownloadStateMap.contains(selectedTask)) {
            selectedTask = null
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        containerColor = Color(0xFF090D18),
        floatingActionButton = {
            if (!isQueueSelectionMode) {
                FABs(modifier = Modifier, downloadCallback = downloadCallback)
            }
        },
        bottomBar = {
            // Floating Mini-Player bar when audio is playing in Listen Mode
            if (PlayerManager.currentMedia != null && !PlayerManager.isPlayerVisible) {
                MiniPlayerBar(
                    onExpand = { PlayerManager.showPlayer() }
                )
            }
        },
    ) { windowInsetsPadding ->
        val lazyListState = rememberLazyGridState()
        val windowWidthSizeClass = LocalWindowWidthState.current
        val spacerHeight =
            with(LocalDensity.current) {
                if (windowWidthSizeClass != WindowWidthSizeClass.Compact) 0f
                else HeaderSpacingDp.dp.toPx()
            }
        var headerOffset by remember { mutableFloatStateOf(spacerHeight) }
        var isGridView by rememberSaveable { mutableStateOf(true) }

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .then(
                        if (windowWidthSizeClass != WindowWidthSizeClass.Compact) Modifier
                        else
                            Modifier.nestedScroll(
                                connection =
                                    TopBarNestedScrollConnection(
                                        maxOffset = spacerHeight,
                                        flingAnimationSpec = rememberSplineBasedDecay(),
                                        offset = { headerOffset },
                                        onOffsetUpdate = { headerOffset = it },
                                    )
                            )
                    )
        ) {
            CompositionLocalProvider(LocalOverscrollFactory provides null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(with(LocalDensity.current) { headerOffset.toDp() }))
                    Header(
                        onExploreApps = {
                            view.slightHapticFeedback()
                            activeHomeTab = HomeSectionTab.SadShaApps
                        },
                        onMenuOpen = onMenuOpen,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    // Primary Segmented Tab Switcher: Downloads vs SadSha Apps (7 APPS)
                    SelectionGroupRow(
                        modifier =
                            Modifier.horizontalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp, vertical = 2.dp)
                    ) {
                        // Tab 1: Downloads
                        val downloadsCount = taskDownloadStateMap.size
                        SelectionGroupItem(
                            colors =
                                SelectionGroupDefaults.colors(
                                    activeContainerColor = Color(0xFF00F5FF).copy(alpha = 0.2f),
                                    activeContentColor = Color(0xFF00F5FF),
                                ),
                            selected = activeHomeTab == HomeSectionTab.Downloads,
                            onClick = {
                                view.slightHapticFeedback()
                                activeHomeTab = HomeSectionTab.Downloads
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DownloadDone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (activeHomeTab == HomeSectionTab.Downloads) Color(0xFF00F5FF) else Color(0xFF94A3B8),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (downloadsCount > 0) "My Downloads ($downloadsCount)"
                                    else "My Downloads",
                                    color = if (activeHomeTab == HomeSectionTab.Downloads) Color(0xFF00F5FF) else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Tab 2: SadSha Apps (7 APPS)
                        SelectionGroupItem(
                            colors =
                                SelectionGroupDefaults.colors(
                                    activeContainerColor = Color(0xFFE040FB).copy(alpha = 0.25f),
                                    activeContentColor = Color(0xFFE040FB),
                                ),
                            selected = activeHomeTab == HomeSectionTab.SadShaApps,
                            onClick = {
                                view.slightHapticFeedback()
                                activeHomeTab = HomeSectionTab.SadShaApps
                                isQueueSelectionMode = false
                            },
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (activeHomeTab == HomeSectionTab.SadShaApps) Color(0xFFE040FB) else Color(0xFF94A3B8),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "SadSha Apps (7 APPS)",
                                    color = if (activeHomeTab == HomeSectionTab.SadShaApps) Color(0xFFE040FB) else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    // Secondary filter row for Downloads tab
                    if (activeHomeTab == HomeSectionTab.Downloads) {
                        Spacer(Modifier.height(4.dp))
                        SelectionGroupRow(
                            modifier =
                                Modifier.horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp)
                        ) {
                            Filter.entries.forEach { filter ->
                                SelectionGroupItem(
                                    colors =
                                        SelectionGroupDefaults.colors(
                                            activeContainerColor =
                                                LocalFixedColorRoles.current.tertiaryFixed,
                                            activeContentColor =
                                                LocalFixedColorRoles.current.onTertiaryFixed,
                                        ),
                                    selected = activeFilter == filter,
                                    onClick = {
                                        view.slightHapticFeedback()
                                        activeFilter = filter
                                    },
                                ) {
                                    Text(filter.label())
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    if (headerOffset <= 0.1f && spacerHeight > 0f) {
                        HorizontalDivider(thickness = Dp.Hairline)
                    }
                }

                LazyVerticalGrid(
                    modifier = Modifier,
                    state = lazyListState,
                    columns = GridCells.Adaptive(260.dp),
                    contentPadding =
                        windowInsetsPadding +
                            PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Item 1: URL Input Card (when not in multi-selection mode)
                    if (!isQueueSelectionMode) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            HomeUrlInputCard(
                                onDownloadUrl = { url -> onUrlDownload(url) },
                                onQuickAudioDownload = { url -> onQuickAudioDownload(url) },
                                onPlayUrl = { url ->
                                    val isYt = url.contains("youtube.com") || url.contains("youtu.be")
                                    PlayerManager.play(
                                        context = context,
                                        media =
                                            PlayableMedia(
                                                title = "Video Preview",
                                                artist = if (isYt) "YouTube Stream" else "Online Media",
                                                mediaUri = url,
                                                isLocal = false,
                                            ),
                                        showUi = true,
                                    )
                                },
                            )
                        }

                    }

                    // TAB CONTENT: SADSHA APPS (7 APPS)
                    if (activeHomeTab == HomeSectionTab.SadShaApps) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SadShaAppsShowcase()
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SadShaFooter()
                        }
                    }

                    // TAB CONTENT: DOWNLOADS
                    if (activeHomeTab == HomeSectionTab.Downloads) {
                        if (filteredMap.isNotEmpty()) {
                            // Queue Multi-selection action bar
                            if (isQueueSelectionMode) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 6.dp),
                                    ) {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = {
                                                        view.slightHapticFeedback()
                                                        isQueueSelectionMode = false
                                                        selectedTaskIds.clear()
                                                    },
                                                    modifier = Modifier.size(32.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Cancel",
                                                    )
                                                }
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = "${selectedTaskIds.size} selected",
                                                    style =
                                                        MaterialTheme.typography.titleSmall.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                )
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                // Batch Resume
                                                IconButton(
                                                    onClick = {
                                                        view.slightHapticFeedback()
                                                        filteredMap.filter { selectedTaskIds.contains(it.key.id) }
                                                            .forEach { (task, _) ->
                                                                onActionPost(task, UiAction.Resume)
                                                            }
                                                        isQueueSelectionMode = false
                                                        selectedTaskIds.clear()
                                                    },
                                                    enabled = selectedTaskIds.isNotEmpty(),
                                                    modifier = Modifier.size(36.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.RestartAlt,
                                                        contentDescription = "Batch Resume",
                                                    )
                                                }

                                                // Batch Cancel
                                                IconButton(
                                                    onClick = {
                                                        view.slightHapticFeedback()
                                                        filteredMap.filter { selectedTaskIds.contains(it.key.id) }
                                                            .forEach { (task, _) ->
                                                                onActionPost(task, UiAction.Cancel)
                                                            }
                                                        isQueueSelectionMode = false
                                                        selectedTaskIds.clear()
                                                    },
                                                    enabled = selectedTaskIds.isNotEmpty(),
                                                    modifier = Modifier.size(36.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Cancel,
                                                        contentDescription = "Batch Cancel",
                                                    )
                                                }

                                                // Batch Delete
                                                IconButton(
                                                    onClick = {
                                                        view.slightHapticFeedback()
                                                        filteredMap.filter { selectedTaskIds.contains(it.key.id) }
                                                            .forEach { (task, _) ->
                                                                onActionPost(task, UiAction.Delete)
                                                            }
                                                        isQueueSelectionMode = false
                                                        selectedTaskIds.clear()
                                                    },
                                                    enabled = selectedTaskIds.isNotEmpty(),
                                                    modifier = Modifier.size(36.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = "Batch Delete",
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val videoCount =
                                    filteredMap.count {
                                        !it.value.viewState.videoFormats.isNullOrEmpty()
                                    }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    SubHeader(
                                        modifier = Modifier.weight(1f),
                                        videoCount = videoCount,
                                        audioCount = filteredMap.size - videoCount,
                                        isGridView = isGridView,
                                        onToggleView = { isGridView = !isGridView },
                                        onShowMenu = {
                                            view.slightHapticFeedback()
                                            isQueueSelectionMode = !isQueueSelectionMode
                                            selectedTaskIds.clear()
                                        },
                                    )

                                    // Multi-selection button for queue
                                    OutlinedButton(
                                        onClick = {
                                            view.slightHapticFeedback()
                                            isQueueSelectionMode = !isQueueSelectionMode
                                            selectedTaskIds.clear()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(32.dp).padding(end = 4.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlaylistAddCheck,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = if (isQueueSelectionMode) "Done" else "Select",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    }
                                }
                            }

                            if (isGridView) {
                                items(
                                    items =
                                        filteredMap.toList().sortedBy { (_, state) ->
                                            state.downloadState
                                        },
                                    key = { (task, _) -> task.id },
                                ) { (task, state) ->
                                    val isSelected = selectedTaskIds.contains(task.id)
                                    with(state.viewState) {
                                        Box(
                                            modifier =
                                                if (isQueueSelectionMode) {
                                                    Modifier.padding(bottom = 20.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .clickable {
                                                            view.slightHapticFeedback()
                                                            if (isSelected) selectedTaskIds.remove(task.id)
                                                            else selectedTaskIds.add(task.id)
                                                        }
                                                } else Modifier
                                        ) {
                                            VideoCardV2(
                                                modifier =
                                                    Modifier.padding(bottom = if (isQueueSelectionMode) 0.dp else 20.dp),
                                                viewState = this@with,
                                                actionButton = {
                                                    ActionButton(
                                                        modifier = Modifier,
                                                        downloadState = state.downloadState,
                                                    ) {
                                                        onActionPost(task, it)
                                                    }
                                                },
                                                stateIndicator = {
                                                    CardStateIndicator(
                                                        modifier = Modifier,
                                                        downloadState = state.downloadState,
                                                    )
                                                },
                                                onClick = {
                                                    if (isQueueSelectionMode) {
                                                        if (isSelected) selectedTaskIds.remove(task.id)
                                                        else selectedTaskIds.add(task.id)
                                                    } else if (
                                                        state.downloadState is Task.DownloadState.Completed
                                                    ) {
                                                        onActionPost(
                                                            task,
                                                            UiAction.OpenFile(
                                                                state.downloadState.filePath
                                                            ),
                                                        )
                                                    } else {
                                                        showActionSheet(task)
                                                    }
                                                },
                                                onButtonClick = { showActionSheet(task) },
                                            )

                                            // Checkbox overlay during queue selection
                                            if (isQueueSelectionMode) {
                                                Box(
                                                    modifier =
                                                        Modifier.padding(10.dp)
                                                            .align(Alignment.TopEnd)
                                                            .size(28.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                                else Color.Black.copy(alpha = 0.6f)
                                                            ),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.DownloadDone,
                                                            contentDescription = "Selected",
                                                            tint = MaterialTheme.colorScheme.onPrimary,
                                                            modifier = Modifier.size(18.dp),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                items(
                                    items =
                                        filteredMap.toList().sortedBy { (_, state) ->
                                            state.downloadState
                                        },
                                    key = { (task, _) -> task.id },
                                    span = { GridItemSpan(maxLineSpan) },
                                ) { (task, state) ->
                                    val isSelected = selectedTaskIds.contains(task.id)
                                    Box(
                                        modifier =
                                            if (isQueueSelectionMode) {
                                                Modifier.padding(bottom = 16.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        view.slightHapticFeedback()
                                                        if (isSelected) selectedTaskIds.remove(task.id)
                                                        else selectedTaskIds.add(task.id)
                                                    }
                                            } else Modifier
                                    ) {
                                        VideoListItem(
                                            modifier = Modifier.padding(bottom = if (isQueueSelectionMode) 0.dp else 16.dp),
                                            viewState = state.viewState,
                                            stateIndicator = {
                                                ListItemStateText(
                                                    modifier = Modifier.padding(top = 3.dp),
                                                    downloadState = state.downloadState,
                                                )
                                            },
                                            onClick = {
                                                if (isQueueSelectionMode) {
                                                    if (isSelected) selectedTaskIds.remove(task.id)
                                                    else selectedTaskIds.add(task.id)
                                                } else if (
                                                    state.downloadState is Task.DownloadState.Completed
                                                ) {
                                                    onActionPost(
                                                        task,
                                                        UiAction.OpenFile(state.downloadState.filePath),
                                                    )
                                                } else {
                                                    showActionSheet(task)
                                                }
                                            },
                                            onButtonClick = { showActionSheet(task) },
                                        )
                                    }
                                }
                            }
                        } else {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyQueueCard(
                                    onExploreSadShaApps = {
                                        activeHomeTab = HomeSectionTab.SadShaApps
                                    }
                                )
                            }
                        }

                        // Footer at bottom of Downloads tab
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SadShaFooter()
                        }
                    }
                }
            }
        }
    }

    if (selectedTask != null) {
        val task = selectedTask!!
        val (downloadState, _, viewState) = taskDownloadStateMap[task] ?: return
        SealModalBottomSheet(
            sheetState = sheetState,
            contentPadding = PaddingValues(),
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { selectedTask = null }
            },
        ) {
            SheetContent(
                task = task,
                downloadState = downloadState,
                viewState = viewState,
                onDismissRequest = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { selectedTask = null }
                },
                onActionPost = onActionPost,
            )
        }
    }
}

@Composable
fun Header(
    onExploreApps: () -> Unit = {},
    modifier: Modifier = Modifier,
    onMenuOpen: () -> Unit = {},
) {
    val windowWidthSizeClass = LocalWindowWidthState.current
    when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            HeaderExpanded(
                onExploreApps = onExploreApps,
                modifier = modifier,
                onMenuOpen = onMenuOpen,
            )
        }
        else -> {
            HeaderCompact(
                onExploreApps = onExploreApps,
                modifier = modifier,
                onMenuOpen = onMenuOpen,
            )
        }
    }
}

@Composable
private fun HeaderCompact(
    onExploreApps: () -> Unit = {},
    modifier: Modifier = Modifier,
    onMenuOpen: () -> Unit = {},
) {
    Row(
        modifier = modifier.height(76.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenuOpen, modifier = Modifier.size(44.dp)) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(R.string.show_navigation_drawer),
                tint = Color(0xFF00F5FF),
                modifier = Modifier.size(26.dp),
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        // Prominent, clear SadSha Logo
        Image(
            painter = painterResource(id = R.drawable.img_sadsha_logo),
            contentDescription = "SadSha Logo",
            modifier =
                Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFF00F5FF), Color(0xFFE040FB))
                        ),
                        RoundedCornerShape(14.dp),
                    ),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.app_name),
                style =
                    MaterialTheme.typography.titleLarge.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                color = Color.White,
                maxLines = 1,
            )
            Text(
                text = "Fast 4K & MP3 Downloader",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                color = Color(0xFF00F5FF),
                maxLines = 1,
            )
        }

        // SadSha 7 Apps Quick Access Chip
        Surface(
            onClick = onExploreApps,
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1035),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE040FB)),
            modifier = Modifier.padding(end = 4.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color(0xFF00F5FF),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = "7 Apps",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color(0xFFE040FB),
                )
            }
        }
    }
}

@Composable
private fun HeaderExpanded(
    onExploreApps: () -> Unit = {},
    modifier: Modifier = Modifier,
    onMenuOpen: () -> Unit = {},
) {
    Row(
        modifier = modifier.height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        // Prominent, clear SadSha Logo for Expanded layout
        Image(
            painter = painterResource(id = R.drawable.img_sadsha_logo),
            contentDescription = "SadSha Logo",
            modifier =
                Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        2.dp,
                        Brush.linearGradient(
                            listOf(Color(0xFF00F5FF), Color(0xFFE040FB))
                        ),
                        RoundedCornerShape(16.dp),
                    ),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = Color.White,
            )
            Text(
                text = "SadSha High-Speed 4K Media & Audio Engine",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF00F5FF),
            )
        }

        Surface(
            onClick = onExploreApps,
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1035),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE040FB)),
            modifier = Modifier.padding(end = 8.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF00F5FF),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "SadSha Ecosystem (7 Apps)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFE040FB),
                )
            }
        }
    }
}

@Composable
fun FABs(modifier: Modifier = Modifier, downloadCallback: () -> Unit = {}) {
    val expanded = LocalWindowWidthState.current != WindowWidthSizeClass.Compact
    Column(modifier = modifier.padding(6.dp), horizontalAlignment = Alignment.End) {
        FloatingActionButton(
            onClick = downloadCallback,
            containerColor = Color(0xFF00F5FF),
            contentColor = Color.Black,
            content = {
                if (expanded) {
                    Row(
                        modifier = Modifier.widthIn(min = 80.dp).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.download), fontWeight = FontWeight.Bold)
                    }
                } else {
                    Icon(
                        Icons.Outlined.FileDownload,
                        contentDescription = stringResource(R.string.download),
                    )
                }
            },
            modifier = Modifier.padding(vertical = 12.dp),
        )
    }
}

@Composable
private fun EmptyQueueCard(
    onExploreSadShaApps: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        shape = RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)
            ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00F5FF).copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier.size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00F5FF).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF00F5FF), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFF00F5FF),
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Ready to Download",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text =
                    "Paste any video or audio URL in the box above to start high-speed downloading, or explore our official 7 SadSha apps.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onExploreSadShaApps,
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE040FB),
                        contentColor = Color.White,
                    ),
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text("Explore 7 Apps by SadSha", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SubHeader(
    modifier: Modifier = Modifier,
    containerColor: Color =
        MaterialTheme.colorScheme.run {
            if (LocalDarkTheme.current.isDarkTheme()) surfaceContainer else surfaceContainerLowest
        },
    videoCount: Int = 0,
    audioCount: Int = 0,
    isGridView: Boolean = true,
    onToggleView: () -> Unit,
    onShowMenu: () -> Unit,
) {
    val text = buildString {
        if (videoCount > 0) {
            append(pluralStringResource(R.plurals.video_count, videoCount).format(videoCount))
            if (audioCount > 0) {
                append(", ")
            }
        }
        if (audioCount > 0) {
            append(pluralStringResource(R.plurals.audio_count, audioCount).format(audioCount))
        }
    }

    Row(
        modifier = modifier.padding(top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(4.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledIconButton(
            onClick = onToggleView,
            modifier = Modifier.clearAndSetSemantics {}.size(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = containerColor),
        ) {
            Icon(
                imageVector =
                    if (isGridView) Icons.AutoMirrored.Outlined.List else Icons.Outlined.GridView,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(Modifier.width(4.dp))

        FilledIconButton(
            onClick = onShowMenu,
            modifier = Modifier.clearAndSetSemantics {}.size(32.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = containerColor),
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

internal class DownloadPageV2Test {
    private val mockDownloader =
        object : DownloaderV2 {
            private val map = mutableStateMapOf<Task, Task.State>()

            init {
                val viewState =
                    Task.ViewState(title = "Sample title", uploader = "dummy video uploader")
                val list =
                    listOf(
                        Task.State(Idle, null, viewState),
                        Task.State(Canceled(Task.RestartableAction.Download), null, viewState),
                        Task.State(Completed(null), null, viewState),
                    )
                map.run {
                    repeat(9) {
                        put(Task(url = "$it", preferences = PreferencesMock), list[it % 3])
                    }
                }
                val scope = CoroutineScope(SupervisorJob())

                scope.launch(Dispatchers.Default) {
                    while (true) {
                        delay(1000)
                        val newEntries =
                            map.toMap().map { (task, state) ->
                                val newDownloadState =
                                    when (state.downloadState) {
                                        is Canceled -> Idle
                                        is Completed -> Idle
                                        is Error -> Idle
                                        is FetchingInfo -> ReadyWithInfo
                                        Idle -> FetchingInfo(Job(), task.id)
                                        ReadyWithInfo -> Running(Job(), task.id)
                                        is Running -> {
                                            val preState: Running = state.downloadState
                                            if (preState.progress >= 1f) Completed(null)
                                            else preState.copy(progress = preState.progress + 0.1f)
                                        }
                                        is Paused -> Idle
                                    }
                                task to state.copy(downloadState = newDownloadState)
                            }
                        Snapshot.withMutableSnapshot {
                            newEntries.forEach { (task, state) ->
                                delay(100)
                                map[task] = state
                            }
                        }
                    }
                }
            }

            override fun getTaskStateMap(): SnapshotStateMap<Task, Task.State> {
                return map
            }

            override fun cancel(task: Task): Boolean {
                return false
            }

            override fun pause(task: Task): Boolean {
                return true
            }

            override fun resume(task: Task) {}

            override fun restart(task: Task) {}

            override fun enqueue(task: Task) {}

            override fun enqueue(task: Task, state: Task.State) {}

            override fun remove(task: Task): Boolean {
                return true
            }
        }

    @Composable
    @Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
    @Preview(name = "Tablet", device = "spec:width=600dp,height=800dp,dpi=240")
    private fun Preview() {
        val downloader: DownloaderV2 = mockDownloader
        SealTheme {
            Column {
                DownloadPageImplV2(
                    taskDownloadStateMap = downloader.getTaskStateMap(),
                    onActionPost = { task, state -> },
                    onMenuOpen = {},
                )
            }
        }
    }
}
