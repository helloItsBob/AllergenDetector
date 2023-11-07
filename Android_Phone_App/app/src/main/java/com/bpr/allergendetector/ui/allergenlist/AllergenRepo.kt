package com.bpr.allergendetector.ui.allergenlist

import androidx.lifecycle.LiveData

class AllergenRepo(private val allergenDAO: AllergenDAO) {

    private val allAllergens: LiveData<List<Allergen>> = allergenDAO.getAllAllergens()

    fun getAllAllergens(): LiveData<List<Allergen>> {
        return allAllergens
    }

    suspend fun insert(allergen: Allergen) {
        allergenDAO.insert(allergen)
    }

    suspend fun insertAll(allergens: List<Allergen>) {
        allergenDAO.insertAll(allergens)
    }

    suspend fun update(allergen: Allergen) {
       allergenDAO.update(allergen)
    }

    suspend fun delete(allergen: Allergen) {
        allergenDAO.delete(allergen)
    }

    suspend fun deleteAll() {
        allergenDAO.deleteAll()
    }
}