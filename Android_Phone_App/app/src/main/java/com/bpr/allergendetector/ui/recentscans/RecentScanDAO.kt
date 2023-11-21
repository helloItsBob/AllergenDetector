package com.bpr.allergendetector.ui.recentscans

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RecentScanDAO {

    @Insert
    suspend fun insert(recentScan: RecentScan)

    @Insert
    suspend fun insertAll(recentScans: List<RecentScan>)

    @Query("DELETE FROM recent_scan_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM recent_scan_table ORDER BY id DESC LIMIT 10")
    suspend fun getLatestRecentScans(): List<RecentScan>

    @Query("DELETE FROM recent_scan_table WHERE id NOT IN (SELECT id FROM recent_scan_table ORDER BY id DESC LIMIT 10)")
    suspend fun deleteAllExceptLatest()

    @Transaction
    suspend fun deleteAndRetrieveLatest(): List<RecentScan> {
        deleteAllExceptLatest()
        return getLatestRecentScans()
    }
}