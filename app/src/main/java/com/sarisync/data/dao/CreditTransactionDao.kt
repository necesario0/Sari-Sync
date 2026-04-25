package com.sarisync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sarisync.data.entity.CreditTransaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the credit_transactions table.
 *
 * Designed around the real workflow of a sari-sari store:
 * - Store owner adds a new "utang" (positive amountOwed)
 * - Customer pays back (negative amountOwed logged as a payment)
 * - Owner can view total debt per customer at a glance
 */
@Dao
interface CreditTransactionDao {

    // ── CREATE ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: CreditTransaction)

    // ── READ ────────────────────────────────────────────────

    /**
     * Returns every credit transaction, newest first.
     * This is the raw transaction log / history view.
     */
    @Query("SELECT * FROM credit_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<CreditTransaction>>

    /**
     * Returns all transactions for a specific customer.
     * Useful for the "View Details" screen per customer.
     */
    @Query("SELECT * FROM credit_transactions WHERE customerName = :name ORDER BY date DESC")
    fun getTransactionsByCustomer(name: String): Flow<List<CreditTransaction>>

    /**
     * THE KEY QUERY FOR THE UTANG LEDGER SCREEN.
     *
     * Aggregates the net debt per customer by summing all their
     * transactions (positive = new utang, negative = payment).
     * Returns a list of [CustomerDebtSummary] for the main ledger view.
     */
    @Query(
        """
        SELECT customerName, SUM(amountOwed) AS totalDebt
        FROM credit_transactions
        GROUP BY customerName
        ORDER BY totalDebt DESC
        """
    )
    fun getDebtPerCustomer(): Flow<List<CustomerDebtSummary>>

    // ── UPDATE ──────────────────────────────────────────────

    @Update
    suspend fun update(transaction: CreditTransaction)

    // ── DELETE ───────────────────────────────────────────────

    @Delete
    suspend fun delete(transaction: CreditTransaction)

    /**
     * Clears all transactions for a specific customer
     * (e.g., when they fully settle their debt and the owner
     * wants a clean slate).
     */
    @Query("DELETE FROM credit_transactions WHERE customerName = :name")
    suspend fun deleteAllForCustomer(name: String)

    @Query("DELETE FROM credit_transactions")
    suspend fun deleteAll()
}

/**
 * A simple data class used by the aggregation query [getDebtPerCustomer].
 * Room maps the query columns directly to these fields.
 *
 * This is NOT an @Entity — it is a read-only projection.
 */
data class CustomerDebtSummary(
    val customerName: String,
    val totalDebt: Double
)