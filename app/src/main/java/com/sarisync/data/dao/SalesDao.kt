package com.sarisync.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sarisync.data.entity.SalesRecord
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the sales_records table.
 *
 * Provides queries for the Dashboard: daily/weekly/monthly
 * revenue, cost, profit, and per-day aggregations for charts.
 */
@Dao
interface SalesDao {

    // ── CREATE ──────────────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: SalesRecord)

    // ── READ — Raw ─────────────────────────────────────────
    @Query("SELECT * FROM sales_records ORDER BY date DESC")
    fun getAllSales(): Flow<List<SalesRecord>>

    @Query("SELECT * FROM sales_records WHERE date >= :startDate ORDER BY date ASC")
    fun getSalesSince(startDate: String): Flow<List<SalesRecord>>

    // ── READ — Aggregated totals ───────────────────────────

    /** Total revenue (sum of sellingPrice * quantity) since [startDate]. */
    @Query("SELECT COALESCE(SUM(sellingPrice * quantity), 0.0) FROM sales_records WHERE date >= :startDate")
    fun totalRevenueSince(startDate: String): Flow<Double>

    /** Total cost (sum of costPrice * quantity) since [startDate]. */
    @Query("SELECT COALESCE(SUM(costPrice * quantity), 0.0) FROM sales_records WHERE date >= :startDate")
    fun totalCostSince(startDate: String): Flow<Double>

    /** Total number of items sold since [startDate]. */
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM sales_records WHERE date >= :startDate")
    fun totalUnitsSoldSince(startDate: String): Flow<Int>

    // ── READ — Per-day aggregation (for charts) ────────────

    /**
     * Returns revenue and cost grouped by date for chart rendering.
     * Each row is a [DailySalesSummary].
     */
    @Query(
        """
        SELECT date,
               SUM(sellingPrice * quantity) AS revenue,
               SUM(costPrice * quantity) AS cost
        FROM sales_records
        WHERE date >= :startDate
        GROUP BY date
        ORDER BY date ASC
        """
    )
    fun dailySummariesSince(startDate: String): Flow<List<DailySalesSummary>>

    // ── READ — Top selling items ───────────────────────────
    @Query(
        """
        SELECT itemName,
               SUM(quantity) AS totalSold,
               SUM(sellingPrice * quantity) AS totalRevenue
        FROM sales_records
        WHERE date >= :startDate
        GROUP BY itemName
        ORDER BY totalSold DESC
        LIMIT 5
        """
    )
    fun topSellingItemsSince(startDate: String): Flow<List<TopSellingItem>>

    // ── READ — Average daily sales per item (for stock prediction) ──
    /**
     * Returns the average daily quantity sold per item since [startDate].
     * Used by InventoryViewModel to predict when stock will run out.
     */
    @Query(
        """
        SELECT itemName,
               CAST(SUM(quantity) AS REAL) / MAX(1, JULIANDAY('now') - JULIANDAY(:startDate)) AS avgDailySales
        FROM sales_records
        WHERE date >= :startDate
        GROUP BY itemName
        """
    )
    fun avgDailySalesSince(startDate: String): Flow<List<ItemAvgDailySales>>

    // ── DELETE ───────────────────────────────────────────────
    @Query("DELETE FROM sales_records")
    suspend fun deleteAll()
}

// ── Projection classes ──────────────────────────────────────

/** One row per day for chart data. */
data class DailySalesSummary(
    val date: String,
    val revenue: Double,
    val cost: Double
)

/** Top-selling item projection. */
data class TopSellingItem(
    val itemName: String,
    val totalSold: Int,
    val totalRevenue: Double
)

/** Average daily sales per item — used for stock depletion prediction. */
data class ItemAvgDailySales(
    val itemName: String,
    val avgDailySales: Double
)
