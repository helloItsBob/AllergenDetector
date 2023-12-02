package com.bpr.allergendetector.ui.scan

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentDetectionResultBinding
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.SwitchState
import com.bpr.allergendetector.ui.UiText
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.recentscans.RecentScan
import java.util.Locale


class DetectionResultFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentDetectionResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: DetectionResultFragmentArgs by navArgs()

    // create a media player for the sound
    private lateinit var mediaPlayer: MediaPlayer

    private var hasVibrated = false
    private lateinit var vibrator: VibratorManager

    private var textToSpeech: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val detectionResultViewModel = ViewModelProvider(this)[DetectionResultViewModel::class.java]

        _binding = FragmentDetectionResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // text to speech init
        textToSpeech = TextToSpeech(requireContext(), this)

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // set the image and text
        val resultPicture = args.imagePath
        binding.imageView.setImageURI(Uri.parse(resultPicture))

        // resulting scan textString from result/edit text fragment
        val resultText = args.scanString

        // create a list of detected allergens
        val detectedAllergens: ArrayList<Allergen> = ArrayList()
        detectionResultViewModel.allAllergens.observe(viewLifecycleOwner) { allergenList ->
            for (allergen in allergenList) {
                if (resultText.contains(allergen.name, ignoreCase = true)) {
                    detectedAllergens.add(allergen)
                }
            }

            // display the result
            displayDetectionResult(detectedAllergens)


            // convert picture to base64 string
            val drawable = binding.imageView.drawable
            val bitmap = (drawable as BitmapDrawable).bitmap
            val resizedBitmap = ImageConverter.resizeBitmap(bitmap, 200, 200)
            val compressedByteArray =
                ImageConverter.compressAndCovertBitmapToByteArray(resizedBitmap, 70)
            val base64Image = ImageConverter.convertByteArrayToBase64(compressedByteArray)

            // save scan to recent scans
            val recentScan = RecentScan(
                0,
                base64Image,
                if (detectedAllergens.isNotEmpty()) SwitchState.HARMFUL_STATE else SwitchState.HARMLESS_STATE,
                detectedAllergens
            )
            detectionResultViewModel.insert(recentScan)
            detectionResultViewModel.trackDailyCount(if (detectedAllergens.isNotEmpty()) 1 else 0)
        }

        binding.shareButton.setOnClickListener {

            var state = SwitchState.HARMLESS_STATE
            if (detectedAllergens.isNotEmpty()) {
                state = SwitchState.HARMFUL_STATE
            }

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"

            var shareText = "This product is $state for me.\n"
            if (state != SwitchState.HARMLESS_STATE) {
                shareText += "\nIt contains the following allergens:\n"
            }
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareText)

            // calculate the max length of the allergen name
            val maxNameLength = detectedAllergens.maxByOrNull { it.name.length }?.name?.length ?: 0

            shareIntent.putExtra(
                Intent.EXTRA_TEXT, detectedAllergens.joinToString(separator = "\n") {
                    val formattedName = "%-${maxNameLength}s".format(it.name.padEnd(10))
                    "• ${
                        formattedName.replaceFirstChar { name ->
                            if (name.isLowerCase()) name.titlecase(
                                Locale.getDefault()
                            ) else name.toString()
                        }
                    }: ${
                        when (it.severity) {
                            1 -> "Low Risk"
                            2 -> "Moderate Risk"
                            3 -> "High Risk"
                            else -> "Unknown"
                        }
                    }"
                }
            )

            val fileUri = Uri.parse(resultPicture)
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)

            startActivity(Intent.createChooser(shareIntent, "Share Detection Result"))
        }

        binding.saveButton.setOnClickListener {
            var state = SwitchState.HARMLESS_STATE
            if (detectedAllergens.isNotEmpty()) {
                state = SwitchState.HARMFUL_STATE
            }

            val action =
                DetectionResultFragmentDirections.actionNavigationDetectionResultToNavigationProduct(
                    resultPicture,
                    state,
                    resultText
                )
            findNavController().navigate(action)
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

    private fun displayDetectionResult(detectedAllergens: ArrayList<Allergen>) {
        if (detectedAllergens.isNotEmpty()) {
            binding.textResult.text =
                UiText.StringResource(R.string.harmful_ingredients).asString(context)
            // set text color to red
            binding.textResult.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )

            // adapter for the recycler view of detected allergens
            val adapter = DetectionResultAdapter(detectedAllergens)
            val recyclerView: RecyclerView = binding.textViewResult
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            // vibrate
            if (!hasVibrated) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    vibrator =
                        requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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
                    hasVibrated = true

                } else {
                    // if version is less than S, use deprecated method
                    val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
                    vibrator!!.vibrate(longArrayOf(300, 200, 100), -1)
                    hasVibrated = true
                }
            }

            // make a sound
            if (!this::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer.create(
                    requireContext(),
                    R.raw.siren
                )
                mediaPlayer.start()
            }

            // convert text to speech
            val readString = "Attention, have found following " + binding.textResult.text.toString()
            var readAllergens = ""
            for (allergen in detectedAllergens) {
                readAllergens += allergen.name + ", "
            }
            convertTextToSpeech(readString)
            convertTextToSpeech(readAllergens)

        } else {
            binding.textResult.text =
                UiText.StringResource(R.string.no_harmful_ingredients).asString(context)
            // set text color to green
            binding.textResult.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )

            // make a sound
            if (!this::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer.create(
                    requireContext(),
                    R.raw.success
                )
                mediaPlayer.setVolume(0.2f, 0.2f)
                mediaPlayer.start()
            }

            // convert text to speech
            convertTextToSpeech(binding.textResult.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        try {
            if (this::mediaPlayer.isInitialized) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            textToSpeech?.stop()
            textToSpeech?.shutdown()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val langResult = textToSpeech?.setLanguage(Locale.US)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported or missing data")
            } else {
                Log.i("TTS", "TextToSpeech engine initialized successfully")
            }
        } else {
            Log.e("TTS", "TextToSpeech initialization failed")
        }
    }

    private fun convertTextToSpeech(text: String) {
        if (textToSpeech != null) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        }
    }
}