package com.sadsha.downloader.ui.player

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sadsha.downloader.player.PlayableMedia
import com.sadsha.downloader.player.PlayerManager
import com.sadsha.downloader.ui.common.AsyncImageImpl
import com.sadsha.downloader.ui.common.HapticFeedback.slightHapticFeedback
import com.sadsha.downloader.util.FileUtil
import com.sadsha.downloader.util.makeToast
import kotlinx.coroutines.delay

@Composable
fun InAppVideoPlayerDialog(
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
            ),
    ) {
        InAppVideoPlayer(
            onClose = onDismissRequest,
        )
    }
}

@Composable
fun InAppVideoPlayer(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val media = PlayerManager.currentMedia ?: return

    val isOnlineYouTube = media.isYouTubeUrl() && !media.isLocal && !PlayerManager.isListenMode
    val youtubeVideoId = remember(media.mediaUri) { media.extractYouTubeId() }

    var areControlsVisible by remember { mutableStateOf(true) }
    var userIsSeeking by remember { mutableStateOf(false) }
    var seekSliderPosition by remember { mutableFloatStateOf(0f) }

    // Auto-hide controls timer when video is actively playing
    LaunchedEffect(areControlsVisible, PlayerManager.isPlaying, isOnlineYouTube) {
        if (!isOnlineYouTube && areControlsVisible && PlayerManager.isPlaying && !userIsSeeking) {
            delay(4000)
            areControlsVisible = false
        }
    }

    BackHandler {
        onClose()
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    areControlsVisible = !areControlsVisible
                }
    ) {
        // Video Viewport Area
        if (isOnlineYouTube && !youtubeVideoId.isNullOrEmpty()) {
            // Keep YouTubePlayerView active so audio streams in background even during Listen Mode
            Box(
                modifier =
                    if (PlayerManager.isListenMode) Modifier.size(1.dp)
                    else Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                YouTubePlayerView(
                    videoId = youtubeVideoId,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                )
            }
        } else if (!PlayerManager.isListenMode) {
            // Local Downloaded File or Direct Video Stream via TextureView
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                val aspectRatio = PlayerManager.videoAspectRatio.coerceIn(0.5f, 2.4f)
                AndroidView(
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            surfaceTextureListener =
                                object : TextureView.SurfaceTextureListener {
                                    override fun onSurfaceTextureAvailable(
                                        surfaceTexture: SurfaceTexture,
                                        width: Int,
                                        height: Int,
                                    ) {
                                        PlayerManager.setSurface(Surface(surfaceTexture))
                                    }

                                    override fun onSurfaceTextureSizeChanged(
                                        surfaceTexture: SurfaceTexture,
                                        width: Int,
                                        height: Int,
                                    ) {}

                                    override fun onSurfaceTextureDestroyed(
                                        surfaceTexture: SurfaceTexture
                                    ): Boolean {
                                        PlayerManager.setSurface(null)
                                        return true
                                    }

                                    override fun onSurfaceTextureUpdated(
                                        surfaceTexture: SurfaceTexture
                                    ) {}
                                }
                            if (isAvailable && surfaceTexture != null) {
                                PlayerManager.setSurface(Surface(surfaceTexture))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().aspectRatio(aspectRatio),
                )
            }
        }

        // When in Listen Mode, display the dedicated Audio Disc & Visualizer
        if (PlayerManager.isListenMode) {
            ListenModeVisualizer(
                title = media.title,
                artist = media.artist,
                thumbnailUrl = media.thumbnailUrl,
                isPlaying = PlayerManager.isPlaying,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Loading Indicator Overlay
        if (PlayerManager.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Loading media...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        // Playback Error Banner
        PlayerManager.playbackError?.let { errorMsg ->
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Playback Error",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedButton(
                                onClick = {
                                    view.slightHapticFeedback()
                                    PlayerManager.play(context, media, showUi = true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Retry")
                            }

                            Button(
                                onClick = {
                                    view.slightHapticFeedback()
                                    openInExternalApp(context, media.mediaUri)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Open External")
                            }
                        }
                    }
                }
            }
        }

        // Top & Bottom Overlay Controls with smooth fade animation
        val showOverlay = areControlsVisible || !PlayerManager.isPlaying || isOnlineYouTube
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.8f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.88f),
                                )
                            )
                        )
            ) {
                // Top Header: Close, Title, Artist, External Player, Listen Mode Toggle
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {},
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            view.slightHapticFeedback()
                            onClose()
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Black.copy(alpha = 0.5f),
                                contentColor = Color.White,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Player",
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = media.title,
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = media.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    // Open In External Player Button
                    IconButton(
                        onClick = {
                            view.slightHapticFeedback()
                            openInExternalApp(context, media.mediaUri)
                        },
                        colors =
                            IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open in external app",
                            modifier = Modifier.size(18.dp),
                        )
                    }

                    Spacer(Modifier.width(6.dp))

                    // Listen Mode Toggle Button
                    Surface(
                        onClick = {
                            view.slightHapticFeedback()
                            PlayerManager.toggleListenMode(context)
                            val msg =
                                if (PlayerManager.isListenMode)
                                    "🎧 Listen Mode ON: You can lock screen or minimize app!"
                                else "Switched to standard video mode"
                            context.makeToast(msg)
                        },
                        shape = RoundedCornerShape(20.dp),
                        color =
                            if (PlayerManager.isListenMode)
                                MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.2f),
                        contentColor =
                            if (PlayerManager.isListenMode)
                                MaterialTheme.colorScheme.onPrimary
                            else Color.White,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Listen Mode",
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text =
                                    if (PlayerManager.isListenMode) "Listen Mode ON"
                                    else "Listen Mode",
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                            )
                        }
                    }
                }

                // Center Controls: Rewind 10s, Play/Pause, Forward 10s (for local media and audio)
                if (!isOnlineYouTube) {
                    Row(
                        modifier =
                            Modifier.align(Alignment.Center)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {},
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        // Rewind 10s
                        FilledIconButton(
                            onClick = {
                                view.slightHapticFeedback()
                                PlayerManager.rewind10s(context)
                            },
                            modifier = Modifier.size(52.dp),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color.Black.copy(alpha = 0.6f),
                                    contentColor = Color.White,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10 seconds",
                                modifier = Modifier.size(28.dp),
                            )
                        }

                        // Play/Pause Main Button
                        FilledIconButton(
                            onClick = {
                                view.slightHapticFeedback()
                                PlayerManager.togglePlayPause(context)
                            },
                            modifier = Modifier.size(72.dp),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                        ) {
                            Icon(
                                imageVector =
                                    if (PlayerManager.isPlaying) Icons.Default.Pause
                                    else Icons.Default.PlayArrow,
                                contentDescription =
                                    if (PlayerManager.isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(42.dp),
                            )
                        }

                        // Forward 10s
                        FilledIconButton(
                            onClick = {
                                view.slightHapticFeedback()
                                PlayerManager.forward10s(context)
                            },
                            modifier = Modifier.size(52.dp),
                            colors =
                                IconButtonDefaults.filledIconButtonColors(
                                    containerColor = Color.Black.copy(alpha = 0.6f),
                                    contentColor = Color.White,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10 seconds",
                                modifier = Modifier.size(28.dp),
                            )
                        }
                    }
                }

                // Bottom Controls: Time Scrubber, Duration, Speed Control (when applicable)
                if (!isOnlineYouTube) {
                    Column(
                        modifier =
                            Modifier.align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {}
                    ) {
                        // Current position and duration label
                        val currentPos =
                            if (userIsSeeking) seekSliderPosition.toLong()
                            else PlayerManager.currentPositionMs
                        val totalDuration = PlayerManager.durationMs

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = formatTimeMs(currentPos),
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                color = Color.White,
                            )

                            // Speed selector chip
                            Surface(
                                onClick = {
                                    view.slightHapticFeedback()
                                    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                                    val currentIndex =
                                        speeds.indexOf(PlayerManager.playbackSpeed).coerceAtLeast(0)
                                    val nextIndex = (currentIndex + 1) % speeds.size
                                    PlayerManager.setSpeed(speeds[nextIndex])
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White,
                            ) {
                                Row(
                                    modifier =
                                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "${PlayerManager.playbackSpeed}x",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                    )
                                }
                            }

                            Text(
                                text = formatTimeMs(totalDuration),
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                color = Color.White.copy(alpha = 0.7f),
                            )
                        }

                        // Scrubber Slider
                        val sliderMax = totalDuration.toFloat().coerceAtLeast(1f)
                        val sliderVal =
                            if (userIsSeeking) seekSliderPosition.coerceIn(0f, sliderMax)
                            else currentPos.toFloat().coerceIn(0f, sliderMax)

                        Slider(
                            value = sliderVal,
                            onValueChange = {
                                userIsSeeking = true
                                seekSliderPosition = it
                            },
                            onValueChangeFinished = {
                                userIsSeeking = false
                                PlayerManager.seekTo(seekSliderPosition.toLong())
                            },
                            valueRange = 0f..sliderMax,
                            colors =
                                SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                                ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-performance, hardware-accelerated embedded YouTube Player for online video preview
 */
@Composable
fun YouTubePlayerView(
    videoId: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    allowFileAccess = false
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    userAgentString =
                        "Mozilla/5.0 (Linux; Android 13; Pixel 7 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                }
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(android.graphics.Color.BLACK)

                val html =
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <style>
                            * { margin:0; padding:0; background:#000000; overflow:hidden; }
                            html, body { width:100%; height:100%; display:flex; align-items:center; justify-content:center; }
                            iframe { width:100vw; height:100vh; border:none; }
                        </style>
                    </head>
                    <body>
                        <iframe 
                            src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&fs=1&rel=0&origin=https://www.youtube.com" 
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                            allowfullscreen>
                        </iframe>
                    </body>
                    </html>
                    """.trimIndent()
                loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier,
    )
}

private fun openInExternalApp(context: android.content.Context, uriString: String) {
    try {
        val intent = FileUtil.createIntentForOpeningFile(uriString)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            val browserIntent =
                android.content.Intent(
                    android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse(uriString),
                ).apply {
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(browserIntent)
        }
    } catch (e: Exception) {
        context.makeToast("Could not open external app: ${e.localizedMessage}")
    }
}

@Composable
fun ListenModeVisualizer(
    title: String,
    artist: String,
    thumbnailUrl: String?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = if (isPlaying) 1.08f else 1.0f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "pulseScale",
        )

    val discRotation by
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(16000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "discRotation",
        )

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Rotating album/thumbnail disc
        Box(
            modifier =
                Modifier.size(240.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E2E))
                    .border(
                        3.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            if (!thumbnailUrl.isNullOrEmpty()) {
                AsyncImageImpl(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier =
                        Modifier.fillMaxSize()
                            .rotate(if (isPlaying) discRotation else 0f)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Audiotrack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp),
                )
            }

            // Center spindle hole
            Box(
                modifier =
                    Modifier.size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Headphones,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "🎧 Background Audio Mode",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = artist,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Audio will keep playing when you lock your screen or minimize the app.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

/**
 * Bottom Floating Mini Player Bar shown when media is playing and full player is dismissed
 */
@Composable
fun MiniPlayerBar(
    modifier: Modifier = Modifier,
    onExpand: () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current
    val media = PlayerManager.currentMedia ?: return

    Surface(
        onClick = {
            view.slightHapticFeedback()
            onExpand()
        },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 6.dp,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail / Icon
            Box(
                modifier =
                    Modifier.size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (!media.thumbnailUrl.isNullOrEmpty()) {
                    AsyncImageImpl(
                        model = media.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector =
                            if (PlayerManager.isListenMode) Icons.Default.Headphones
                            else Icons.Default.Audiotrack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = media.title,
                    style =
                        MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        if (PlayerManager.isListenMode) "🎧 Listen Mode • ${media.artist}"
                        else media.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Play/Pause
            FilledIconButton(
                onClick = {
                    view.slightHapticFeedback()
                    PlayerManager.togglePlayPause(context)
                },
                modifier = Modifier.size(38.dp),
                colors =
                    IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Icon(
                    imageVector =
                        if (PlayerManager.isPlaying) Icons.Default.Pause
                        else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(4.dp))

            // Close button
            IconButton(
                onClick = {
                    view.slightHapticFeedback()
                    PlayerManager.stop(context)
                },
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatTimeMs(timeMs: Long): String {
    val totalSeconds = (timeMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
