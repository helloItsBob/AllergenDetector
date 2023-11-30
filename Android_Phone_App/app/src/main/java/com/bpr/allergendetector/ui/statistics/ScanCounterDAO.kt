package com.bpr.allergendetector.ui.statistics

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScanCounterDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(scanCounter: ScanCounter)

    @Query("SELECT * FROM scan_counter_table WHERE date = :date")
    suspend fun getDailyScanCount(date: String): ScanCounter?

    // get a list of liveData for all scan counters
    @Query("SELECT * FROM scan_counter_table")
    fun getAllScanCounters(): LiveData<List<ScanCounter>>
}