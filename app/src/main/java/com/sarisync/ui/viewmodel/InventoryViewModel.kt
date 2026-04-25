package com.sarisync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarisync.data.SariSyncDatabase
import com.sarisync.data.dao.ItemAvgDailySales
import com.sarisync.data.entity.CreditTransaction
import com.sarisync.data.entity.InventoryItem
import com.sarisync.data.entity.SalesRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SariSyncDatabase.getDatabase(application)
    private val inventoryDao = db.inventoryDao()
    private val salesDao = db.salesDao()
    private val creditDao = db.creditTransactionDao()

    // ── Helper: today's date ────────────────────────────────
    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // ── Helper: date N days ago ─────────────────────────────
    private fun getDateDaysAgo(days: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return sdf.format(cal.time)
    }

    // ── UI STATE: Items + Stock Predictions ─────────────────

    /**
     * Combines the inventory list with average daily sales data
     * to produce stock depletion predictions for each item.
     */
    val uiState: StateFlow<InventoryUiState> = combine(
        inventoryDao.getAllItems(),
        salesDao.avgDailySalesSince(getDateDaysAgo(7))
    ) { items: List<InventoryItem>, avgSales: List<ItemAvgDailySales> ->

        // Build a lookup map: itemName -> avgDailySales
        val salesMap: Map<String, Double> = avgSales.associate {
            it.itemName to it.avgDailySales
        }

        // Calculate predictions for each item
        val predictions: Map<Int, StockPrediction> = items.associate { item: InventoryItem ->
            val avg: Double = salesMap[item.name] ?: 0.0
            val prediction: StockPrediction = if (avg > 0 && item.currentStock > 0) {
                val daysLeft: Double = item.currentStock.toDouble() / avg
                when {
                    daysLeft <= 3.0 -> StockPrediction.Urgent(daysLeft.toInt().coerceAtLeast(1))
                    daysLeft <= 7.0 -> StockPrediction.Warning(daysLeft.toInt())
                    else -> StockPrediction.Safe
                }
            } else if (item.currentStock <= 0) {
                StockPrediction.OutOfStock
            } else {
                StockPrediction.NoData
            }
            item.id to prediction
        }

        InventoryUiState.Success(items = items, predictions = predictions)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InventoryUiState.Loading
    )

    // ── All item names (for duplicate detection) ────────────

    val allItemNames: StateFlow<List<String>> = inventoryDao.getAllItems()
        .map { items: List<InventoryItem> -> items.map { it.name.uppercase() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ── ACTIONS ─────────────────────────────────────────────

    fun addItem(name: String, category: String, price: Double, stock: Int, costPrice: Double = 0.0) {
        viewModelScope.launch {
            inventoryDao.insert(
                InventoryItem(
                    name = name.trim().uppercase(),
                    category = category.trim(),
                    price = price,
                    costPrice = costPrice,
                    currentStock = stock
                )
            )
        }
    }

    /**
     * Sells one unit of the given item.
     * @param item The inventory item being sold.
     * @param isCash If true, it's a cash sale. If false, it's utang (credit).
     * @param customerName Required when isCash is false.
     */
    fun sellOne(item: InventoryItem, isCash: Boolean = true, customerName: String? = null) {
        viewModelScope.launch {
            // 1. Decrement stock
            inventoryDao.decrementStock(item.id)
            // 2. Log the sale for dashboard analytics
            salesDao.insert(
                SalesRecord(
                    itemId = item.id,
                    itemName = item.name,
                    category = item.category,
                    quantity = 1,
                    sellingPrice = item.price,
                    costPrice = item.costPrice,
                    date = getTodayDate()
                )
            )
            // 3. If utang, create a credit transaction
            if (!isCash && !customerName.isNullOrBlank()) {
                creditDao.insert(
                    CreditTransaction(
                        customerName = customerName.trim().uppercase(),
                        amountOwed = item.price,
                        date = getTodayDate()
                    )
                )
            }
        }
    }

    fun restockItem(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            inventoryDao.restockItem(itemId, quantity)
        }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            inventoryDao.delete(item)
        }
    }
}

// ── UI State ────────────────────────────────────────────────

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(
        val items: List<InventoryItem>,
        val predictions: Map<Int, StockPrediction> = emptyMap()
    ) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}

// ── Stock Prediction Model ──────────────────────────────────

sealed class StockPrediction {
    /** Will run out in 1-3 days — urgent! */
    data class Urgent(val daysLeft: Int) : StockPrediction()
    /** Will run out in 4-7 days — warning */
    data class Warning(val daysLeft: Int) : StockPrediction()
    /** More than 7 days of stock — safe */
    object Safe : StockPrediction()
    /** Already out of stock */
    object OutOfStock : StockPrediction()
    /** No sales history — can't predict */
    object NoData : StockPrediction()
}
