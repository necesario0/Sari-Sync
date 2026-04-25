package com.sarisync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.sarisync.ui.screens.InventoryScreen
import com.sarisync.ui.screens.UtangScreen
import com.sarisync.ui.theme.SariSyncTheme
import com.sarisync.ui.viewmodel.InventoryViewModel
import com.sarisync.ui.viewmodel.UtangViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inventoryViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
        val utangViewModel = ViewModelProvider(this)[UtangViewModel::class.java]

        setContent {
            SariSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(inventoryViewModel, utangViewModel)
                }
            }
        }
    }
}

@Composable
fun MainApp(
    inventoryViewModel: InventoryViewModel,
    utangViewModel: UtangViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inventory") },
                    label = { Text("Inventory") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Utang") },
                    label = { Text("Utang") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> InventoryScreen(viewModel = inventoryViewModel)
                1 -> UtangScreen(viewModel = utangViewModel)
            }
        }
    }
}