package com.bpr.allergendetector.ui.lists

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDAO {
    @Insert
    suspend fun insert(product: Product)

    @Insert
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("DELETE FROM product_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM product_table")
    fun getAllProducts() : LiveData<List<Product>>
}