package com.sarisync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarisync.data.SariSyncDatabase
import com.sarisync.data.entity.InventoryItem
import com.sarisync.data.entity.SalesRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SariSyncDatabase.getDatabase(application)
    private val inventoryDao = db.inventoryDao()
    private val salesDao = db.salesDao()

    val uiState: StateFlow<InventoryUiState> = inventoryDao.getAllItems()
        .map { items -> InventoryUiState.Success(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InventoryUiState.Loading
        )

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addItem(name: String, category: String, price: Double, stock: Int, costPrice: Double = 0.0) {
        viewModelScope.launch {
            inventoryDao.insert(
                InventoryItem(
                    name = name.trim(),
                    category = category.trim(),
                    price = price,
                    costPrice = costPrice,
                    currentStock = stock
                )
            )
        }
    }

    /**
     * Sells one unit of the given item:
     * 1. Decrements stock in the inventory table.
     * 2. Logs a SalesRecord for the Dashboard to track revenue & profit.
     */
    fun sellOne(item: InventoryItem) {
        viewModelScope.launch {
            inventoryDao.decrementStock(item.id)
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

sealed class InventoryUiState {
    object Loading : InventoryUiState()
    data class Success(val items: List<InventoryItem>) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}
