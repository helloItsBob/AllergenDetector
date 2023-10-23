package com.bpr.allergendetector.ui.allergenlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AllergenListViewModel : ViewModel() {

    private val _allergenTempList = MutableLiveData<List<Allergen>>().apply {
        value = listOf(
            Allergen("Peanuts", 1),
            Allergen("Tree Nuts", 2),
            Allergen("Milk", 3),
            Allergen("Eggs", 1),
            Allergen("Wheat", 2),
            Allergen("Soy", 3),
            Allergen("Fish", 1),
            Allergen("Shellfish", 2),
            Allergen("Sesame", 3),
            Allergen("Mustard", 1),
        )
    }
    val allergenTempList: MutableLiveData<List<Allergen>> = _allergenTempList
}