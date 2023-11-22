package com.bpr.allergendetector.ui.recentscans

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RecentScansViewModel(application: Application) : AndroidViewModel(application) {

    private val recentScanRepo: RecentScanRepo

    init {
        val recentScanDAO = RecentScanDB.getInstance(application).recentScanDAO()
        recentScanRepo = RecentScanRepo(recentScanDAO)
    }

    private val _latestRecentScans = MutableLiveData<List<RecentScan>>()
    val latestRecentScans: LiveData<List<RecentScan>> get() = _latestRecentScans

    fun deleteAndRetrieveLatest() {
        viewModelScope.launch {
            _latestRecentScans.value = recentScanRepo.deleteAndRetrieveLatest()
        }
    }

    fun insertAll(recentScans: List<RecentScan>) {
        viewModelScope.launch {
            recentScanRepo.insertAll(recentScans)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            recentScanRepo.deleteAll()
        }
    }
}