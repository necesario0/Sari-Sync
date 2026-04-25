package com.sarisync.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarisync.data.entity.CreditTransaction
import com.sarisync.data.entity.InventoryItem
import com.sarisync.data.entity.SalesRecord
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
            val salesDao = database.salesDao()

            // ════════════════════════════════════════════
            // 15 REALISTIC SARI-SARI STORE INVENTORY ITEMS
            // (now with costPrice for profit calculations)
            // ════════════════════════════════════════════

            val items = listOf(
                // ── Inumin ─────────────────────────────
                InventoryItem(name = "C2 Green Tea (500ml)", category = "Inumin", price = 25.00, costPrice = 18.00, currentStock = 24),
                InventoryItem(name = "Cobra Energy Drink", category = "Inumin", price = 15.00, costPrice = 10.00, currentStock = 18),
                InventoryItem(name = "Nescafe 3-in-1 (sachet)", category = "Inumin", price = 8.00, costPrice = 5.50, currentStock = 40),

                // ── Pagkain ────────────────────────────
                InventoryItem(name = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", price = 12.50, costPrice = 9.00, currentStock = 30),
                InventoryItem(name = "Century Tuna (155g)", category = "Pagkain", price = 32.00, costPrice = 24.00, currentStock = 12),
                InventoryItem(name = "Argentina Corned Beef (100g)", category = "Pagkain", price = 28.00, costPrice = 21.00, currentStock = 8),
                InventoryItem(name = "Skyflakes Crackers (pack)", category = "Pagkain", price = 10.00, costPrice = 7.00, currentStock = 15),

                // ── Pangluto ───────────────────────────
                InventoryItem(name = "Bigas - Rice (1 kilo)", category = "Pangluto", price = 56.00, costPrice = 45.00, currentStock = 5),
                InventoryItem(name = "Magic Sarap (sachet)", category = "Pangluto", price = 3.00, costPrice = 2.00, currentStock = 50),
                InventoryItem(name = "Silver Swan Soy Sauce (200ml)", category = "Pangluto", price = 18.00, costPrice = 13.00, currentStock = 7),
                InventoryItem(name = "Cooking Oil - Baguio (250ml)", category = "Pangluto", price = 35.00, costPrice = 27.00, currentStock = 3),

                // ── Gamit sa Bahay ─────────────────────
                InventoryItem(name = "Safeguard Soap (regular)", category = "Gamit sa Bahay", price = 42.00, costPrice = 32.00, currentStock = 10),
                InventoryItem(name = "Surf Powder Detergent (sachet)", category = "Gamit sa Bahay", price = 9.50, costPrice = 6.50, currentStock = 22),
                InventoryItem(name = "Downy Fabric Conditioner (sachet)", category = "Gamit sa Bahay", price = 7.00, costPrice = 4.50, currentStock = 0),

                // ── Meryenda ───────────────────────────
                InventoryItem(name = "Yakult (1 bottle)", category = "Meryenda", price = 11.00, costPrice = 8.00, currentStock = 0)
            )

            items.forEach { inventoryDao.insert(it) }

            // ════════════════════════════════════════════
            // 5 CREDIT ("UTANG") TRANSACTIONS
            // ════════════════════════════════════════════

            val transactions = listOf(
                CreditTransaction(customerName = "Aling Nena", amountOwed = 250.00, date = "2026-04-20"),
                CreditTransaction(customerName = "Aling Nena", amountOwed = -50.00, date = "2026-04-22"),
                CreditTransaction(customerName = "Mang Jun", amountOwed = 350.00, date = "2026-04-18"),
                CreditTransaction(customerName = "Ate Maricel", amountOwed = 84.00, date = "2026-04-21"),
                CreditTransaction(customerName = "Ate Maricel", amountOwed = -84.00, date = "2026-04-23"),
                CreditTransaction(customerName = "Kuya Rodel", amountOwed = 120.00, date = "2026-04-19"),
                CreditTransaction(customerName = "Tita Cora", amountOwed = 500.00, date = "2026-04-17")
            )

            transactions.forEach { creditDao.insert(it) }

            // ════════════════════════════════════════════
            // SEED SALES RECORDS (for Dashboard demo data)
            // Simulates ~2 weeks of realistic daily sales
            // ════════════════════════════════════════════

            val salesRecords = listOf(
                // April 12
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 3, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-12"),
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 5, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-12"),
                SalesRecord(itemId = 9, itemName = "Magic Sarap (sachet)", category = "Pangluto", quantity = 8, sellingPrice = 3.00, costPrice = 2.00, date = "2026-04-12"),

                // April 13
                SalesRecord(itemId = 2, itemName = "Cobra Energy Drink", category = "Inumin", quantity = 4, sellingPrice = 15.00, costPrice = 10.00, date = "2026-04-13"),
                SalesRecord(itemId = 3, itemName = "Nescafe 3-in-1 (sachet)", category = "Inumin", quantity = 6, sellingPrice = 8.00, costPrice = 5.50, date = "2026-04-13"),
                SalesRecord(itemId = 12, itemName = "Safeguard Soap (regular)", category = "Gamit sa Bahay", quantity = 2, sellingPrice = 42.00, costPrice = 32.00, date = "2026-04-13"),

                // April 14
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 2, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-14"),
                SalesRecord(itemId = 5, itemName = "Century Tuna (155g)", category = "Pagkain", quantity = 3, sellingPrice = 32.00, costPrice = 24.00, date = "2026-04-14"),
                SalesRecord(itemId = 8, itemName = "Bigas - Rice (1 kilo)", category = "Pangluto", quantity = 2, sellingPrice = 56.00, costPrice = 45.00, date = "2026-04-14"),

                // April 15
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 7, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-15"),
                SalesRecord(itemId = 13, itemName = "Surf Powder Detergent (sachet)", category = "Gamit sa Bahay", quantity = 4, sellingPrice = 9.50, costPrice = 6.50, date = "2026-04-15"),
                SalesRecord(itemId = 9, itemName = "Magic Sarap (sachet)", category = "Pangluto", quantity = 10, sellingPrice = 3.00, costPrice = 2.00, date = "2026-04-15"),

                // April 16
                SalesRecord(itemId = 6, itemName = "Argentina Corned Beef (100g)", category = "Pagkain", quantity = 2, sellingPrice = 28.00, costPrice = 21.00, date = "2026-04-16"),
                SalesRecord(itemId = 10, itemName = "Silver Swan Soy Sauce (200ml)", category = "Pangluto", quantity = 3, sellingPrice = 18.00, costPrice = 13.00, date = "2026-04-16"),
                SalesRecord(itemId = 2, itemName = "Cobra Energy Drink", category = "Inumin", quantity = 5, sellingPrice = 15.00, costPrice = 10.00, date = "2026-04-16"),

                // April 17
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 4, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-17"),
                SalesRecord(itemId = 7, itemName = "Skyflakes Crackers (pack)", category = "Pagkain", quantity = 6, sellingPrice = 10.00, costPrice = 7.00, date = "2026-04-17"),
                SalesRecord(itemId = 3, itemName = "Nescafe 3-in-1 (sachet)", category = "Inumin", quantity = 5, sellingPrice = 8.00, costPrice = 5.50, date = "2026-04-17"),

                // April 18
                SalesRecord(itemId = 11, itemName = "Cooking Oil - Baguio (250ml)", category = "Pangluto", quantity = 2, sellingPrice = 35.00, costPrice = 27.00, date = "2026-04-18"),
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 4, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-18"),
                SalesRecord(itemId = 12, itemName = "Safeguard Soap (regular)", category = "Gamit sa Bahay", quantity = 1, sellingPrice = 42.00, costPrice = 32.00, date = "2026-04-18"),

                // April 19
                SalesRecord(itemId = 5, itemName = "Century Tuna (155g)", category = "Pagkain", quantity = 2, sellingPrice = 32.00, costPrice = 24.00, date = "2026-04-19"),
                SalesRecord(itemId = 8, itemName = "Bigas - Rice (1 kilo)", category = "Pangluto", quantity = 3, sellingPrice = 56.00, costPrice = 45.00, date = "2026-04-19"),
                SalesRecord(itemId = 9, itemName = "Magic Sarap (sachet)", category = "Pangluto", quantity = 12, sellingPrice = 3.00, costPrice = 2.00, date = "2026-04-19"),

                // April 20
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 5, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-20"),
                SalesRecord(itemId = 6, itemName = "Argentina Corned Beef (100g)", category = "Pagkain", quantity = 3, sellingPrice = 28.00, costPrice = 21.00, date = "2026-04-20"),
                SalesRecord(itemId = 13, itemName = "Surf Powder Detergent (sachet)", category = "Gamit sa Bahay", quantity = 3, sellingPrice = 9.50, costPrice = 6.50, date = "2026-04-20"),

                // April 21
                SalesRecord(itemId = 2, itemName = "Cobra Energy Drink", category = "Inumin", quantity = 3, sellingPrice = 15.00, costPrice = 10.00, date = "2026-04-21"),
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 6, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-21"),
                SalesRecord(itemId = 10, itemName = "Silver Swan Soy Sauce (200ml)", category = "Pangluto", quantity = 2, sellingPrice = 18.00, costPrice = 13.00, date = "2026-04-21"),

                // April 22
                SalesRecord(itemId = 3, itemName = "Nescafe 3-in-1 (sachet)", category = "Inumin", quantity = 8, sellingPrice = 8.00, costPrice = 5.50, date = "2026-04-22"),
                SalesRecord(itemId = 7, itemName = "Skyflakes Crackers (pack)", category = "Pagkain", quantity = 3, sellingPrice = 10.00, costPrice = 7.00, date = "2026-04-22"),
                SalesRecord(itemId = 5, itemName = "Century Tuna (155g)", category = "Pagkain", quantity = 4, sellingPrice = 32.00, costPrice = 24.00, date = "2026-04-22"),

                // April 23
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 3, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-23"),
                SalesRecord(itemId = 8, itemName = "Bigas - Rice (1 kilo)", category = "Pangluto", quantity = 2, sellingPrice = 56.00, costPrice = 45.00, date = "2026-04-23"),
                SalesRecord(itemId = 9, itemName = "Magic Sarap (sachet)", category = "Pangluto", quantity = 7, sellingPrice = 3.00, costPrice = 2.00, date = "2026-04-23"),

                // April 24
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 8, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-24"),
                SalesRecord(itemId = 2, itemName = "Cobra Energy Drink", category = "Inumin", quantity = 6, sellingPrice = 15.00, costPrice = 10.00, date = "2026-04-24"),
                SalesRecord(itemId = 12, itemName = "Safeguard Soap (regular)", category = "Gamit sa Bahay", quantity = 3, sellingPrice = 42.00, costPrice = 32.00, date = "2026-04-24"),

                // April 25
                SalesRecord(itemId = 5, itemName = "Century Tuna (155g)", category = "Pagkain", quantity = 2, sellingPrice = 32.00, costPrice = 24.00, date = "2026-04-25"),
                SalesRecord(itemId = 11, itemName = "Cooking Oil - Baguio (250ml)", category = "Pangluto", quantity = 1, sellingPrice = 35.00, costPrice = 27.00, date = "2026-04-25"),
                SalesRecord(itemId = 3, itemName = "Nescafe 3-in-1 (sachet)", category = "Inumin", quantity = 4, sellingPrice = 8.00, costPrice = 5.50, date = "2026-04-25"),
                SalesRecord(itemId = 6, itemName = "Argentina Corned Beef (100g)", category = "Pagkain", quantity = 2, sellingPrice = 28.00, costPrice = 21.00, date = "2026-04-25"),

                // April 26 (today)
                SalesRecord(itemId = 1, itemName = "C2 Green Tea (500ml)", category = "Inumin", quantity = 2, sellingPrice = 25.00, costPrice = 18.00, date = "2026-04-26"),
                SalesRecord(itemId = 4, itemName = "Lucky Me! Pancit Canton (Original)", category = "Pagkain", quantity = 3, sellingPrice = 12.50, costPrice = 9.00, date = "2026-04-26"),
                SalesRecord(itemId = 9, itemName = "Magic Sarap (sachet)", category = "Pangluto", quantity = 5, sellingPrice = 3.00, costPrice = 2.00, date = "2026-04-26")
            )

            salesRecords.forEach { salesDao.insert(it) }
        }
    }
}
