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
     * Returns payer behavior based on payment delays (time-based):
     * - averagePaymentDelayDays: average days between utang and payment
     * - daysSinceLastUnpaid: days since the most recent unpaid utang
     * - netDebt: current outstanding balance
     *
     * Classification:
     * - Good Payer: pays within 7 days on average
     * - Average Payer: pays within 30 days on average
     * - Bad Payer: unpaid for 30+ days or no payment history
     */
    @Query(
        """
        SELECT 
            customerName,
            CAST(AVG(CASE WHEN paymentDelay >= 0 THEN paymentDelay ELSE NULL END) AS INTEGER) AS averagePaymentDelayDays,
            CAST(MAX(CASE WHEN amountOwed > 0 THEN CAST((julianday('now') - julianday(date)) AS INTEGER) ELSE NULL END) AS INTEGER) AS daysSinceLastUnpaid,
            SUM(amountOwed) AS netDebt
        FROM (
            SELECT 
                ct1.customerName,
                ct1.date,
                ct1.amountOwed,
                CAST((julianday(COALESCE(MIN(CASE WHEN ct2.amountOwed < 0 AND ct2.date >= ct1.date THEN ct2.date END), 'now')) - julianday(ct1.date)) AS INTEGER) AS paymentDelay
            FROM credit_transactions ct1
            LEFT JOIN credit_transactions ct2 ON ct1.customerName = ct2.customerName
            WHERE ct1.amountOwed > 0
            GROUP BY ct1.id, ct1.customerName, ct1.date, ct1.amountOwed
        )
        GROUP BY customerName
        ORDER BY daysSinceLastUnpaid DESC
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
 * Read-only projection for time-based payer behavior analysis.
 *
 * averagePaymentDelayDays: average number of days between utang and payment
 * daysSinceLastUnpaid: days since the most recent unpaid utang
 * netDebt: current outstanding balance
 *
 * Classification (in UtangViewModel):
 * - Good Payer: averagePaymentDelayDays <= 7 days
 * - Average Payer: averagePaymentDelayDays <= 30 days
 * - Bad Payer: averagePaymentDelayDays > 30 days OR daysSinceLastUnpaid > 30 days
 * - Fully Paid: netDebt <= 0
 */
data class PayerBehaviorSummary(
    val customerName: String,
    val averagePaymentDelayDays: Int?,
    val daysSinceLastUnpaid: Int?,
    val netDebt: Double
)
