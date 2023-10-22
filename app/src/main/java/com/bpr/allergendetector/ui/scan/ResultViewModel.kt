package com.bpr.allergendetector.ui.scan

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

import com.bpr.allergendetector.R

class ResultViewModel : ViewModel() {

    fun handleOnBackPressed(fragment: Fragment) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragment.view?.let {
                    findNavController(fragment).popBackStack(
                        R.id.navigation_scan,
                        false
                    )
                }
            }
        }
        fragment.requireActivity().onBackPressedDispatcher.addCallback(
            fragment.viewLifecycleOwner,
            callback
        )
    }
}