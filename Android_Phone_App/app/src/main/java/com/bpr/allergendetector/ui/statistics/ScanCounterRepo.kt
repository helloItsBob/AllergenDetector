package com.bpr.allergendetector.ui.statistics

import androidx.lifecycle.LiveData

class ScanCounterRepo(private val scanCounterDAO: ScanCounterDAO) {

    private val allScanCounters: LiveData<List<ScanCounter>> = scanCounterDAO.getAllScanCounters()

    fun getAllScanCounters() : LiveData<List<ScanCounter>> {
        return allScanCounters
    }

    suspend fun insertOrUpdate(scanCounter: ScanCounter) {
        scanCounterDAO.insertOrUpdate(scanCounter)
    }

    suspend fun getDailyScanCount(date: String): ScanCounter? {
        return scanCounterDAO.getDailyScanCount(date)
    }

    suspend fun insertAll(scanCounters: List<ScanCounter>) {
        scanCounterDAO.insertAll(scanCounters)
    }

    suspend fun deleteAll() {
        scanCounterDAO.deleteAll()
    }
}