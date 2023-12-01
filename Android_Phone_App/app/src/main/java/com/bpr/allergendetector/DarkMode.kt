package com.bpr.allergendetector

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class DarkMode {

    companion object {
        fun setDarkModeBasedOnPrefs(activity: Activity) {
            val sharedPreferences =
                activity.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val nightMode = sharedPreferences.getBoolean("DARK_MODE", false)
            if (nightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}