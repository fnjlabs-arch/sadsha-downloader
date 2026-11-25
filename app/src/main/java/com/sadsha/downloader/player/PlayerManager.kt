package com.sadsha.downloader.player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.view.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class PlayableMedia(
    val title: String,
    val artist: String,
    val mediaUri: String,
    val thumbnailUrl: String? = null,
    val isLocal: Boolean = false,
) {
    fun isYouTubeUrl(): Boolean {
        val u = mediaUri.lowercase()
        return u.contains("youtube.com") || u.contains("youtu.be")
    }

    fun extractYouTubeId(): String? {
        val url = mediaUri
        return when {
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?").substringBefore("&").substringBefore("/")
            }
            url.contains("watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&").substringBefore("#")
            }
            url.contains("youtube.com/embed/") -> {
                url.substringAfter("embed/").substringBefore("?").substringBefore("&")
            }
            url.contains("youtube.com/shorts/") -> {
                url.substringAfter("shorts/").substringBefore("?").substringBefore("&")
            }
            else -> null
        }?.takeIf { it.isNotBlank() }
    }
}

object PlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var wakeLock: PowerManager.WakeLock? = null
    private var currentSurface: Surface? = null

    var currentMedia by mutableStateOf<PlayableMedia?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var isListenMode by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var playbackError by mutableStateOf<String?>(null)
        private set

    var currentPositionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(0L)
        private set

    var playbackSpeed by mutableFloatStateOf(1.0f)
        private set

    var videoAspectRatio by mutableFloatStateOf(16f / 9f)
        private set

    var isPlayerVisible by mutableStateOf(false)
        private set

    private val _playbackStateFlow = MutableStateFlow(false)
    val playbackStateFlow = _playbackStateFlow.asStateFlow()

    fun getMediaPlayer(): MediaPlayer? = mediaPlayer

    fun setSurface(surface: Surface?) {
        currentSurface = surface
        try {
            mediaPlayer?.setSurface(surface)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(
        context: Context,
        media: PlayableMedia,
        startInListenMode: Boolean = false,
        showUi: Boolean = true,
    ) {
        try {
            stop(context)

            currentMedia = media
            isListenMode = startInListenMode
            isPlayerVisible = showUi
            isLoading = true
            playbackError = null
            currentPositionMs = 0L
            durationMs = 0L

            // For online YouTube videos that aren't in listen mode, InAppVideoPlayer renders YouTube WebView
            if (media.isYouTubeUrl() && !media.isLocal && !startInListenMode) {
                isLoading = false
                isPlaying = true
                _playbackStateFlow.value = true
                return
            }

            val player = MediaPlayer()
            mediaPlayer = player

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )

            // Acquire partial wake lock for background/lock-screen playback
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SadSha:ListenModeWakeLock")
            wakeLock?.setReferenceCounted(false)
            player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)

            // Safe dataSource setup for local files, content URIs, and remote streams
            val path = media.mediaUri
            when {
                path.startsWith("content://") -> {
                    player.setDataSource(context, Uri.parse(path))
                }
                path.startsWith("file://") -> {
                    val cleanPath = path.removePrefix("file://")
                    player.setDataSource(cleanPath)
                }
                path.startsWith("http://") || path.startsWith("https://") -> {
                    player.setDataSource(context, Uri.parse(path))
                }
                else -> {
                    val f = File(path)
                    if (f.exists() && f.canRead()) {
                        player.setDataSource(f.absolutePath)
                    } else {
                        player.setDataSource(context, Uri.parse(path))
                    }
                }
            }

            player.setOnVideoSizeChangedListener { _, width, height ->
                if (width > 0 && height > 0) {
                    videoAspectRatio = width.toFloat() / height.toFloat()
                }
            }

            player.setOnPreparedListener { mp ->
                isLoading = false
                playbackError = null
                durationMs = mp.duration.toLong().coerceAtLeast(0L)

                currentSurface?.let { s ->
                    try {
                        mp.setSurface(s)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                applyPlaybackSpeed(playbackSpeed)
                try {
                    mp.start()
                    isPlaying = true
                    _playbackStateFlow.value = true
                    startProgressTracker()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (isListenMode) {
                    wakeLock?.acquire(3600_000L) // 1 hour safety timeout
                    BackgroundAudioService.startService(context, media)
                }
            }

            player.setOnCompletionListener {
                isPlaying = false
                _playbackStateFlow.value = false
                currentPositionMs = durationMs
                BackgroundAudioService.updateNotification(context)
            }

            player.setOnErrorListener { _, what, extra ->
                isLoading = false
                isPlaying = false
                _playbackStateFlow.value = false
                playbackError = "Playback error ($what, $extra)"
                true
            }

            player.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
            isPlaying = false
            _playbackStateFlow.value = false
            playbackError = e.localizedMessage ?: "Failed to open video"
        }
    }

    fun togglePlayPause(context: Context) {
        val media = currentMedia ?: return
        if (media.isYouTubeUrl() && !media.isLocal && !isListenMode) {
            isPlaying = !isPlaying
            _playbackStateFlow.value = isPlaying
            return
        }

        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) {
                pause(context)
            } else {
                resume(context)
            }
        } catch (e: Exception) {
            try {
                resume(context)
            } catch (_: Exception) {}
        }
    }

    fun pause(context: Context) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (_: Exception) {}
        isPlaying = false
        _playbackStateFlow.value = false
        if (isListenMode) {
            BackgroundAudioService.updateNotification(context)
        }
    }

    fun resume(context: Context) {
        try {
            mediaPlayer?.start()
            isPlaying = true
            _playbackStateFlow.value = true
            startProgressTracker()
            if (isListenMode) {
                wakeLock?.acquire(3600_000L)
                BackgroundAudioService.updateNotification(context)
            }
        } catch (_: Exception) {}
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.let {
                val target = positionMs.coerceIn(0L, durationMs).toInt()
                it.seekTo(target)
                currentPositionMs = target.toLong()
            }
        } catch (_: Exception) {}
    }

    fun forward10s(context: Context) {
        val next = (currentPositionMs + 10_000L).coerceAtMost(durationMs)
        seekTo(next)
        if (isListenMode) BackgroundAudioService.updateNotification(context)
    }

    fun rewind10s(context: Context) {
        val prev = (currentPositionMs - 10_000L).coerceAtLeast(0L)
        seekTo(prev)
        if (isListenMode) BackgroundAudioService.updateNotification(context)
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mediaPlayer?.let {
                    val params = it.playbackParams
                    params.speed = speed
                    it.playbackParams = params
                }
            } catch (_: Exception) {}
        }
    }

    fun toggleListenMode(context: Context) {
        isListenMode = !isListenMode
        val media = currentMedia ?: return
        if (isListenMode) {
            wakeLock?.acquire(3600_000L)
            BackgroundAudioService.startService(context, media)
        } else {
            if (wakeLock?.isHeld == true) wakeLock?.release()
            BackgroundAudioService.stopService(context)
        }
    }

    fun showPlayer() {
        isPlayerVisible = true
    }

    fun hidePlayer() {
        isPlayerVisible = false
    }

    fun stop(context: Context) {
        progressJob?.cancel()
        progressJob = null
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        } catch (_: Exception) {}

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}

        mediaPlayer = null
        currentSurface = null
        isPlaying = false
        isLoading = false
        playbackError = null
        _playbackStateFlow.value = false
        currentPositionMs = 0L
        durationMs = 0L

        BackgroundAudioService.stopService(context)
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob =
            scope.launch {
                while (isActive) {
                    try {
                        mediaPlayer?.let {
                            if (it.isPlaying) {
                                currentPositionMs = it.currentPosition.toLong().coerceAtLeast(0L)
                                durationMs = it.duration.toLong().coerceAtLeast(0L)
                            }
                        }
                    } catch (_: Exception) {}
                    delay(400)
                }
            }
    }
}
