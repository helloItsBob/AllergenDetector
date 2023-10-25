package com.bpr.allergendetector.ui.scan

import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentDetectionResultBinding


class DetectionResultFragment : Fragment() {

    private var _binding: FragmentDetectionResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: DetectionResultFragmentArgs by navArgs()

    // create a media player for the sound
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetectionResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // set the image and text
        val resultPicture = args.imagePath
        binding.imageView.setImageURI(Uri.parse(resultPicture))

        // for now just set it in text view, later implement actual detection logic
        val resultText = args.scanString
        binding.textResult.text = "HARMFUL INGREDIENTS:"
        binding.textViewResult.text = resultText

        if (binding.textResult.text == "HARMFUL INGREDIENTS:") {
            // set text color to red
            binding.textResult.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibrator: VibratorManager =
                    requireContext().getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val timings: LongArray = longArrayOf(300, 200, 100)
                val amplitudes: IntArray = intArrayOf(90, 200, 160)
                val repeatIndex = -1 // Do not repeat.
                vibrator.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(
                        timings,
                        amplitudes,
                        repeatIndex
                    )
                )
            } else {
                // if version is less than S, use deprecated method
                val vibrator = activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator?
                vibrator!!.vibrate(longArrayOf(300, 200, 100), -1)
            }

            // make a sound
            if (!this::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer.create(
                    requireContext(),
                    R.raw.siren
                )
            }
            mediaPlayer.start()

        } else {
            // Do nothing for now
        }

        binding.shareButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Not yet implemented",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.saveButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Not yet implemented",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.returnButton.setOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_scan,
                false
            )
        }

        // handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.navigation_scan, false)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}