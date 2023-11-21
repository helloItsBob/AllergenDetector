package com.bpr.allergendetector.ui.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenDB
import com.bpr.allergendetector.ui.allergenlist.AllergenRepo
import com.bpr.allergendetector.ui.recentscans.RecentScan
import com.bpr.allergendetector.ui.recentscans.RecentScanDB
import com.bpr.allergendetector.ui.recentscans.RecentScanRepo
import kotlinx.coroutines.launch

class DetectionResultViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: AllergenRepo = AllergenRepo(AllergenDB.getInstance(application).allergenDAO())
    val allAllergens: LiveData<List<Allergen>> = repo.getAllAllergens()

    private val recentScanRepo: RecentScanRepo =
        RecentScanRepo(RecentScanDB.getInstance(application).recentScanDAO())

    fun insert(recentScan: RecentScan) {
        viewModelScope.launch {
            recentScanRepo.insert(recentScan)
        }
    }
}