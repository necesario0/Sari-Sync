package com.sarisync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarisync.data.SariSyncDatabase
import com.sarisync.data.dao.DailySalesSummary
import com.sarisync.data.dao.TopSellingItem
import com.sarisync.data.entity.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ── Time period options ─────────────────────────────────

enum class TimePeriod { TODAY, WEEK, MONTH }

// ── UI State ────────────────────────────────────────────

data class DashboardUiState(
    val isLoading: Boolean = true,

    // Key metrics
    val totalRevenue: Double = 0.0,
    val totalCost: Double = 0.0,
    val netProfit: Double = 0.0,
    val profitMarginPercent: Double = 0.0,
    val totalUnitsSold: Int = 0,

    // Chart data
    val dailySummaries: List<DailySalesSummary> = emptyList(),

    // Top sellers
    val topSellingItems: List<TopSellingItem> = emptyList(),

    // Inventory health
    val totalProducts: Int = 0,
    val outOfStockItems: List<InventoryItem> = emptyList(),
    val lowStockItems: List<InventoryItem> = emptyList(),
    val healthyStockCount: Int = 0,

    // Outstanding credit
    val totalOutstandingCredit: Double = 0.0,

    // Selected period
    val selectedPeriod: TimePeriod = TimePeriod.WEEK
)

// ── ViewModel ───────────────────────────────────────────

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SariSyncDatabase.getDatabase(application)
    private val salesDao = db.salesDao()
    private val inventoryDao = db.inventoryDao()
    private val creditDao = db.creditTransactionDao()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /** The currently selected time period, drives all queries. */
    val selectedPeriod = MutableStateFlow(TimePeriod.WEEK)

    /**
     * Combined UI state that reacts to period changes and database updates.
     */
    val uiState: StateFlow<DashboardUiState> = selectedPeriod.flatMapLatest { period ->
        val startDate = getStartDate(period)

        // Combine all the reactive flows into one DashboardUiState
        combine(
            salesDao.totalRevenueSince(startDate),
            salesDao.totalCostSince(startDate),
            salesDao.totalUnitsSoldSince(startDate),
            salesDao.dailySummariesSince(startDate),
            salesDao.topSellingItemsSince(startDate)
        ) { revenue, cost, unitsSold, dailySummaries, topItems ->
            val profit = revenue - cost
            val margin = if (revenue > 0) (profit / revenue) * 100.0 else 0.0

            DashboardUiState(
                isLoading = false,
                totalRevenue = revenue,
                totalCost = cost,
                netProfit = profit,
                profitMarginPercent = margin,
                totalUnitsSold = unitsSold,
                dailySummaries = dailySummaries,
                topSellingItems = topItems,
                selectedPeriod = period
            )
        }.combine(inventoryDao.getAllItems()) { state, items ->
            val outOfStock = items.filter { it.currentStock <= 0 }
            val lowStock = items.filter { it.currentStock in 1..10 }
            val healthy = items.count { it.currentStock > 10 }

            state.copy(
                totalProducts = items.size,
                outOfStockItems = outOfStock,
                lowStockItems = lowStock,
                healthyStockCount = healthy
            )
        }.combine(creditDao.getDebtPerCustomer()) { state, debts ->
            val totalCredit = debts.sumOf { maxOf(it.totalDebt, 0.0) }
            state.copy(totalOutstandingCredit = totalCredit)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun setPeriod(period: TimePeriod) {
        selectedPeriod.value = period
    }

    // ── Helpers ─────────────────────────────────────────

    private fun getStartDate(period: TimePeriod): String {
        val cal = Calendar.getInstance()
        when (period) {
            TimePeriod.TODAY -> { /* already today */ }
            TimePeriod.WEEK -> cal.add(Calendar.DAY_OF_YEAR, -7)
            TimePeriod.MONTH -> cal.add(Calendar.DAY_OF_YEAR, -30)
        }
        return sdf.format(cal.time)
    }
}
