package com.sarisync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.sarisync.ui.localization.LanguageManager
import com.sarisync.ui.localization.LocalStrings
import com.sarisync.ui.localization.currentStrings
import com.sarisync.ui.screens.DashboardScreen
import com.sarisync.ui.screens.InventoryScreen
import com.sarisync.ui.screens.SettingsScreen
import com.sarisync.ui.screens.UtangScreen
import com.sarisync.ui.screens.WelcomeScreen
import com.sarisync.ui.theme.SariSyncTheme
import com.sarisync.ui.viewmodel.DashboardViewModel
import com.sarisync.ui.viewmodel.InventoryViewModel
import com.sarisync.ui.viewmodel.UtangViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialise all preferences from SharedPreferences
        LanguageManager.init(this)

        val inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
        val utangViewModel = ViewModelProvider(this)[UtangViewModel::class.java]
        val dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        setContent {
            // Theme now reads LanguageManager.themeMode internally
            SariSyncTheme {
                // Provide the current language strings to the entire tree
                CompositionLocalProvider(LocalStrings provides currentStrings) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppRoot(inventoryViewModel, utangViewModel, dashboardViewModel)
                    }
                }
            }
        }
    }
}

/**
 * Root composable that decides whether to show the Welcome screen
 * or the main app based on onboarding state.
 */
@Composable
fun AppRoot(
    inventoryViewModel: InventoryViewModel,
    utangViewModel: UtangViewModel,
    dashboardViewModel: DashboardViewModel
) {
    var showWelcome by remember { mutableStateOf(!LanguageManager.onboardingDone) }

    if (showWelcome) {
        WelcomeScreen(
            onGetStarted = { showWelcome = false }
        )
    } else {
        MainApp(inventoryViewModel, utangViewModel, dashboardViewModel)
    }
}

/**
 * Main app with four bottom navigation tabs:
 *  0 — Inventory
 *  1 — Credit / Utang
 *  2 — Dashboard
 *  3 — Settings (far right)
 */
@Composable
fun MainApp(
    inventoryViewModel: InventoryViewModel,
    utangViewModel: UtangViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = strings.navInventory) },
                    label = { Text(strings.navInventory) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = strings.navCredit) },
                    label = { Text(strings.navCredit) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = strings.navDashboard) },
                    label = { Text(strings.navDashboard) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = strings.navSettings) },
                    label = { Text(strings.navSettings) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> InventoryScreen(viewModel = inventoryViewModel)
                1 -> UtangScreen(viewModel = utangViewModel)
                2 -> DashboardScreen(viewModel = dashboardViewModel)
                3 -> SettingsScreen()
            }
        }
    }
}
