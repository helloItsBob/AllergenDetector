package com.bpr.allergendetector.ui.recentscans

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentScanRepo(private val recentScanDAO: RecentScanDAO) {

    suspend fun deleteAndRetrieveLatest(): List<RecentScan> {
        return withContext(Dispatchers.IO) {
            recentScanDAO.deleteAndRetrieveLatest()
        }
    }

    suspend fun insert(recentScan: RecentScan) {
        recentScanDAO.insert(recentScan)
    }

    suspend fun insertAll(recentScans: List<RecentScan>) {
        recentScanDAO.insertAll(recentScans)
    }

    suspend fun deleteAll() {
        recentScanDAO.deleteAll()
    }
}