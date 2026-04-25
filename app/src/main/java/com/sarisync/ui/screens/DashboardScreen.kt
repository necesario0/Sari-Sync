package com.sarisync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarisync.ui.components.LanguageSwitcher
import com.sarisync.ui.components.SimpleBarChart
import com.sarisync.ui.components.SimpleDualLineChart
import com.sarisync.ui.localization.LocalStrings
import com.sarisync.ui.viewmodel.DashboardUiState
import com.sarisync.ui.viewmodel.DashboardViewModel
import com.sarisync.ui.viewmodel.TimePeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {

    val strings = LocalStrings.current
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = strings.dashboardTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    titleContentColor = MaterialTheme.colorScheme.onTertiary
                )
            )
        }
    ) { innerPadding ->

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(strings.dashboardLoading)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Language Switcher ───────────────────
                LanguageSwitcher(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // ── Time Period Selector ────────────────
                TimePeriodSelector(
                    selected = state.selectedPeriod,
                    onSelect = { viewModel.setPeriod(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ══════════════════════════════════════════
                // KEY METRICS CARDS
                // ══════════════════════════════════════════

                Text(
                    text = strings.dashboardKeyMetrics,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row 1: Revenue & Cost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = strings.dashboardRevenue,
                        value = "₱${"%.2f".format(state.totalRevenue)}",
                        color = Color(0xFF388E3C),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = strings.dashboardCost,
                        value = "₱${"%.2f".format(state.totalCost)}",
                        color = Color(0xFFF57C00),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Net Profit & Profit Margin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = strings.dashboardNetProfit,
                        value = "₱${"%.2f".format(state.netProfit)}",
                        color = if (state.netProfit >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = strings.dashboardProfitMargin,
                        value = "${"%.1f".format(state.profitMarginPercent)}%",
                        color = if (state.profitMarginPercent >= 20) Color(0xFF388E3C) else Color(0xFFF57C00),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Units Sold & Outstanding Credit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = strings.dashboardUnitsSold,
                        value = "${state.totalUnitsSold}",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = strings.dashboardOutstandingCredit,
                        value = "₱${"%.2f".format(state.totalOutstandingCredit)}",
                        color = if (state.totalOutstandingCredit > 0) Color(0xFFD32F2F) else Color(0xFF388E3C),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ══════════════════════════════════════════
                // SALES TREND LINE CHART (Revenue vs Cost)
                // ══════════════════════════════════════════

                if (state.dailySummaries.isNotEmpty()) {
                    Text(
                        text = strings.dashboardSalesTrend,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Legend
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendDot(color = Color(0xFF388E3C), label = strings.dashboardRevenue)
                        LegendDot(color = Color(0xFFF57C00), label = strings.dashboardCost)
                    }

                    val chartLabels = state.dailySummaries.map {
                        it.date.takeLast(5) // "04/25" from "2026-04-25"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        SimpleDualLineChart(
                            labels = chartLabels,
                            series1 = state.dailySummaries.map { it.revenue },
                            series2 = state.dailySummaries.map { it.cost },
                            color1 = Color(0xFF388E3C),
                            color2 = Color(0xFFF57C00),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ══════════════════════════════════════
                    // DAILY PROFIT BAR CHART
                    // ══════════════════════════════════════

                    Text(
                        text = strings.dashboardDailyProfit,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        SimpleBarChart(
                            labels = chartLabels,
                            values = state.dailySummaries.map { it.revenue - it.cost },
                            barColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ══════════════════════════════════════════
                // TOP SELLING ITEMS
                // ══════════════════════════════════════════

                if (state.topSellingItems.isNotEmpty()) {
                    Text(
                        text = strings.dashboardTopSellers,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    state.topSellingItems.forEachIndexed { index, item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.itemName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${strings.dashboardSold}: ${item.totalSold}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = "₱${"%.2f".format(item.totalRevenue)}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF388E3C)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ══════════════════════════════════════════
                // INVENTORY HEALTH
                // ══════════════════════════════════════════

                Text(
                    text = strings.dashboardInventoryHealth,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Summary row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InventoryHealthChip(
                        label = strings.dashboardTotalProducts,
                        count = state.totalProducts,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    InventoryHealthChip(
                        label = strings.dashboardHealthy,
                        count = state.healthyStockCount,
                        color = Color(0xFF388E3C),
                        modifier = Modifier.weight(1f)
                    )
                    InventoryHealthChip(
                        label = strings.dashboardLowStock,
                        count = state.lowStockItems.size,
                        color = Color(0xFFF57C00),
                        modifier = Modifier.weight(1f)
                    )
                    InventoryHealthChip(
                        label = strings.dashboardOutOfStock,
                        count = state.outOfStockItems.size,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Low stock alerts
                if (state.lowStockItems.isNotEmpty()) {
                    Text(
                        text = strings.dashboardLowStockAlerts,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF57C00),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    state.lowStockItems.forEach { item ->
                        StockAlertRow(
                            name = item.name,
                            stock = item.currentStock,
                            color = Color(0xFFF57C00)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Out of stock alerts
                if (state.outOfStockItems.isNotEmpty()) {
                    Text(
                        text = strings.dashboardOutOfStockAlerts,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    state.outOfStockItems.forEach { item ->
                        StockAlertRow(
                            name = item.name,
                            stock = 0,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════
// SUB-COMPOSABLES
// ══════════════════════════════════════════════════════════

@Composable
private fun TimePeriodSelector(
    selected: TimePeriod,
    onSelect: (TimePeriod) -> Unit
) {
    val strings = LocalStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimePeriod.entries.forEach { period ->
            val label = when (period) {
                TimePeriod.TODAY -> strings.dashboardToday
                TimePeriod.WEEK -> strings.dashboardWeek
                TimePeriod.MONTH -> strings.dashboardMonth
            }
            Button(
                onClick = { onSelect(period) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == period)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (selected == period)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InventoryHealthChip(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(12.dp)
                .background(color, RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StockAlertRow(name: String, stock: Int, color: Color) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (stock <= 0) strings.dashboardOutOfStock else "$stock ${strings.dashboardRemaining}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
