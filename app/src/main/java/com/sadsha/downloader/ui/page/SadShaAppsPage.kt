package com.sadsha.downloader.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadsha.downloader.ui.component.SadShaAppsShowcase
import com.sadsha.downloader.ui.component.SadShaFooter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SadShaAppsPage(
    onNavigateBack: () -> Unit = {},
    onMenuOpen: (() -> Unit)? = null,
) {
    Scaffold(
        containerColor = Color(0xFF090D18),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SadSha Apps Ecosystem",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0D1322),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color(0xFF00F5FF),
                    ),
                navigationIcon = {
                    if (onMenuOpen != null) {
                        IconButton(onClick = onMenuOpen) {
                            Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(Color(0xFF090D18))
                    .padding(paddingValues)
        ) {
            Column(
                modifier =
                    Modifier.fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
            ) {
                SadShaAppsShowcase()
                SadShaFooter()
            }
        }
    }
}
