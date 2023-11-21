package com.bpr.allergendetector.ui.recentscans

import androidx.room.TypeConverter
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AllergenListConverter {

    @TypeConverter
    fun fromString(value: String?): List<Allergen?>? {
        val listType: Type = object : TypeToken<List<Allergen?>?>() {}.type
        return Gson().fromJson<List<Allergen?>>(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<Allergen?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}