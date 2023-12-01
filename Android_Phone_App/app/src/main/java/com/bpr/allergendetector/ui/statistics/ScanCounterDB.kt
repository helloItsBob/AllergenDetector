package com.bpr.allergendetector.ui.statistics

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScanCounter::class], version = 4, exportSchema = false)
abstract class ScanCounterDB : RoomDatabase() {
    abstract fun scanCounterDAO(): ScanCounterDAO

    companion object {
        @Volatile
        private var instance: ScanCounterDB? = null

        fun getInstance(context: Context): ScanCounterDB {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    ScanCounterDB::class.java,
                    "scan_counter_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = db
                db
            }
        }
    }
}