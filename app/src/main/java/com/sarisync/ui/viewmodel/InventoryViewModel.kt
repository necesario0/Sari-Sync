package com.sarisync.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarisync.data.SariSyncDatabase
import com.sarisync.data.entity.InventoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val inventoryDao = SariSyncDatabase.getDatabase(application).inventoryDao()

    val uiState: StateFlow<InventoryUiState> = inventoryDao.getAllItems()
        .map { items -> InventoryUiState.Success(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InventoryUiState.Loading
        )

    fun addItem(name: String, category: String, price: Double, stock: Int) {
        viewModelScope.launch {
            inventoryDao.insert(
                InventoryItem(
                    name = name.trim(),
                    category = category.trim(),
                    price = price,
                    currentStock = stock
                )
            )
        }
    }

    fun sellOne(item: InventoryItem) {
        viewModelScope.launch {
            inventoryDao.decrementStock(item.id)
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