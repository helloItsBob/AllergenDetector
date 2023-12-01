package com.bpr.allergendetector.ui.statistics

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "scan_counter_table", primaryKeys = ["date"])
data class ScanCounter(
    val date: String,
    var count: Int,
    @SerializedName("harmful")
    val isHarmful: Int // 0 = false, +1 = true
) {

}