package com.sadsha.downloader

import com.sadsha.downloader.util.ClipboardMonitor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClipboardAndHistoryUnitTest {

    @Before
    fun setup() {
        ClipboardMonitor.clearCache()
    }

    @Test
    fun testExtractVideoUrlFromDirectLink() {
        val ytUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        val detected = ClipboardMonitor.extractVideoUrl(ytUrl)
        assertEquals(ytUrl, detected)
    }

    @Test
    fun testExtractVideoUrlFromMixedText() {
        val text = "Check out this song! https://youtu.be/dQw4w9WgXcQ it is trending."
        val detected = ClipboardMonitor.extractVideoUrl(text)
        assertEquals("https://youtu.be/dQw4w9WgXcQ", detected)
    }

    @Test
    fun testExtractTikTokUrl() {
        val tiktok = "https://www.tiktok.com/@creator/video/7123456789012345678"
        val detected = ClipboardMonitor.extractVideoUrl(tiktok)
        assertEquals(tiktok, detected)
    }

    @Test
    fun testExtractInstagramUrl() {
        val ig = "https://www.instagram.com/reel/Cx123456789/"
        val detected = ClipboardMonitor.extractVideoUrl(ig)
        assertEquals(ig, detected)
    }

    @Test
    fun testExtractTwitterUrl() {
        val xUrl = "https://x.com/news/status/1234567890"
        val detected = ClipboardMonitor.extractVideoUrl(xUrl)
        assertEquals(xUrl, detected)
    }

    @Test
    fun testNonUrlReturnsNull() {
        val nonUrl = "Just a regular copied message with no links."
        assertNull(ClipboardMonitor.extractVideoUrl(nonUrl))
        assertNull(ClipboardMonitor.extractVideoUrl(null))
        assertNull(ClipboardMonitor.extractVideoUrl("   "))
    }

    @Test
    fun testGetPlatformName() {
        assertEquals("YouTube", ClipboardMonitor.getPlatformName("https://www.youtube.com/watch?v=123"))
        assertEquals("YouTube", ClipboardMonitor.getPlatformName("https://youtu.be/123"))
        assertEquals("TikTok", ClipboardMonitor.getPlatformName("https://www.tiktok.com/@abc/video/123"))
        assertEquals("Instagram", ClipboardMonitor.getPlatformName("https://www.instagram.com/p/123/"))
        assertEquals("Twitter / X", ClipboardMonitor.getPlatformName("https://x.com/abc/status/123"))
        assertEquals("Facebook", ClipboardMonitor.getPlatformName("https://www.facebook.com/watch?v=123"))
        assertEquals("Reddit", ClipboardMonitor.getPlatformName("https://reddit.com/r/videos/123"))
    }

    @Test
    fun testPromptDeduplication() {
        val url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        assertTrue(ClipboardMonitor.shouldPrompt(url))

        // Mark as processed -> should not prompt again
        ClipboardMonitor.markAsProcessed(url)
        assertFalse(ClipboardMonitor.shouldPrompt(url))

        // Different URL should still prompt
        val anotherUrl = "https://www.youtube.com/watch?v=another123"
        assertTrue(ClipboardMonitor.shouldPrompt(anotherUrl))

        // Mark as dismissed -> should not prompt
        ClipboardMonitor.markAsDismissed(anotherUrl)
        assertFalse(ClipboardMonitor.shouldPrompt(anotherUrl))

        // Clear cache -> should prompt again
        ClipboardMonitor.clearCache()
        assertTrue(ClipboardMonitor.shouldPrompt(url))
    }
}
