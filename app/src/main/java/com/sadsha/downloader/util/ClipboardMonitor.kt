package com.sadsha.downloader.util

import android.net.Uri

object ClipboardMonitor {

    private var lastProcessedUrl: String = ""
    private var lastDismissedUrl: String = ""

    private val VIDEO_DOMAINS =
        listOf(
            "youtube.com",
            "youtu.be",
            "tiktok.com",
            "instagram.com",
            "twitter.com",
            "x.com",
            "facebook.com",
            "fb.watch",
            "reddit.com",
            "vimeo.com",
            "bilibili.com",
            "twitch.tv",
            "dailymotion.com",
            "soundcloud.com",
            "streamable.com",
            "pinterest.com",
            "vk.com",
        )

    private val VIDEO_EXTENSIONS =
        listOf(".mp4", ".mkv", ".webm", ".m4a", ".mp3", ".flv", ".mov", ".avi", ".m3u8")

    fun extractVideoUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val urls = findURLsFromString(text.trim(), firstMatchOnly = false)
        if (urls.isEmpty()) return null

        // Prioritize known video domains
        for (url in urls) {
            val lower = url.lowercase()
            if (VIDEO_DOMAINS.any { lower.contains(it) }) {
                return url
            }
            if (VIDEO_EXTENSIONS.any { lower.contains(it) }) {
                return url
            }
        }

        // Otherwise return first valid http/https URL
        return urls.firstOrNull()
    }

    fun getPlatformName(url: String): String {
        val lower = url.lowercase()
        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> "YouTube"
            lower.contains("tiktok.com") -> "TikTok"
            lower.contains("instagram.com") -> "Instagram"
            lower.contains("twitter.com") || lower.contains("x.com") -> "Twitter / X"
            lower.contains("facebook.com") || lower.contains("fb.watch") -> "Facebook"
            lower.contains("reddit.com") -> "Reddit"
            lower.contains("vimeo.com") -> "Vimeo"
            lower.contains("bilibili.com") -> "Bilibili"
            lower.contains("soundcloud.com") -> "SoundCloud"
            lower.contains("twitch.tv") -> "Twitch"
            lower.contains("dailymotion.com") -> "Dailymotion"
            else -> {
                runCatching {
                    Uri.parse(url).host?.removePrefix("www.") ?: "Video Link"
                }.getOrDefault("Video Link")
            }
        }
    }

    fun getPlatformIconText(url: String): String {
        val lower = url.lowercase()
        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> "▶️"
            lower.contains("tiktok.com") -> "🎵"
            lower.contains("instagram.com") -> "📸"
            lower.contains("twitter.com") || lower.contains("x.com") -> "🐦"
            lower.contains("facebook.com") || lower.contains("fb.watch") -> "👥"
            lower.contains("reddit.com") -> "🤖"
            lower.contains("soundcloud.com") -> "🎶"
            else -> "🔗"
        }
    }

    fun shouldPrompt(url: String): Boolean {
        if (url.isBlank()) return false
        val trimmed = url.trim()
        if (trimmed == lastProcessedUrl || trimmed == lastDismissedUrl) {
            return false
        }
        return true
    }

    fun markAsProcessed(url: String) {
        lastProcessedUrl = url.trim()
    }

    fun markAsDismissed(url: String) {
        lastDismissedUrl = url.trim()
    }

    fun clearCache() {
        lastProcessedUrl = ""
        lastDismissedUrl = ""
    }
}
