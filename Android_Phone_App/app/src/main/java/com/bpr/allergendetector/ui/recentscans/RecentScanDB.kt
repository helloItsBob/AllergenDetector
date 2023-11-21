package com.bpr.allergendetector.ui.recentscans

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecentScan::class], version = 3, exportSchema = false)
@TypeConverters(AllergenListConverter::class)
abstract class RecentScanDB : RoomDatabase() {

    abstract fun recentScanDAO(): RecentScanDAO

    companion object {
        @Volatile
        private var instance: RecentScanDB? = null

        fun getInstance(context: Context): RecentScanDB {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    RecentScanDB::class.java,
                    "recent_scan_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = db
                db
            }
        }
    }
}