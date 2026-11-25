package com.sadsha.downloader.player

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sadsha.downloader.MainActivity
import com.sadsha.downloader.R

class BackgroundAudioService : Service() {

    companion object {
        const val CHANNEL_ID = "sadsha_media_playback_channel"
        const val NOTIFICATION_ID = 2024

        const val ACTION_START = "com.sadsha.downloader.action.START_LISTEN_MODE"
        const val ACTION_STOP = "com.sadsha.downloader.action.STOP_LISTEN_MODE"
        const val ACTION_TOGGLE_PLAY_PAUSE = "com.sadsha.downloader.action.TOGGLE_PLAY"
        const val ACTION_SEEK_FORWARD = "com.sadsha.downloader.action.SEEK_FORWARD"
        const val ACTION_SEEK_BACK = "com.sadsha.downloader.action.SEEK_BACK"

        fun startService(context: Context, media: PlayableMedia) {
            val intent =
                Intent(context, BackgroundAudioService::class.java).apply {
                    action = ACTION_START
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateNotification(context: Context) {
            val intent =
                Intent(context, BackgroundAudioService::class.java).apply {
                    action = "com.sadsha.downloader.action.UPDATE_NOTIFICATION"
                }
            try {
                context.startService(intent)
            } catch (_: Exception) {}
        }

        fun stopService(context: Context) {
            val intent =
                Intent(context, BackgroundAudioService::class.java).apply {
                    action = ACTION_STOP
                }
            try {
                context.startService(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START -> {
                val notification = buildMediaNotification()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val serviceType =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        } else {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        }
                    startForeground(NOTIFICATION_ID, notification, serviceType)
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            }

            ACTION_TOGGLE_PLAY_PAUSE -> {
                PlayerManager.togglePlayPause(this)
                updateCurrentNotification()
            }

            ACTION_SEEK_FORWARD -> {
                PlayerManager.forward10s(this)
                updateCurrentNotification()
            }

            ACTION_SEEK_BACK -> {
                PlayerManager.rewind10s(this)
                updateCurrentNotification()
            }

            ACTION_STOP -> {
                PlayerManager.stop(this)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            else -> {
                updateCurrentNotification()
            }
        }

        return START_NOT_STICKY
    }

    private fun updateCurrentNotification() {
        if (!PlayerManager.isListenMode && !PlayerManager.isPlaying) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return
        }
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        nm?.notify(NOTIFICATION_ID, buildMediaNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Listen Mode & Background Playback"
            val descriptionText = "Background media playback and lock screen controls"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    setShowBadge(false)
                    setSound(null, null)
                }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun buildMediaNotification(): Notification {
        val media = PlayerManager.currentMedia
        val title = media?.title ?: "SadSha Media Player"
        val artist = media?.artist ?: "Listen Mode Active (Background Audio)"
        val isPlaying = PlayerManager.isPlaying

        // Intent to open MainActivity on notification click
        val openAppIntent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val openAppPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // Action intents
        val togglePlayIntent =
            PendingIntent.getService(
                this,
                1,
                Intent(this, BackgroundAudioService::class.java).apply {
                    action = ACTION_TOGGLE_PLAY_PAUSE
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val rewindIntent =
            PendingIntent.getService(
                this,
                2,
                Intent(this, BackgroundAudioService::class.java).apply {
                    action = ACTION_SEEK_BACK
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val forwardIntent =
            PendingIntent.getService(
                this,
                3,
                Intent(this, BackgroundAudioService::class.java).apply {
                    action = ACTION_SEEK_FORWARD
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val stopIntent =
            PendingIntent.getService(
                this,
                4,
                Intent(this, BackgroundAudioService::class.java).apply {
                    action = ACTION_STOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val playPauseIcon =
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_seal)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText("🎧 Listen Mode")
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(android.R.drawable.ic_media_rew, "-10s", rewindIntent)
            .addAction(playPauseIcon, playPauseTitle, togglePlayIntent)
            .addAction(android.R.drawable.ic_media_ff, "+10s", forwardIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", stopIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$artist\n🎧 Listen Mode: Audio is playing in background. Lock screen or switch apps anytime.")
            )
            .build()
    }
}
