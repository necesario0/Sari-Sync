package com.sarisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single product in the sari-sari store's inventory.
 *
 * Each item tracks the product name, its selling price per unit,
 * and the current stock count. The stock is decremented on each sale
 * and can be restocked manually by the store owner.
 */
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    // Category for organization (e.g., "Beverages", "Food", "Household")
    val category: String = "Other",

    // Selling price per unit in PHP (e.g., 12.50)
    val price: Double,

    // Current number of units in stock
    val currentStock: Int
)