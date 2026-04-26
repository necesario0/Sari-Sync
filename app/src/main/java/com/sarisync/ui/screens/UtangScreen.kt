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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.sarisync.data.dao.CustomerDebtSummary
import com.sarisync.data.dao.PayerBehaviorSummary
import com.sarisync.ui.localization.LocalStrings
import com.sarisync.ui.viewmodel.UtangUiState
import com.sarisync.ui.viewmodel.UtangViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtangScreen(viewModel: UtangViewModel) {

    val strings = LocalStrings.current
    val state: UtangUiState by viewModel.uiState.collectAsState()
    val behaviorMap: Map<String, PayerBehaviorSummary> by viewModel.payerBehaviorMap.collectAsState()

    // ── Bottom Sheet state ──────────────────────────────
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // ── Search state ────────────────────────────────────
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.creditTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.addCredit,
                    tint = MaterialTheme.colorScheme.onSecondary
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

            // ── Search Bar ──────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it.uppercase() },
                label = { Text(strings.searchCustomerLabel) },
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

            // ── Debt List ───────────────────────────────
            when (val currentState = state) {
                is UtangUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(strings.loadingCredits)
                    }
                }

                is UtangUiState.Success -> {
                    val debts: List<CustomerDebtSummary> = currentState.debtSummaries
                    val filteredDebts: List<CustomerDebtSummary> = if (searchQuery.isBlank()) {
                        debts
                    } else {
                        debts.filter { it.customerName.uppercase().contains(searchQuery) }
                    }

                    Text(
                        text = strings.customersCount(filteredDebts.size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (filteredDebts.isEmpty()) {
                        Text(
                            text = if (searchQuery.isNotBlank()) strings.noSearchResults else strings.emptyCredits,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = filteredDebts,
                                key = { debt: CustomerDebtSummary -> debt.customerName }
                            ) { debt: CustomerDebtSummary ->
                                val behavior: PayerBehaviorSummary? = behaviorMap[debt.customerName]
                                DebtCard(
                                    debtSummary = debt,
                                    behavior = behavior
                                )
                            }
                        }
                    }
                }

                is UtangUiState.Error -> {
                    Text(
                        text = strings.errorPrefix + currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        }
    }

    // ════════════════════════════════════════════════════
    // ADD UTANG / RECORD PAYMENT BOTTOM SHEET
    // ════════════════════════════════════════════════════
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = sheetState
        ) {
            AddUtangForm(
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
}

// ════════════════════════════════════════════════════════════
// ADD UTANG / RECORD PAYMENT FORM (inside Bottom Sheet)
// ════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUtangForm(
    strings: com.sarisync.ui.localization.AppStrings,
    viewModel: UtangViewModel,
    onDone: () -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isPayment by remember { mutableStateOf(false) }
    var customerDropdownExpanded by remember { mutableStateOf(false) }
    val customerNames by viewModel.customerNames.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = if (isPayment) strings.recordPayment else strings.addCredit,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Toggle: Utang or Payment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isPayment = false },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isPayment) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = strings.addCreditButton,
                    color = if (!isPayment) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = { isPayment = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPayment) Color(0xFF388E3C)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = strings.paymentButton,
                    color = if (isPayment) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Customer Name
        if (isPayment && customerNames.isNotEmpty()) {
            // Payment: Use dropdown
            ExposedDropdownMenuBox(
                expanded = customerDropdownExpanded,
                onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Customer") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = customerDropdownExpanded,
                    onDismissRequest = { customerDropdownExpanded = false }
                ) {
                    customerNames.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(text = name) },
                            onClick = {
                                customerName = name
                                customerDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        } else {
            // Add Credit or Payment with no customers: Use text input
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it.uppercase() },
                label = { Text(strings.customerNameLabel) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(strings.amountLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                if (customerName.isNotBlank() && parsedAmount > 0) {
                    if (isPayment) {
                        viewModel.recordPayment(customerName.trim().uppercase(), parsedAmount)
                    } else {
                        viewModel.addUtang(customerName.trim().uppercase(), parsedAmount)
                    }
                    onDone()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPayment) Color(0xFF388E3C) else MaterialTheme.colorScheme.secondary
            ),
            enabled = customerName.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = strings.save,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = strings.save,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ════════════════════════════════════════════════════════════
// DEBT CARD WITH PAYER BEHAVIOR BADGE
// ════════════════════════════════════════════════════════════
@Composable
fun DebtCard(
    debtSummary: CustomerDebtSummary,
    behavior: PayerBehaviorSummary?
) {
    val strings = LocalStrings.current

    // ── Debt health status ──────────────────────────────
    val (healthStatus: String, healthColor: Color) = when {
        debtSummary.totalDebt <= 0 -> strings.statusPaid to Color(0xFF388E3C)
        debtSummary.totalDebt <= 200 -> strings.statusModerate to Color(0xFFF57C00)
        else -> strings.statusHigh to Color(0xFFD32F2F)
    }

    // ── Payer behavior classification (time-based) ──────
    val behaviorResult: Triple<String, Color, String> = if (behavior != null) {
        when {
            // Fully paid
            behavior.netDebt <= 0 -> Triple("Bayad Na", Color(0xFF388E3C), "✓")

            // Bad payer: unpaid for 30+ days
            (behavior.daysSinceLastUnpaid ?: 0) > 30 -> Triple("Masamang Nagbabayad", Color(0xFFD32F2F), "⚠️")

            // Bad payer: average payment delay > 30 days
            (behavior.averagePaymentDelayDays ?: 0) > 30 -> Triple("Masamang Nagbabayad", Color(0xFFD32F2F), "⚠️")

            // Average payer: pays within 30 days
            (behavior.averagePaymentDelayDays ?: 0) > 7 -> Triple("Katamtaman", Color(0xFFF57C00), "👌")

            // Good payer: pays within 7 days
            (behavior.averagePaymentDelayDays ?: 0) <= 7 -> Triple("Mabuting Nagbabayad", Color(0xFF388E3C), "👍")

            // Default: no payment history yet
            else -> Triple("Walang History", Color(0xFF757575), "🆕")
        }
    } else {
        Triple("Walang History", Color(0xFF757575), "🆕")
    }

    val behaviorLabel: String = behaviorResult.first
    val behaviorColor: Color = behaviorResult.second
    val behaviorEmoji: String = behaviorResult.third

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Customer name
                Text(
                    text = debtSummary.customerName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Debt amount
                Text(
                    text = "₱${"%.2f".format(debtSummary.totalDebt)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = healthColor
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Debt health status
                Text(
                    text = healthStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Payer behavior badge
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = behaviorColor.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = behaviorEmoji, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = behaviorLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = behaviorColor
                        )
                    }
                }

                // Payment delay info (if available)
                if (behavior != null && behavior.averagePaymentDelayDays != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Avg. payment delay: ${behavior.averagePaymentDelayDays} days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Health indicator circle
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = healthColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when {
                            debtSummary.totalDebt <= 0 -> "✓"
                            debtSummary.totalDebt <= 200 -> "!"
                            else -> "⚠"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
