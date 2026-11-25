package com.sadsha.downloader.ui.page.settings.appearance

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sadsha.downloader.R
import com.sadsha.downloader.ui.common.LocalDarkTheme
import com.sadsha.downloader.ui.component.BackButton
import com.sadsha.downloader.ui.component.PreferenceSingleChoiceItem
import com.sadsha.downloader.ui.component.PreferenceSubtitle
import com.sadsha.downloader.ui.component.PreferenceSwitchVariant
import com.sadsha.downloader.ui.component.ThemeSettingCard
import com.sadsha.downloader.util.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.sadsha.downloader.util.DarkThemePreference.Companion.OFF
import com.sadsha.downloader.util.DarkThemePreference.Companion.ON
import com.sadsha.downloader.util.PreferenceUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePreferences(onNavigateBack: () -> Unit) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true },
        )
    val darkThemePreference = LocalDarkTheme.current
    val isHighContrastModeEnabled = darkThemePreference.isHighContrastModeEnabled

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(modifier = Modifier, text = stringResource(id = R.string.dark_theme))
                },
                navigationIcon = { BackButton { onNavigateBack() } },
                scrollBehavior = scrollBehavior,
            )
        },
        content = {
            LazyColumn(modifier = Modifier, contentPadding = it) {
                item {
                    ThemeSettingCard(
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        onNavigateToDetails = null,
                    )
                }

                item { PreferenceSubtitle(text = stringResource(R.string.theme_mode)) }

                if (Build.VERSION.SDK_INT >= 29) {
                    item {
                        PreferenceSingleChoiceItem(
                            modifier = Modifier.testTag("dark_theme_pref_system"),
                            text = stringResource(R.string.follow_system),
                            selected = darkThemePreference.darkThemeValue == FOLLOW_SYSTEM,
                        ) {
                            PreferenceUtil.modifyDarkThemePreference(FOLLOW_SYSTEM)
                        }
                    }
                }

                item {
                    PreferenceSingleChoiceItem(
                        modifier = Modifier.testTag("dark_theme_pref_dark"),
                        text = stringResource(R.string.theme_dark),
                        selected = darkThemePreference.darkThemeValue == ON,
                    ) {
                        PreferenceUtil.modifyDarkThemePreference(ON)
                    }
                }

                item {
                    PreferenceSingleChoiceItem(
                        modifier = Modifier.testTag("dark_theme_pref_light"),
                        text = stringResource(R.string.theme_light),
                        selected = darkThemePreference.darkThemeValue == OFF,
                    ) {
                        PreferenceUtil.modifyDarkThemePreference(OFF)
                    }
                }

                item { PreferenceSubtitle(text = stringResource(R.string.additional_settings)) }

                item {
                    PreferenceSwitchVariant(
                        title = stringResource(R.string.high_contrast),
                        icon = Icons.Outlined.Contrast,
                        isChecked = isHighContrastModeEnabled,
                        onClick = {
                            PreferenceUtil.modifyDarkThemePreference(
                                isHighContrastModeEnabled = !isHighContrastModeEnabled
                            )
                        },
                    )
                }
            }
        },
    )
}
