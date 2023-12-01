package com.bpr.allergendetector.ui.scan

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenDB
import com.bpr.allergendetector.ui.allergenlist.AllergenRepo
import com.bpr.allergendetector.ui.recentscans.RecentScan
import com.bpr.allergendetector.ui.recentscans.RecentScanDB
import com.bpr.allergendetector.ui.recentscans.RecentScanRepo
import com.bpr.allergendetector.ui.statistics.ScanCounter
import com.bpr.allergendetector.ui.statistics.ScanCounterDB
import com.bpr.allergendetector.ui.statistics.ScanCounterRepo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetectionResultViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: AllergenRepo = AllergenRepo(AllergenDB.getInstance(application).allergenDAO())
    val allAllergens: LiveData<List<Allergen>> = repo.getAllAllergens()

    private val recentScanRepo: RecentScanRepo =
        RecentScanRepo(RecentScanDB.getInstance(application).recentScanDAO())

    private val scanCounterRepo: ScanCounterRepo =
        ScanCounterRepo(ScanCounterDB.getInstance(application).scanCounterDAO())

    fun insert(recentScan: RecentScan) {
        viewModelScope.launch {
            recentScanRepo.insert(recentScan)
        }
    }

    fun trackDailyCount(isHarmful: Int) {
        viewModelScope.launch {
            val currentDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // check if there's an existing record for the current date
            val existingRecord = scanCounterRepo.getDailyScanCount(currentDate)

            if (existingRecord != null) {
                // if a record exists for the current date, increment the count
                val updatedCount = existingRecord.count + 1
                val updatedHarmfulCount = existingRecord.isHarmful + isHarmful
                val updatedRecord = existingRecord.copy(count = updatedCount, isHarmful = updatedHarmfulCount)
                scanCounterRepo.insertOrUpdate(updatedRecord)
            } else {
                // if a record doesn't exist - create a new record
                val newRecord = ScanCounter(date = currentDate, count = 1, isHarmful = isHarmful)
                scanCounterRepo.insertOrUpdate(newRecord)
            }

            Log.e("DetectionResultViewModel",
                scanCounterRepo.getDailyScanCount(currentDate).toString()
            )
        }
    }
}