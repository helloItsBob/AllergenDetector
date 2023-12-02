package com.bpr.allergendetector.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.LoginActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentSettingsBinding
import com.bpr.allergendetector.ui.AvatarUtil
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.UiText
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SettingsFragment : Fragment(), SettingsButtonAdapter.OnButtonClickListener {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var selectedAvatar: ImageView? = null
    private var selectedImageUri: Uri? = null
    private var saveButton: Button? = null

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

        // check whether 'hide hints' is on or off
        val sharedPreferences = requireActivity().getSharedPreferences(
            "my_preferences",
            Context.MODE_PRIVATE
        )
        val hideHints = sharedPreferences.getBoolean("HIDE_HINTS", false)
        binding.hideHintsSwitch.isChecked = hideHints

        binding.hideHintsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()

            if (isChecked) {
                editor.putBoolean("HIDE_HINTS", true)
            } else {
                editor.putBoolean("HIDE_HINTS", false)
            }
            editor.apply()
        }

        // text to speech based on user prefs
        val textToSpeech = sharedPreferences.getBoolean("TEXT_TO_SPEECH", false)
        binding.textToSpeechSwitch.isChecked = textToSpeech

        binding.textToSpeechSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()

            if (isChecked) {
                editor.putBoolean("TEXT_TO_SPEECH", true)
            } else {
                editor.putBoolean("TEXT_TO_SPEECH", false)
            }
            editor.apply()
        }

        // dark mode based on user prefs
        val darkMode = sharedPreferences.getBoolean("DARK_MODE", false)
        binding.darkModeSwitch.isChecked = darkMode

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()

            if (isChecked) {
                // dark mode on
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("DARK_MODE", true)
            } else {
                // dark mode off
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("DARK_MODE", false)
            }
            editor.apply()
        }

        binding.aboutButton.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.about_modal, null)
            val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
            val alertDialog = dialogBuilder.create()

            val okButton = dialogView.findViewById<Button>(R.id.okButton)
            okButton.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        // on change avatar button click
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        selectedImageUri = data.data

                        if (selectedImageUri != null) {
                            Log.e("SettingsFragment", "selectedImageUri: $selectedImageUri")
                            Glide.with(this)
                                .load(selectedImageUri)
                                .apply(RequestOptions.circleCropTransform()) // make image circular
                                .into(selectedAvatar!!)

                            saveButton?.isEnabled = true
                        }
                    }
                }
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

    override fun onButtonClicked(buttonName: String) {
        when (buttonName) {
            UiText.StringResource(R.string.change_avatar_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")

                // inflate the dialog with custom view
                val dialogView = layoutInflater.inflate(R.layout.change_avatar_modal, null)
                val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
                val alertDialog = dialogBuilder.create()

                val chooseFromGalleryButton =
                    dialogView.findViewById<Button>(R.id.chooseFromGalleryButtonAvatarModal)
                selectedAvatar = dialogView.findViewById(R.id.selectedImageAvatarModal)
                AvatarUtil.loadAvatarFromSharedPrefs(selectedAvatar!!, requireContext())

                val cancelButton = dialogView.findViewById<Button>(R.id.cancelButtonAvatarModal)
                saveButton = dialogView.findViewById<Button>(R.id.saveButtonAvatarModal)
                saveButton?.isEnabled = false

                chooseFromGalleryButton.setOnClickListener {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher?.launch(galleryIntent)
                }

                cancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }

                saveButton?.setOnClickListener {

                    // convert selected image to a Base64 string
                    val drawable = selectedAvatar?.drawable
                    val bitmap = (drawable as BitmapDrawable).bitmap
                    val resizedBitmap = ImageConverter.resizeBitmap(bitmap, 200, 200)
                    val compressedByteArray =
                        ImageConverter.compressAndCovertBitmapToByteArray(resizedBitmap, 70)
                    val base64Image = ImageConverter.convertByteArrayToBase64(compressedByteArray)

                    // save avatar base64 string to shared preferences for immediate use
                    val sharedPreferences = requireActivity().getSharedPreferences(
                        "my_preferences",
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString("avatar", base64Image)
                    editor.apply()

                    // persist the allergen list to DB
                    val db = FirebaseFirestore.getInstance()
                    val avatar = mapOf("avatar" to base64Image)
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    Log.e("AllergenListFragment", "User ID: $userId")
                    if (userId != null) {
                        val allergensCollectionRef = db.collection("users").document(userId)
                        // update a document
                        allergensCollectionRef.set(avatar, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.e("SettingsFragment", "Avatar updated")
                                Toast.makeText(
                                    context,
                                    "Avatar updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("SettingsFragment", "Error updating avatar", exception)
                            }
                    }

                    alertDialog.dismiss()
                }

                alertDialog.show()
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

            UiText.StringResource(R.string.feedback_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")

                val recipients = arrayOf("abistrovs3@gmail.com", "mx.fokins@gmail.com")
                val subject = "AllergenDetector Feedback"
                val message = "Hello,\n\nI would like to provide the following feedback:\n"

                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.setData(Uri.parse("mailto:"))
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(Intent.EXTRA_TEXT, message)

                startActivity(emailIntent)
            }

            UiText.StringResource(R.string.delete_account_button).asString(context) -> {
                Log.e("SettingsFragment", "$buttonName clicked")

                val dialogView = layoutInflater.inflate(R.layout.delete_account_modal, null)
                val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
                val alertDialog = dialogBuilder.create()

                val description = dialogView.findViewById<TextView>(R.id.deleteAccountDescription)
                val password = dialogView.findViewById<EditText>(R.id.deleteAccountPassword)
                val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
                val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)

                val user = FirebaseAuth.getInstance().currentUser
                val providerId = user?.providerData?.get(1)?.providerId
                if (providerId == GoogleAuthProvider.PROVIDER_ID) {
                    description.text =
                        getString(R.string.are_you_sure_you_want_to_delete_your_account)
                    password.visibility = View.GONE
                }

                cancelButton.setOnClickListener {
                    alertDialog.cancel()
                }

                confirmButton.setOnClickListener {
                    val passwordStr = password.text.toString()
                    if (providerId != GoogleAuthProvider.PROVIDER_ID && passwordStr.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Password cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    settingsViewModel.deleteUserAccount(requireContext(), passwordStr)
                    settingsViewModel.accountHasBeenDeleted.observe(viewLifecycleOwner) { isDeleted ->
                        if (isDeleted) {
                            alertDialog.dismiss()

                            // navigate to login activity
                            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(loginIntent)
                            requireActivity().finish()

                            Toast.makeText(
                                context,
                                "Account has been successfully deleted.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                alertDialog.show()
            }

            else -> {
                Log.e("SettingsFragment", "$buttonName doesn't exist!")
            }
        }
    }
}