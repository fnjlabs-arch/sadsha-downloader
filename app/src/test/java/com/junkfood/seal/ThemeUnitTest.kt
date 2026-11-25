package com.sadsha.downloader

import com.sadsha.downloader.util.DarkThemePreference
import com.sadsha.downloader.util.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.sadsha.downloader.util.DarkThemePreference.Companion.OFF
import com.sadsha.downloader.util.DarkThemePreference.Companion.ON
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeUnitTest {

    @Test
    fun testDarkThemePreferenceDefaults() {
        val defaultPref = DarkThemePreference()
        assertEquals(FOLLOW_SYSTEM, defaultPref.darkThemeValue)
        assertFalse(defaultPref.isHighContrastModeEnabled)
    }

    @Test
    fun testDarkThemePreferenceExplicitStates() {
        val lightPref = DarkThemePreference(darkThemeValue = OFF)
        assertEquals(OFF, lightPref.darkThemeValue)

        val darkPref = DarkThemePreference(darkThemeValue = ON)
        assertEquals(ON, darkPref.darkThemeValue)

        val systemPref = DarkThemePreference(darkThemeValue = FOLLOW_SYSTEM)
        assertEquals(FOLLOW_SYSTEM, systemPref.darkThemeValue)
    }

    @Test
    fun testHighContrastState() {
        val highContrastPref = DarkThemePreference(darkThemeValue = ON, isHighContrastModeEnabled = true)
        assertTrue(highContrastPref.isHighContrastModeEnabled)
        assertEquals(ON, highContrastPref.darkThemeValue)
    }
}
