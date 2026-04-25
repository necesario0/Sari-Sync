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

        // We need a coroutine scope because Room DAOs are suspend functions.
        // Dispatchers.IO ensures this runs on a background thread.
        CoroutineScope(Dispatchers.IO).launch {

            // Get DAO references from the singleton database instance.
            val database = SariSyncDatabase.INSTANCE
                ?: return@launch // Safety check — should never be null at this point

            val inventoryDao = database.inventoryDao()
            val creditDao = database.creditTransactionDao()

            // ════════════════════════════════════════════
            // 15 REALISTIC SARI-SARI STORE INVENTORY ITEMS
            // ════════════════════════════════════════════

            val items = listOf(
                // ── Beverages ───────────────────────────
                InventoryItem(name = "C2 Green Tea (500ml)", category = "Beverages", price = 25.00, currentStock = 24),
                InventoryItem(name = "Cobra Energy Drink", category = "Beverages", price = 15.00, currentStock = 18),
                InventoryItem(name = "Nescafe 3-in-1 (sachet)", category = "Beverages", price = 8.00, currentStock = 40),

                // ── Food / Canned Goods ─────────────────
                InventoryItem(name = "Lucky Me! Pancit Canton (Original)", category = "Food", price = 12.50, currentStock = 30),
                InventoryItem(name = "Century Tuna (155g)", category = "Food", price = 32.00, currentStock = 12),
                InventoryItem(name = "Argentina Corned Beef (100g)", category = "Food", price = 28.00, currentStock = 8),
                InventoryItem(name = "Skyflakes Crackers (pack)", category = "Food", price = 10.00, currentStock = 15),

                // ── Cooking Essentials ──────────────────
                InventoryItem(name = "Bigas - Rice (1 kilo)", category = "Cooking", price = 56.00, currentStock = 5),
                InventoryItem(name = "Magic Sarap (sachet)", category = "Cooking", price = 3.00, currentStock = 50),
                InventoryItem(name = "Silver Swan Soy Sauce (200ml)", category = "Cooking", price = 18.00, currentStock = 7),
                InventoryItem(name = "Cooking Oil - Baguio (250ml)", category = "Cooking", price = 35.00, currentStock = 3),

                // ── Household / Personal Care ───────────
                InventoryItem(name = "Safeguard Soap (regular)", category = "Household", price = 42.00, currentStock = 10),
                InventoryItem(name = "Surf Powder Detergent (sachet)", category = "Household", price = 9.50, currentStock = 22),
                InventoryItem(name = "Downy Fabric Conditioner (sachet)", category = "Household", price = 7.00, currentStock = 0),

                // ── Snacks / Miscellaneous ──────────────
                InventoryItem(name = "Yakult (1 bottle)", category = "Snacks", price = 11.00, currentStock = 0)
            )

            items.forEach { inventoryDao.insert(it) }

            // ════════════════════════════════════════════
            // 5 CREDIT ("UTANG") TRANSACTIONS
            // ════════════════════════════════════════════
            //
            // Positive amountOwed = new utang (customer took goods on credit)
            // Negative amountOwed = bayad (customer made a payment)
            //
            // These are designed to show different "Utang Health" states:
            //   Aling Nena  → multiple utang, one payment → net debt ~200 (Yellow)
            //   Mang Jun    → large single utang, no payment → net debt 350 (Red)
            //   Ate Maricel → small utang, fully paid → net debt 0 (Green)
            //   Kuya Rodel  → moderate utang → net debt 120 (Yellow)
            //   Tita Cora   → large utang → net debt 500 (Red)

            val transactions = listOf(
                // Aling Nena — bought rice and canned goods on credit
                CreditTransaction(
                    customerName = "Aling Nena",
                    amountOwed = 250.00,
                    date = "2026-04-20"
                ),
                // Aling Nena — partial payment
                CreditTransaction(
                    customerName = "Aling Nena",
                    amountOwed = -50.00,
                    date = "2026-04-22"
                ),

                // Mang Jun — bought cooking supplies, hasn't paid
                CreditTransaction(
                    customerName = "Mang Jun",
                    amountOwed = 350.00,
                    date = "2026-04-18"
                ),

                // Ate Maricel — bought soap, already paid in full
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

                // Kuya Rodel — bought noodles and drinks
                CreditTransaction(
                    customerName = "Kuya Rodel",
                    amountOwed = 120.00,
                    date = "2026-04-19"
                ),

                // Tita Cora — large utang from a birthday party supply run
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