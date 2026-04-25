package com.sarisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Records every individual sale made in the store.
 *
 * Each time the owner taps "Sell" on an inventory item, a new
 * SalesRecord is created capturing the revenue (selling price),
 * the cost (purchase/cost price), and the date. This enables
 * the Dashboard to compute profit margins, revenue trends, and
 * sales trajectory over time.
 */
@Entity(tableName = "sales_records")
data class SalesRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** ID of the inventory item that was sold. */
    val itemId: Int,

    /** Name snapshot at time of sale (in case item is later deleted). */
    val itemName: String,

    /** Category snapshot at time of sale. */
    val category: String,

    /** Number of units sold in this transaction (usually 1). */
    val quantity: Int = 1,

    /** Selling price per unit in PHP. */
    val sellingPrice: Double,

    /** Cost / purchase price per unit in PHP. */
    val costPrice: Double,

    /** ISO-8601 date string, e.g. "2026-04-25". */
    val date: String
)
