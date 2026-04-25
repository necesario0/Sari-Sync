package com.sarisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single credit ("utang") transaction for a customer.
 *
 * Each record logs who owes money, how much they owe for that
 * specific transaction, and the date it was recorded. Multiple
 * transactions can exist per customer — the DAO provides a query
 * to aggregate the total debt per customer name.
 */
@Entity(tableName = "credit_transactions")
data class CreditTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val customerName: String,

    // Amount owed for this transaction in PHP (e.g., 150.00)
    // Positive value = customer owes money (new utang)
    // Negative value = customer made a payment (bayad)
    val amountOwed: Double,

    // Stored as ISO-8601 string (e.g., "2026-04-25") for simplicity.
    // In a production app, consider using a TypeConverter for Date/Long.
    val date: String
)