package com.sarisync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sarisync.data.entity.CreditTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditTransactionDao {

    // ── CREATE ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: CreditTransaction)

    // ── READ ────────────────────────────────────────────────

    @Query("SELECT * FROM credit_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<CreditTransaction>>

    @Query("SELECT * FROM credit_transactions WHERE customerName = :name ORDER BY date DESC")
    fun getTransactionsByCustomer(name: String): Flow<List<CreditTransaction>>

    /**
     * Aggregates net debt per customer.
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

    /**
     * Returns the customer with the highest outstanding debt.
     * Used by the Dashboard "Top Debtor" card.
     */
    @Query(
        """
        SELECT customerName, SUM(amountOwed) AS totalDebt
        FROM credit_transactions
        GROUP BY customerName
        ORDER BY totalDebt DESC
        LIMIT 1
        """
    )
    fun getTopDebtor(): Flow<CustomerDebtSummary?>

    /**
     * Returns a list of distinct customer names for the payment dropdown.
     */
    @Query("SELECT DISTINCT customerName FROM credit_transactions ORDER BY customerName ASC")
    fun getDistinctCustomerNames(): Flow<List<String>>

    /**
     * Returns payer behavior stats for each customer:
     * - totalUtang: count of utang transactions (positive amounts)
     * - totalBayad: count of payment transactions (negative amounts)
     * - totalAmountOwed: sum of all positive utang amounts
     * - totalAmountPaid: absolute sum of all negative (payment) amounts
     * - netDebt: current outstanding balance
     *
     * Used to classify customers as "Good Payer", "Bad Payer", etc.
     */
    @Query(
        """
        SELECT 
            customerName,
            SUM(CASE WHEN amountOwed > 0 THEN 1 ELSE 0 END) AS totalUtangCount,
            SUM(CASE WHEN amountOwed < 0 THEN 1 ELSE 0 END) AS totalBayadCount,
            SUM(CASE WHEN amountOwed > 0 THEN amountOwed ELSE 0.0 END) AS totalAmountOwed,
            ABS(SUM(CASE WHEN amountOwed < 0 THEN amountOwed ELSE 0.0 END)) AS totalAmountPaid,
            SUM(amountOwed) AS netDebt
        FROM credit_transactions
        GROUP BY customerName
        ORDER BY netDebt DESC
        """
    )
    fun getPayerBehavior(): Flow<List<PayerBehaviorSummary>>

    // ── UPDATE ──────────────────────────────────────────────
    @Update
    suspend fun update(transaction: CreditTransaction)

    // ── DELETE ───────────────────────────────────────────────
    @Delete
    suspend fun delete(transaction: CreditTransaction)

    @Query("DELETE FROM credit_transactions WHERE customerName = :name")
    suspend fun deleteAllForCustomer(name: String)

    @Query("DELETE FROM credit_transactions")
    suspend fun deleteAll()
}

/**
 * Read-only projection for the aggregation query.
 */
data class CustomerDebtSummary(
    val customerName: String,
    val totalDebt: Double
)

/**
 * Read-only projection for payer behavior analysis.
 *
 * paymentRatio = totalAmountPaid / totalAmountOwed
 *   - >= 0.8  → "Mabuting Nagbabayad" (Good Payer)
 *   - >= 0.4  → "Katamtaman" (Average Payer)
 *   - < 0.4   → "Masamang Nagbabayad" (Bad Payer)
 *   - netDebt <= 0 → "Bayad Na Lahat" (Fully Paid)
 */
data class PayerBehaviorSummary(
    val customerName: String,
    val totalUtangCount: Int,
    val totalBayadCount: Int,
    val totalAmountOwed: Double,
    val totalAmountPaid: Double,
    val netDebt: Double
)
