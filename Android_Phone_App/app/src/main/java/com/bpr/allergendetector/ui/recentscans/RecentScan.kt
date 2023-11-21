package com.bpr.allergendetector.ui.recentscans

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bpr.allergendetector.ui.allergenlist.Allergen

@Entity(tableName = "recent_scan_table")
data class RecentScan(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var image: String,
    var result: String,
    @TypeConverters(AllergenListConverter::class)
    var allergenList: List<Allergen>?
) {

}