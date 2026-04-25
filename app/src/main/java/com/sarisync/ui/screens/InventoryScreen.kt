package com.sarisync.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarisync.data.entity.InventoryItem
import com.sarisync.ui.components.ScanButton
import com.sarisync.ui.localization.LocalStrings
import com.sarisync.ui.viewmodel.InventoryUiState
import com.sarisync.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {

    val strings = LocalStrings.current
    val state: InventoryUiState by viewModel.uiState.collectAsState()

    // ── Bottom Sheet state ──────────────────────────────
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // ── Search state ────────────────────────────────────
    var searchQuery by remember { mutableStateOf("") }

    // ── Sell dialog state ───────────────────────────────
    var showSellDialog by remember { mutableStateOf(false) }
    var sellTargetItem: InventoryItem? by remember { mutableStateOf(null) }
    var utangCustomerName by remember { mutableStateOf("") }

    // ── Restock dialog state ────────────────────────────
    var showRestockDialog by remember { mutableStateOf(false) }
    var restockTargetItem: InventoryItem? by remember { mutableStateOf(null) }
    var restockQuantity by remember { mutableStateOf("") }

    // ── Delete confirmation dialog state ────────────────
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTargetItem: InventoryItem? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.inventoryTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.addItemTitle,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            // ════════════════════════════════════════════
            // SEARCH BAR
            // ════════════════════════════════════════════
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it.uppercase() },
                label = { Text(strings.searchLabel) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ════════════════════════════════════════════
            // INVENTORY LIST
            // ════════════════════════════════════════════
            when (val currentState = state) {
                is InventoryUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(strings.loadingInventory)
                    }
                }

                is InventoryUiState.Success -> {
                    val allItems: List<InventoryItem> = currentState.items
                    val filteredItems: List<InventoryItem> = if (searchQuery.isBlank()) {
                        allItems
                    } else {
                        allItems.filter { item: InventoryItem ->
                            item.name.uppercase().contains(searchQuery) ||
                                    item.category.uppercase().contains(searchQuery)
                        }
                    }

                    if (filteredItems.isEmpty()) {
                        Text(
                            text = if (searchQuery.isNotBlank()) strings.noSearchResults else strings.emptyInventory,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = filteredItems,
                                key = { item: InventoryItem -> item.id }
                            ) { item: InventoryItem ->
                                InventoryItemCard(
                                    item = item,
                                    strings = strings,
                                    onSell = {
                                        sellTargetItem = item
                                        utangCustomerName = ""
                                        showSellDialog = true
                                    },
                                    onRestock = {
                                        restockTargetItem = item
                                        restockQuantity = ""
                                        showRestockDialog = true
                                    },
                                    onDelete = {
                                        deleteTargetItem = item
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                is InventoryUiState.Error -> {
                    Text(
                        text = "Error: ${currentState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════
    // ADD ITEM BOTTOM SHEET
    // ════════════════════════════════════════════════════
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddItemForm(
                strings = strings,
                viewModel = viewModel,
                onDone = {
                    scope.launch {
                        sheetState.hide()
                        showAddSheet = false
                    }
                }
            )
        }
    }

    // ════════════════════════════════════════════════════
    // SELL DIALOG (Cash or Utang)
    // ════════════════════════════════════════════════════
    if (showSellDialog && sellTargetItem != null) {
        AlertDialog(
            onDismissRequest = { showSellDialog = false },
            title = {
                Text(
                    text = strings.sellDialogTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${sellTargetItem!!.name} — ₱${"%.2f".format(sellTargetItem!!.price)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.sellDialogQuestion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Cash button
                    Button(
                        onClick = {
                            viewModel.sellOne(sellTargetItem!!, isCash = true, customerName = null)
                            showSellDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E3C)
                        )
                    ) {
                        Text(strings.cashButton, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Utang section
                    OutlinedTextField(
                        value = utangCustomerName,
                        onValueChange = { utangCustomerName = it.uppercase() },
                        label = { Text(strings.customerNameLabel) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (utangCustomerName.isNotBlank()) {
                                viewModel.sellOne(
                                    sellTargetItem!!,
                                    isCash = false,
                                    customerName = utangCustomerName.trim()
                                )
                                showSellDialog = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF57C00)
                        ),
                        enabled = utangCustomerName.isNotBlank()
                    ) {
                        Text(strings.utangButton, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSellDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }

    // ════════════════════════════════════════════════════
    // RESTOCK DIALOG
    // ════════════════════════════════════════════════════
    if (showRestockDialog && restockTargetItem != null) {
        AlertDialog(
            onDismissRequest = { showRestockDialog = false },
            title = {
                Text(
                    text = strings.restockDialogTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = restockTargetItem!!.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${strings.currentStockLabel}: ${restockTargetItem!!.currentStock}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = restockQuantity,
                        onValueChange = { restockQuantity = it },
                        label = { Text(strings.restockQuantityLabel) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = restockQuantity.toIntOrNull() ?: 0
                        if (qty > 0) {
                            viewModel.restockItem(restockTargetItem!!.id, qty)
                            showRestockDialog = false
                        }
                    },
                    enabled = (restockQuantity.toIntOrNull() ?: 0) > 0
                ) {
                    Text(strings.restockConfirmButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestockDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }

    // ════════════════════════════════════════════════════
    // DELETE CONFIRMATION DIALOG
    // ════════════════════════════════════════════════════
    if (showDeleteDialog && deleteTargetItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = strings.deleteDialogTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${strings.deleteDialogMessage} \"${deleteTargetItem!!.name}\"?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem(deleteTargetItem!!)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(strings.deleteConfirmButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}

// ════════════════════════════════════════════════════════════
// ADD ITEM FORM (inside Bottom Sheet)
// ════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemForm(
    strings: com.sarisync.ui.localization.AppStrings,
    viewModel: InventoryViewModel,
    onDone: () -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Inumin", "Pagkain", "Pangluto", "Gamit sa Bahay", "Meryenda", "Iba Pa")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = strings.addItemTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ML Kit Scan
        ScanButton(onTextExtracted = { text: String -> itemName = text.uppercase() })

        Spacer(modifier = Modifier.height(8.dp))

        // Item Name
        OutlinedTextField(
            value = itemName,
            onValueChange = { itemName = it.uppercase() },
            label = { Text(strings.itemNameLabel) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text(strings.categoryLabel) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { category: String ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price and Cost Price row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(strings.priceLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).height(64.dp),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = costPrice,
                onValueChange = { costPrice = it },
                label = { Text(strings.costPriceLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).height(64.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stock
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text(strings.stockLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                val parsedPrice = price.toDoubleOrNull() ?: 0.0
                val parsedCost = costPrice.toDoubleOrNull() ?: 0.0
                val parsedStock = stock.toIntOrNull() ?: 0

                if (itemName.isNotBlank() && selectedCategory.isNotBlank() && parsedPrice > 0) {
                    viewModel.addItem(
                        name = itemName.trim().uppercase(),
                        category = selectedCategory,
                        price = parsedPrice,
                        stock = parsedStock,
                        costPrice = parsedCost
                    )
                    onDone()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = strings.saveButton,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// INVENTORY ITEM CARD
// ════════════════════════════════════════════════════════════
@Composable
fun InventoryItemCard(
    item: InventoryItem,
    strings: com.sarisync.ui.localization.AppStrings,
    onSell: () -> Unit,
    onRestock: () -> Unit,
    onDelete: () -> Unit
) {
    val stockColor: Color = when {
        item.currentStock <= 0 -> Color(0xFFD32F2F)
        item.currentStock <= 10 -> Color(0xFFF57C00)
        else -> Color(0xFF388E3C)
    }

    val stockText: String = when {
        item.currentStock <= 0 -> strings.stockOut
        item.currentStock <= 10 -> "${item.currentStock} ${strings.stockLow}"
        else -> "${item.currentStock} ${strings.stockHealthy}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row: Name + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = strings.deleteButton,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price + Stock row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₱${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stockText,
                    fontWeight = FontWeight.Bold,
                    color = stockColor,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons row: Sell + Restock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSell,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = item.currentStock > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(strings.sellButton, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onRestock,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.restockButton, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
