package com.bpr.allergendetector.ui.lists

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    var image: String,
    var category: String,
    var harmful: Boolean,
    var allergens: String?,
    var tags: String?
) {

}