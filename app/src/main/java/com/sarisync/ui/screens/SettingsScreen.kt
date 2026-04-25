package com.sarisync.ui.screens

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarisync.data.SariSyncDatabase
import com.sarisync.ui.localization.AppThemeMode
import com.sarisync.ui.localization.Language
import com.sarisync.ui.localization.LanguageManager
import com.sarisync.ui.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Settings screen — the main customisation area for the app.
 *
 * Designed to be simple, clear, and beginner-friendly.
 * Uses easy-to-understand labels and short helper descriptions.
 *
 * Sections:
 *  1. Business Info (store name)
 *  2. Language
 *  3. Money Format (currency)
 *  4. App Appearance (light / dark / auto)
 *  5. Notifications (low stock, sales)
 *  6. Data Management (delete all)
 *  7. Help (simple guide)
 *  8. App version
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    val strings = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Local state for editable fields ────────────────
    var businessNameField by remember { mutableStateOf(LanguageManager.businessName) }
    var showNameSaved by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteDone by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.settingsTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ════════════════════════════════════════════
            // 1. BUSINESS INFO
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsBusinessInfo,
                description = strings.settingsBusinessInfoDesc
            ) {
                OutlinedTextField(
                    value = businessNameField,
                    onValueChange = { businessNameField = it },
                    label = { Text(strings.settingsBusinessName) },
                    placeholder = { Text(strings.settingsBusinessNameHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        LanguageManager.updateBusinessName(businessNameField.trim())
                        showNameSaved = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.save, fontWeight = FontWeight.Bold)
                }

                if (showNameSaved) {
                    Text(
                        text = strings.settingsBusinessNameSaved,
                        color = Color(0xFF388E3C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ════════════════════════════════════════════
            // 2. LANGUAGE
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsLanguage,
                description = strings.settingsLanguageDesc
            ) {
                val currentLang = LanguageManager.currentLanguage

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsChoiceChip(
                        label = strings.languageEnglish,
                        isSelected = currentLang == Language.ENGLISH,
                        onClick = { LanguageManager.setLanguage(Language.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                    SettingsChoiceChip(
                        label = strings.languageFilipino,
                        isSelected = currentLang == Language.FILIPINO,
                        onClick = { LanguageManager.setLanguage(Language.FILIPINO) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ════════════════════════════════════════════
            // 3. MONEY FORMAT
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsMoneyFormat,
                description = strings.settingsMoneyFormatDesc
            ) {
                val currentCurrency = LanguageManager.currencySymbol

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsChoiceChip(
                        label = strings.settingsCurrencyPeso,
                        isSelected = currentCurrency == "₱",
                        onClick = { LanguageManager.updateCurrencySymbol("₱") },
                        modifier = Modifier.weight(1f)
                    )
                    SettingsChoiceChip(
                        label = strings.settingsCurrencyDollar,
                        isSelected = currentCurrency == "$",
                        onClick = { LanguageManager.updateCurrencySymbol("$") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ════════════════════════════════════════════
            // 4. APP APPEARANCE
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsAppearance,
                description = strings.settingsAppearanceDesc
            ) {
                val currentTheme = LanguageManager.themeMode

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsChoiceChip(
                        label = strings.settingsThemeLight,
                        isSelected = currentTheme == AppThemeMode.LIGHT,
                        onClick = { LanguageManager.updateThemeMode(AppThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )
                    SettingsChoiceChip(
                        label = strings.settingsThemeDark,
                        isSelected = currentTheme == AppThemeMode.DARK,
                        onClick = { LanguageManager.updateThemeMode(AppThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                    SettingsChoiceChip(
                        label = strings.settingsThemeSystem,
                        isSelected = currentTheme == AppThemeMode.SYSTEM,
                        onClick = { LanguageManager.updateThemeMode(AppThemeMode.SYSTEM) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ════════════════════════════════════════════
            // 5. NOTIFICATIONS
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsNotifications,
                description = strings.settingsNotificationsDesc
            ) {
                SettingsToggleRow(
                    label = strings.settingsNotifLowStock,
                    description = strings.settingsNotifLowStockDesc,
                    checked = LanguageManager.notifLowStock,
                    onCheckedChange = { LanguageManager.updateNotifLowStock(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SettingsToggleRow(
                    label = strings.settingsNotifSales,
                    description = strings.settingsNotifSalesDesc,
                    checked = LanguageManager.notifSales,
                    onCheckedChange = { LanguageManager.updateNotifSales(it) }
                )
            }

            // ════════════════════════════════════════════
            // 6. DATA MANAGEMENT
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsDataManagement,
                description = strings.settingsDataManagementDesc
            ) {
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.settingsDeleteAll, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = strings.settingsDeleteAllDesc,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (showDeleteDone) {
                    Text(
                        text = strings.settingsDeleteAllDone,
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // ════════════════════════════════════════════
            // 7. HELP
            // ════════════════════════════════════════════
            SettingsSection(
                title = strings.settingsHelp,
                description = strings.settingsHelpDesc
            ) {
                OutlinedButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.settingsHelpTitle, fontWeight = FontWeight.SemiBold)
                }
            }

            // ════════════════════════════════════════════
            // 8. APP VERSION
            // ════════════════════════════════════════════
            Text(
                text = strings.settingsVersion,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Extra bottom padding for navigation bar
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ── Delete All Confirmation Dialog ──────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    strings.settingsDeleteAll,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(strings.settingsDeleteAllWarning)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        scope.launch(Dispatchers.IO) {
                            val db = SariSyncDatabase.getDatabase(context)
                            db.inventoryDao().deleteAll()
                            db.salesDao().deleteAll()
                            db.creditTransactionDao().deleteAll()
                        }
                        showDeleteDone = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(strings.settingsDeleteAllConfirm, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    // ── Help Dialog ─────────────────────────────────────
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    strings.settingsHelpTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Parse the help content with simple bold markers
                    HelpContentText(strings.settingsHelpContent)
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(strings.ok, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ════════════════════════════════════════════════════════
// REUSABLE SETTINGS COMPONENTS
// ════════════════════════════════════════════════════════

/**
 * A settings section card with a title, description, and content slot.
 */
@Composable
private fun SettingsSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

/**
 * A selectable chip / button used for language, currency, and theme choices.
 */
@Composable
private fun SettingsChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    val containerColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surface

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor
        )
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * A row with a label, description, and a toggle switch.
 */
@Composable
private fun SettingsToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Renders the help content string with simple **bold** markers.
 */
@Composable
private fun HelpContentText(content: String) {
    val lines = content.trimIndent().lines()

    for (line in lines) {
        if (line.isBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
        } else if (line.startsWith("**") && line.endsWith("**")) {
            // Bold section header
            Text(
                text = line.removePrefix("**").removeSuffix("**"),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        } else {
            Text(
                text = line,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}
