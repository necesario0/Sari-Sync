package com.sarisync.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sarisync.data.dao.CreditTransactionDao
import com.sarisync.data.dao.InventoryDao
import com.sarisync.data.entity.CreditTransaction
import com.sarisync.data.entity.InventoryItem

/**
 * The single Room database for the entire Sari-Sync app.
 *
 * Now includes a PrepopulateCallback that seeds the database
 * with realistic sari-sari store data on first launch.
 *
 * IMPORTANT: If you change the schema during the hackathon,
 * uninstall the app from your device/emulator first so the
 * database is recreated and the seed data is inserted again.
 */
@Database(
    entities = [
        InventoryItem::class,
        CreditTransaction::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SariSyncDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao
    abstract fun creditTransactionDao(): CreditTransactionDao

    companion object {

        @Volatile
        var INSTANCE: SariSyncDatabase? = null  // Changed from private to internal for callback access

        fun getDatabase(context: Context): SariSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SariSyncDatabase::class.java,
                    "sari_sync_database"
                )
                    .addCallback(PrepopulateCallback())  // ← THIS IS THE NEW LINE
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}