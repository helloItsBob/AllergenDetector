package com.bpr.allergendetector.ui.statistics

import androidx.room.Entity

@Entity(tableName = "scan_counter_table", primaryKeys = ["date"])
data class ScanCounter(
    val date: String,
    var count: Int,
    val isHarmful: Int // 0 = false, +1 = true
) {

}