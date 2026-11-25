package com.sadsha.downloader.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadsha.downloader.R
import com.sadsha.downloader.ui.common.LocalDarkTheme
import com.sadsha.downloader.util.DarkThemePreference
import com.sadsha.downloader.util.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.sadsha.downloader.util.DarkThemePreference.Companion.OFF
import com.sadsha.downloader.util.DarkThemePreference.Companion.ON
import com.sadsha.downloader.util.PreferenceUtil

private data class ThemeOption(
    val id: Int,
    val titleRes: Int,
    val icon: ImageVector,
    val testTag: String,
)

private val themeOptions =
    listOf(
        ThemeOption(OFF, R.string.theme_light, Icons.Rounded.LightMode, "theme_option_light"),
        ThemeOption(ON, R.string.theme_dark, Icons.Rounded.DarkMode, "theme_option_dark"),
        ThemeOption(
            FOLLOW_SYSTEM,
            R.string.theme_system,
            Icons.Outlined.Contrast,
            "theme_option_system",
        ),
    )

@Composable
fun ThemeSettingCard(
    modifier: Modifier = Modifier,
    onNavigateToDetails: (() -> Unit)? = null,
) {
    val darkThemePref = LocalDarkTheme.current
    val currentThemeValue = darkThemePref.darkThemeValue
    val isDarkActive = darkThemePref.isDarkTheme()

    val headerIcon =
        when (currentThemeValue) {
            ON -> Icons.Outlined.DarkMode
            OFF -> Icons.Outlined.LightMode
            else -> Icons.Outlined.Contrast
        }

    val subtitle =
        when (currentThemeValue) {
            ON -> stringResource(R.string.theme_dark_desc)
            OFF -> stringResource(R.string.theme_light_desc)
            else -> stringResource(R.string.theme_system_desc)
        }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("theme_setting_card"),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header: Title, Description, and Direct Light/Dark Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier.size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.theme_mode),
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Direct Toggle Switch between Light & Dark mode
                Switch(
                    modifier = Modifier.testTag("theme_toggle_switch"),
                    checked = isDarkActive,
                    onCheckedChange = { checked ->
                        PreferenceUtil.modifyDarkThemePreference(if (checked) ON else OFF)
                    },
                    thumbContent = {
                        Icon(
                            imageVector =
                                if (isDarkActive) Icons.Rounded.DarkMode
                                else Icons.Rounded.LightMode,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    },
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Way Segmented Control: [ Light | Dark | System ]
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().testTag("theme_segmented_row")
            ) {
                themeOptions.forEachIndexed { index, option ->
                    val selected = currentThemeValue == option.id
                    val shape = SegmentedButtonDefaults.itemShape(index, themeOptions.size)

                    SingleChoiceSegmentedButton(
                        selected = selected,
                        onClick = { PreferenceUtil.modifyDarkThemePreference(option.id) },
                        shape = shape,
                        modifier = Modifier.testTag(option.testTag),
                        icon = {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(option.titleRes),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                    )
                }
            }

            if (onNavigateToDetails != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier =
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onNavigateToDetails() }
                            .testTag("theme_more_options_button"),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.display_settings),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
