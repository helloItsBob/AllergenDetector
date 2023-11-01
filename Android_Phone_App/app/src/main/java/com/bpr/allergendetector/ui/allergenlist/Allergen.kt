package com.bpr.allergendetector.ui.allergenlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allergen_table")
data class Allergen(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var severity: Int
) {

}