package com.bpr.allergendetector.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.databinding.FragmentPhotoBinding
import com.bumptech.glide.Glide


class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: PhotoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val photoViewModel =
            ViewModelProvider(this).get(PhotoViewModel::class.java)

        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load and display the image in an ImageView.
        val imagePath = args.image
        val capturedImage = binding.capturedImage
        Glide.with(requireContext())
            .load(imagePath)
            .into(capturedImage)

        binding.useButton.setOnClickListener {
            // TODO("Implement the logic to use the photo - crop it and send to ML model.")
            Toast.makeText(requireContext(), "Image is being processed", Toast.LENGTH_SHORT).show()
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