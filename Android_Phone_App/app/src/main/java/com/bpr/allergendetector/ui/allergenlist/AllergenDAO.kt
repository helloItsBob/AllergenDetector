package com.bpr.allergendetector.ui.allergenlist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AllergenDAO {
    @Insert
    suspend fun insert(allergen: Allergen)

    @Insert
    suspend fun insertAll(allergens: List<Allergen>)

    @Update
    suspend fun update(allergen: Allergen)

    @Delete
    suspend fun delete(allergen: Allergen)

    @Query("DELETE FROM allergen_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM allergen_table")
    fun getAllAllergens() : LiveData<List<Allergen>>
}