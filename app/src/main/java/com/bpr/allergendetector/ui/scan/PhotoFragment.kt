package com.bpr.allergendetector.ui.scan

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.databinding.FragmentPhotoBinding

class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: PhotoFragmentArgs by navArgs()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val photoViewModel =
            ViewModelProvider(this).get(PhotoViewModel::class.java)

        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // Load and display the image in an ImageView.
        var imagePath = args.image
        val capturedImage = binding.capturedImage

        // Image cropper
        capturedImage.setImageUriAsync(Uri.parse(imagePath))

        binding.cropButton.setOnClickListener {
            val cropped = capturedImage.getCroppedImage()
            if (cropped != null) {
                photoViewModel.deleteImage(requireContext(), Uri.parse(imagePath))
                imagePath = photoViewModel.saveImageToGallery(requireContext(), cropped).toString()
                capturedImage.setImageUriAsync(Uri.parse(imagePath))
            }
        }

        binding.useButton.setOnClickListener {
            // TODO("Implement the logic to use the photo - crop it and send to ML model.")
            Toast.makeText(requireContext(), "Image is being processed", Toast.LENGTH_SHORT).show()

            // ML Kit Text Recognition
            val image = photoViewModel.imageFromPath(requireContext(), Uri.parse(imagePath))
            val resultFuture = photoViewModel.performTextRecognition(image)
            resultFuture.thenApply { resultText ->
                // switch to a fragment with a result string as an argument
                val action =
                    PhotoFragmentDirections.actionNavigationPhotoToNavigationResult(resultText)
                findNavController().navigate(action)
            }
        }

        binding.retakeButton.setOnClickListener {
            findNavController(root).popBackStack()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}