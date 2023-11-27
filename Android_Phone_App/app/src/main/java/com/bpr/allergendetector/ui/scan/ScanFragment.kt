package com.bpr.allergendetector.ui.scan

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bpr.allergendetector.databinding.FragmentScanBinding
import com.bpr.allergendetector.ui.CameraUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // CameraX
    private lateinit var cameraExecutor: ExecutorService

    private var galleryLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // unused for now
        val scanViewModel =
            ViewModelProvider(this).get(ScanViewModel::class.java)

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Request camera permissions
        if (allPermissionsGranted()) {
            CameraUtil.startCamera(binding.viewFinder, requireContext(), viewLifecycleOwner)
        } else {
            requestPermissions()
        }

        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { CameraUtil.takePhoto(requireContext(), this) }
        cameraExecutor = Executors.newSingleThreadExecutor()

        // setup button listeners
        binding.recentScanButton.setOnClickListener {
            Toast.makeText(requireContext(), "Recent Scan not yet implemented", Toast.LENGTH_SHORT)
                .show()
        }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val selectedImageUri = data.data
                        // Handle the selected image URI here
                        if (selectedImageUri != null) {
                            val action =
                                ScanFragmentDirections.actionNavigationScanToNavigationPhoto(
                                    selectedImageUri.toString()
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }

        binding.openGalleryButton.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher?.launch(galleryIntent)
        }

        binding.recentScanButton.setOnClickListener {
            val action = ScanFragmentDirections.actionNavigationScanToNavigationRecentScans()
            findNavController().navigate(action)
        }

        binding.showHintButton.setOnClickListener {
            val sharedPreferences =
                context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.remove("FIRST_LOGIN")
            editor?.apply()

            val action = ScanFragmentDirections.actionNavigationScanToNavigationGuidelines()
            findNavController().navigate(action)
        }

        // check if the user enabled 'hide hints' in settings
        val sharedPreferences =
            context?.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val hideHints = sharedPreferences?.getBoolean("HIDE_HINTS", false)
        if (hideHints == true) {
            binding.showHintButton.visibility = View.GONE
        } else {
            binding.showHintButton.visibility = View.VISIBLE
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // key listener for the back button
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(CameraUtil.REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = CameraUtil.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                CameraUtil.startCamera(binding.viewFinder, requireContext(), viewLifecycleOwner)
            } else {
                // Permissions denied, prompt the user to go to settings
                showPermissionDeniedDialog()
            }
        }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("To use the camera, please go to the app's settings and grant the necessary permissions.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}