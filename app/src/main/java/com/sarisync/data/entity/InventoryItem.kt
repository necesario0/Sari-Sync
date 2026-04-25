package com.sarisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single product in the sari-sari store's inventory.
 *
 * Each item tracks the product name, its selling price per unit,
 * the cost/purchase price per unit (for profit calculations),
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

    // Cost / purchase price per unit in PHP (e.g., 8.00)
    // Used for profit margin calculations on the Dashboard.
    // Defaults to 0.0 for backward compatibility with existing data.
    val costPrice: Double = 0.0,

    // Current number of units in stock
    val currentStock: Int
)
