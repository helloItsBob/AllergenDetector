package com.bpr.allergendetector.ui.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenDB
import com.bpr.allergendetector.ui.allergenlist.AllergenRepo

class DetectionResultViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: AllergenRepo = AllergenRepo(AllergenDB.getInstance(application).allergenDAO())
    val allAllergens: LiveData<List<Allergen>> = repo.getAllAllergens()
}