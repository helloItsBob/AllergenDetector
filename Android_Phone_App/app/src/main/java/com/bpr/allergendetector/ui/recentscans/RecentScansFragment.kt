package com.bpr.allergendetector.ui.recentscans

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentRecentScansBinding
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.SwitchState
import com.bpr.allergendetector.ui.scan.DetectionResultAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Locale


class RecentScansFragment : Fragment(), RecentScansAdapter.ButtonClickListener {

    private var _binding: FragmentRecentScansBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recentScansViewModel: RecentScansViewModel

    private lateinit var recentScanList: List<RecentScan>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        recentScansViewModel = ViewModelProvider(this)[RecentScansViewModel::class.java]

        _binding = FragmentRecentScansBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // get rid of the back button in the action bar
        val actionBar = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // initialize the adapter
        val adapter = RecentScansAdapter(ArrayList())
        val recyclerView: RecyclerView = binding.recentScansList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // set the button click listener
        adapter.buttonClickListener = this@RecentScansFragment

        recentScansViewModel.deleteAndRetrieveLatest()
        recentScansViewModel.latestRecentScans.observe(viewLifecycleOwner) { recentScans ->
            adapter.updateData(recentScans)
            this.recentScanList = recentScans
        }

        binding.goBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()

        // persist recent scan list to fireStore DB
        val db = FirebaseFirestore.getInstance()
        val recentScanMap = mapOf("recentScan" to recentScanList)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val collectionRef = db.collection("users").document(userId)
            // update a document
            collectionRef.set(recentScanMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e("RecentScans", "Recent scan list updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e("RecentScans", "Error updating recent scan list", exception)
                }
        }
    }

    override fun onViewScanButtonClick(recentScan: RecentScan) {
        // inflate the detection result fragment with the recent scan data
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_detection_result)

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val resultPicture = dialog.findViewById<ImageView>(R.id.imageView)
        val resultText = dialog.findViewById<TextView>(R.id.textResult)
        val resultAllergens = dialog.findViewById<RecyclerView>(R.id.textViewResult)

        val shareButton = dialog.findViewById<Button>(R.id.shareButton)
        val saveButton = dialog.findViewById<Button>(R.id.saveButton)
        saveButton.visibility = View.GONE

        val returnButton = dialog.findViewById<Button>(R.id.returnButton)

        // convert the base64 string to a bitmap
        val baseString = recentScan.image
        val bitmap = ImageConverter.convertStringToBitmap(baseString)
        resultPicture.setImageBitmap(bitmap)
        resultPicture.scaleType = ImageView.ScaleType.FIT_XY

        // set the text
        resultText.text = recentScan.result
        if (recentScan.result == SwitchState.HARMFUL_STATE) {
            resultText.setTextColor(resources.getColor(R.color.red))
        } else {
            resultText.setTextColor(resources.getColor(R.color.green))
        }

        // set the allergens
        val adapter = DetectionResultAdapter(recentScan.allergenList ?: emptyList())
        val recyclerView: RecyclerView = resultAllergens
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        shareButton.setOnClickListener {

            try {
                // temporary file to share
                val path = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    bitmap,
                    "Recent Scan",
                    null
                )

                val imageUri = Uri.parse(path)

                // send intent
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "This recently scanned product is ${recentScan.result} for me.\n" +
                            "\nIt contains the following allergens:\n"
                )
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                // calculate the max length of the allergen name
                val maxNameLength =
                    recentScan.allergenList?.maxByOrNull { it.name.length }?.name?.length ?: 0

                intent.putExtra(
                    Intent.EXTRA_TEXT, recentScan.allergenList?.joinToString(separator = "\n") {
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

                startActivity(Intent.createChooser(intent, "Share Recent Scan"))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        returnButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}