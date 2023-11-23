package com.bpr.allergendetector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bpr.allergendetector.databinding.FragmentGuidelinesBinding

class GuidelinesFragment : Fragment() {

    private var _binding: FragmentGuidelinesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuidelinesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        if (isFirstLogin()) {
            // set to false so that the guidelines are not shown again
            setFirstLoginFlag()
        }
        else {
            val action = GuidelinesFragmentDirections.actionNavigationGuidelinesToNavigationScan()
            findNavController().navigate(action)
        }

        binding.staySafeButton.setOnClickListener {
            val action = GuidelinesFragmentDirections.actionNavigationGuidelinesToNavigationScan()
            findNavController().navigate(action)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isFirstLogin(): Boolean {
        val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPreferences?.getBoolean("FIRST_LOGIN", true)!!
    }

    private fun setFirstLoginFlag() {
        val sharedPreferences = context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("FIRST_LOGIN", false)
        editor?.apply()
    }
}