package com.bpr.allergendetector.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get buttons names from view model
        val buttonNames: List<String> =
            settingsViewModel.buttons.value?.map { it.asString(context) } ?: emptyList()

        // set up adapter for buttons for recycler view
        val buttonAdapter = SettingsButtonAdapter(buttonNames)
        val recyclerView: RecyclerView = binding.settingsButtons
        recyclerView.adapter = buttonAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        binding.hideHintsSwitch.setOnClickListener {
            Toast.makeText(
                context,
                "Hide hints switch triggered",
                Toast.LENGTH_SHORT
            ).show()
        }

        // TODO: Implement proper switch handling
        binding.darkModeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // dark mode on
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // dark mode off
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.aboutButton.setOnClickListener {
            Toast.makeText(
                context,
                "About button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.goBackButton.setOnClickListener {
            findNavController().popBackStack(com.bpr.allergendetector.R.id.navigation_profile, false)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}