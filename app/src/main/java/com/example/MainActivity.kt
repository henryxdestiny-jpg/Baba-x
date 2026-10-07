package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudioTab
import com.example.ui.components.StudioHeader
import com.example.ui.screens.HhdStudioScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StemSplitterScreen
import com.example.ui.screens.VocalBoothScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HhdBgDark
import com.example.ui.theme.HhdBorder
import com.example.ui.theme.HhdSurfaceDark
import com.example.ui.theme.HhdSurfaceElevated
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSub
import com.example.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val studioViewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudioApp(viewModel = studioViewModel)
            }
        }
    }
}

@Composable
fun StudioApp(
    viewModel: StudioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.bannerMessage) {
        uiState.bannerMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissBanner()
        }
    }

    BackHandler(enabled = uiState.currentTab != StudioTab.HOME) {
        viewModel.selectTab(StudioTab.HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(HhdBgDark)
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            StudioHeader()
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HhdBorder)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = HhdSurfaceDark,
                contentColor = TextSub
            ) {
                StudioTab.values().forEach { tab ->
                    val isSelected = uiState.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        modifier = Modifier.testTag(tab.testTag),
                        icon = {
                            when (tab) {
                                StudioTab.HOME -> androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                                StudioTab.VOCAL_BOOTH -> androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mic Booth"
                                )
                                StudioTab.HHD_STUDIO -> androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "HHD Studio"
                                )
                                StudioTab.STEM_SPLITTER -> androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.CallSplit,
                                    contentDescription = "AI Stems"
                                )
                                StudioTab.SETTINGS -> androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyanAccent,
                            selectedTextColor = CyanAccent,
                            indicatorColor = HhdSurfaceElevated,
                            unselectedIconColor = TextSub,
                            unselectedTextColor = TextSub
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = HhdBgDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                StudioTab.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                StudioTab.VOCAL_BOOTH -> VocalBoothScreen(uiState = uiState, viewModel = viewModel)
                StudioTab.HHD_STUDIO -> HhdStudioScreen(uiState = uiState, viewModel = viewModel)
                StudioTab.STEM_SPLITTER -> StemSplitterScreen(uiState = uiState, viewModel = viewModel)
                StudioTab.SETTINGS -> SettingsScreen(uiState = uiState, viewModel = viewModel)
            }
        }
    }
}
