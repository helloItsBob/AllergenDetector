package com.bpr.allergendetector.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentSettingsBinding
import com.bpr.allergendetector.ui.UiText


class SettingsFragment : Fragment(), SettingsButtonAdapter.OnButtonClickListener {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get buttons names from view model
        val buttonNames: List<String> =
            settingsViewModel.buttons.value?.map { it.asString(context) } ?: emptyList()

        // set up adapter for buttons for recycler view
        val buttonAdapter = SettingsButtonAdapter(buttonNames)
        buttonAdapter.setOnButtonClickListener(this)
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
            findNavController().popBackStack(R.id.navigation_profile, false)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // TODO: Add the rest of onClickListeners here
    override fun onButtonClicked(buttonName: String) {
        when (buttonName) {
            UiText.StringResource(R.string.change_avatar_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")
            }

            UiText.StringResource(R.string.change_password_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")

                // inflate the dialog with custom view
                val dialogView = layoutInflater.inflate(R.layout.change_password_modal, null)
                val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
                val alertDialog = dialogBuilder.create()

                val oldPassword = dialogView.findViewById<EditText>(R.id.oldPasswordEditText)
                val newPassword = dialogView.findViewById<EditText>(R.id.newPasswordEditText)
                val confirmPassword =
                    dialogView.findViewById<EditText>(R.id.confirmNewPasswordEditText)

                val cancelButton = dialogView.findViewById<Button>(R.id.cancelButtonModal)
                val saveButton = dialogView.findViewById<Button>(R.id.saveButtonModal)

                cancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }

                saveButton.setOnClickListener {

                    // re-authenticate user and update password
                    settingsViewModel.updatePassword(
                        requireContext(),
                        oldPassword,
                        newPassword,
                        confirmPassword
                    )

                    settingsViewModel.passwordChangeSuccessLiveData.observe(viewLifecycleOwner) { success ->
                        if (success) {
                            alertDialog.dismiss()
                        } else {
                            // keep the dialog open
                        }
                    }
                }

                alertDialog.show()
            }

            UiText.StringResource(R.string.change_language_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")
            }

            UiText.StringResource(R.string.feedback_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")
            }

            UiText.StringResource(R.string.delete_account_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")
            }

            else -> {
                Log.e("SettingsFragment", "$buttonName doesn't exist!")
            }
        }
    }
}