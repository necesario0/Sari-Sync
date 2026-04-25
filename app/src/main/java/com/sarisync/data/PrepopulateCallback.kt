package com.sarisync.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarisync.data.entity.CreditTransaction
import com.sarisync.data.entity.InventoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Pre-populates the Sari-Sync database the very first time it is created.
 *
 * This callback runs ONCE — only when the database file does not yet exist
 * on the device. If the user has already opened the app before, this will
 * NOT overwrite their data.
 *
 * All items and prices are based on real Philippine sari-sari store products
 * as of 2026. Stock levels are set to create a realistic mix of healthy,
 * low-stock, and out-of-stock items for a compelling demo.
 */
class PrepopulateCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {

            val database = SariSyncDatabase.INSTANCE
                ?: return@launch

            val inventoryDao = database.inventoryDao()
            val creditDao = database.creditTransactionDao()

            // ════════════════════════════════════════════
            // 15 REALISTIC SARI-SARI STORE INVENTORY ITEMS
            // ════════════════════════════════════════════

            val items = listOf(
                // ── Inumin ─────────────────────────────
                InventoryItem(name = "C2 Green Tea (500ml)", category = "Inumin", price = 25.00, currentStock = 24),
                InventoryItem(name = "Cobra Energy Drink", category = "Inumin", price = 15.00, currentStock = 18),
                InventoryItem(name = "Nescafe 3-in-1 (sachet)", category = "Inumin", price = 8.00, currentStock = 40),

                // ── Pagkain ────────────────────────────
                InventoryItem(name = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", price = 12.50, currentStock = 30),
                InventoryItem(name = "Century Tuna (155g)", category = "Pagkain", price = 32.00, currentStock = 12),
                InventoryItem(name = "Argentina Corned Beef (100g)", category = "Pagkain", price = 28.00, currentStock = 8),
                InventoryItem(name = "Skyflakes Crackers (pack)", category = "Pagkain", price = 10.00, currentStock = 15),

                // ── Pangluto ───────────────────────────
                InventoryItem(name = "Bigas - Rice (1 kilo)", category = "Pangluto", price = 56.00, currentStock = 5),
                InventoryItem(name = "Magic Sarap (sachet)", category = "Pangluto", price = 3.00, currentStock = 50),
                InventoryItem(name = "Silver Swan Soy Sauce (200ml)", category = "Pangluto", price = 18.00, currentStock = 7),
                InventoryItem(name = "Cooking Oil - Baguio (250ml)", category = "Pangluto", price = 35.00, currentStock = 3),

                // ── Gamit sa Bahay ─────────────────────
                InventoryItem(name = "Safeguard Soap (regular)", category = "Gamit sa Bahay", price = 42.00, currentStock = 10),
                InventoryItem(name = "Surf Powder Detergent (sachet)", category = "Gamit sa Bahay", price = 9.50, currentStock = 22),
                InventoryItem(name = "Downy Fabric Conditioner (sachet)", category = "Gamit sa Bahay", price = 7.00, currentStock = 0),

                // ── Meryenda ───────────────────────────
                InventoryItem(name = "Yakult (1 bottle)", category = "Meryenda", price = 11.00, currentStock = 0)
            )

            items.forEach { inventoryDao.insert(it) }

            // ════════════════════════════════════════════
            // 5 CREDIT ("UTANG") TRANSACTIONS
            // ════════════════════════════════════════════

            val transactions = listOf(
                // Aling Nena — umutang ng bigas at de-lata
                CreditTransaction(
                    customerName = "Aling Nena",
                    amountOwed = 250.00,
                    date = "2026-04-20"
                ),
                // Aling Nena — partial na bayad
                CreditTransaction(
                    customerName = "Aling Nena",
                    amountOwed = -50.00,
                    date = "2026-04-22"
                ),

                // Mang Jun — umutang ng pangluto, hindi pa nagbabayad
                CreditTransaction(
                    customerName = "Mang Jun",
                    amountOwed = 350.00,
                    date = "2026-04-18"
                ),

                // Ate Maricel — umutang ng sabon, bayad na lahat
                CreditTransaction(
                    customerName = "Ate Maricel",
                    amountOwed = 84.00,
                    date = "2026-04-21"
                ),
                CreditTransaction(
                    customerName = "Ate Maricel",
                    amountOwed = -84.00,
                    date = "2026-04-23"
                ),

                // Kuya Rodel — umutang ng noodles at inumin
                CreditTransaction(
                    customerName = "Kuya Rodel",
                    amountOwed = 120.00,
                    date = "2026-04-19"
                ),

                // Tita Cora — malaking utang para sa birthday party
                CreditTransaction(
                    customerName = "Tita Cora",
                    amountOwed = 500.00,
                    date = "2026-04-17"
                )
            )

            transactions.forEach { creditDao.insert(it) }
        }
    }
}