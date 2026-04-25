package com.sarisync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sarisync.data.dao.CreditTransactionDao
import com.sarisync.data.dao.InventoryDao
import com.sarisync.data.dao.SalesDao
import com.sarisync.data.entity.CreditTransaction
import com.sarisync.data.entity.InventoryItem
import com.sarisync.data.entity.SalesRecord

/**
 * The single Room database for the entire Sari-Sync app.
 *
 * Version 2 adds:
 *  - SalesRecord entity (sales_records table)
 *  - costPrice column on InventoryItem
 *
 * Uses fallbackToDestructiveMigration() so the database is
 * recreated (with seed data) when the schema changes.
 */
@Database(
    entities = [
        InventoryItem::class,
        CreditTransaction::class,
        SalesRecord::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SariSyncDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao
    abstract fun creditTransactionDao(): CreditTransactionDao
    abstract fun salesDao(): SalesDao

    companion object {

        @Volatile
        var INSTANCE: SariSyncDatabase? = null

        fun getDatabase(context: Context): SariSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SariSyncDatabase::class.java,
                    "sari_sync_database"
                )
                    .addCallback(PrepopulateCallback())
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
