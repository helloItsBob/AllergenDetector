package com.bpr.allergendetector.ui.lists

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 4, exportSchema = false)
abstract class ProductDB : RoomDatabase() {
    abstract fun productDAO(): ProductDAO

    companion object {
        @Volatile
        private var instance: ProductDB? = null

        fun getInstance(context: Context): ProductDB {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDB::class.java,
                    "product_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = db
                db
            }
        }
    }
}