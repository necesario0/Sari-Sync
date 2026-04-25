package com.sarisync.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sarisync.data.entity.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the inventory_items table.
 *
 * All read queries return Flow<List<T>>, which means the UI
 * (via Jetpack Compose) will automatically recompose whenever
 * the underlying data changes — no manual refresh needed.
 */
@Dao
interface InventoryDao {

    // ── CREATE ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem)

    // ── READ ────────────────────────────────────────────────

    /**
     * Returns all inventory items ordered alphabetically.
     * Emits a new list automatically whenever the table changes.
     */
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItem>>

    /**
     * Returns a single item by its ID (useful for detail/edit screens).
     */
    @Query("SELECT * FROM inventory_items WHERE id = :itemId")
    fun getItemById(itemId: Int): Flow<InventoryItem?>

    /**
     * Returns only items whose stock has fallen to zero or below.
     * Useful for the "Out of Stock" warning banner on the dashboard.
     */
    @Query("SELECT * FROM inventory_items WHERE currentStock <= 0 ORDER BY name ASC")
    fun getOutOfStockItems(): Flow<List<InventoryItem>>

    // ── UPDATE ──────────────────────────────────────────────

    @Update
    suspend fun update(item: InventoryItem)

    /**
     * Quick helper to decrement stock by 1 when a sale is logged.
     * Prevents stock from going below zero at the database level.
     */
    @Query("UPDATE inventory_items SET currentStock = MAX(currentStock - 1, 0) WHERE id = :itemId")
    suspend fun decrementStock(itemId: Int)

    /**
     * Restock an item by adding a specific quantity.
     */
    @Query("UPDATE inventory_items SET currentStock = currentStock + :quantity WHERE id = :itemId")
    suspend fun restockItem(itemId: Int, quantity: Int)

    // ── DELETE ───────────────────────────────────────────────

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("DELETE FROM inventory_items")
    suspend fun deleteAll()
}