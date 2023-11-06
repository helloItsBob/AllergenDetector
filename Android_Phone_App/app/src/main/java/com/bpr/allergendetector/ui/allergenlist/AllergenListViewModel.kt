package com.bpr.allergendetector.ui.allergenlist

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import kotlinx.coroutines.launch

class AllergenListViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: AllergenRepo = AllergenRepo(AllergenDB.getInstance(application).allergenDAO())
    val allAllergens: LiveData<List<Allergen>> = repo.getAllAllergens()

    fun insert(allergen: Allergen) {
        viewModelScope.launch {
            if (allergen.name.isNotEmpty()) {
                repo.insert(allergen)
            }
        }
    }

    fun insertAll(allergens: List<Allergen>) {
        viewModelScope.launch {
            repo.insertAll(allergens)
        }
    }

    fun update(allergen: Allergen) {
        viewModelScope.launch {
            repo.update(allergen)
        }
    }

    fun delete(allergen: Allergen) {
        viewModelScope.launch {
            repo.delete(allergen)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repo.deleteAll()
        }
    }

    //Method to go back to previous fragment in the stack
    fun goBack(fragment: Fragment) {
        val navController = findNavController(fragment)
        navController.popBackStack()
    }
}