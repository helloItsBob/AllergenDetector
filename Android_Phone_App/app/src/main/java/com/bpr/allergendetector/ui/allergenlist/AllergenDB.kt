package com.bpr.allergendetector.ui.allergenlist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Allergen::class], version = 2, exportSchema = false)
abstract class AllergenDB : RoomDatabase() {
    abstract fun allergenDAO(): AllergenDAO

    companion object {
        @Volatile
        private var instance: AllergenDB? = null

        fun getInstance(context: Context): AllergenDB {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AllergenDB::class.java,
                    "allergen_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = db
                db
            }
        }
    }
}