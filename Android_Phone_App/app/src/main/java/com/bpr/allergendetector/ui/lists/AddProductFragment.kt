package com.bpr.allergendetector.ui.lists

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.camera.view.PreviewView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.CustomSwitchBinding
import com.bpr.allergendetector.databinding.FragmentProductBinding
import com.bpr.allergendetector.ui.CameraUtil
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.SwitchState
import com.google.android.material.chip.Chip
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: AddProductFragmentArgs by navArgs()

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addProductViewModel = ViewModelProvider(this)[ListsViewModel::class.java]

        _binding = FragmentProductBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        // set the image
        val imagePath = args.imagePath
        binding.imageView.setImageURI(Uri.parse(imagePath))

        val state = args.state
        val switchBinding = CustomSwitchBinding.bind(binding.relativeLayout)
        switchBinding.switchHarmfulHarmless.isEnabled = false
        SwitchState.updateSwitchUI(switchBinding, requireContext(), state)
        if (state == SwitchState.HARMLESS_STATE) {
            binding.textViewPossibleAllergens.visibility = View.GONE
            binding.linearLayout.visibility = View.GONE
        }

        val resultText = args.scanString

        val allergenList: ArrayList<String> = ArrayList()
        addProductViewModel.allAllergens.observe(viewLifecycleOwner) { allergens ->
            allergens.forEach { allergen ->

                val chip = Chip(context)
                chip.text = allergen.name
                chip.isCheckable = true
                chip.isClickable = true
                if (resultText.contains(allergen.name, ignoreCase = true)) {
                    chip.isChecked = true
                    chip.isClickable = false
                    allergenList.add(allergen.name)
                }

                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked && !resultText.contains(allergen.name, ignoreCase = true)) {
                        allergenList.add(allergen.name)
                    } else {
                        allergenList.remove(allergen.name)
                    }
                }

                binding.allergenChipGroup.addView(chip)
            }
        }

        binding.captureButton.setOnClickListener {
            // inflate scan fragment
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.fragment_scan)

            // hide buttons
            val openGalleryButton = dialog.findViewById<Button>(R.id.openGalleryButton)
            val recentScansButton = dialog.findViewById<Button>(R.id.recentScanButton)
            val hintButton = dialog.findViewById<Button>(R.id.showHintButton)
            openGalleryButton.visibility = View.GONE
            recentScansButton.visibility = View.GONE
            hintButton.visibility = View.GONE

            // start camera
            val viewFinder = dialog.findViewById<PreviewView>(R.id.viewFinder)
            CameraUtil.startCamera(viewFinder, requireContext(), viewLifecycleOwner)

            val imageCaptureButton = dialog.findViewById<Button>(R.id.image_capture_button)
            imageCaptureButton.setOnClickListener {
                CameraUtil.takePhoto(requireContext(), this, binding.imageView)
                cameraExecutor = Executors.newSingleThreadExecutor()

                dialog.dismiss()
            }

            dialog.show()
        }

        binding.addTagChip.setOnClickListener {
            addProductViewModel.showAddTagDialog(
                requireContext(),
                inflater,
                binding.tagChipGroup,
                root
            )
        }

        binding.tagChipGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val chip: Chip = group.findViewById(checkedId)
                addProductViewModel.removeTag(group, chip.text.toString())
            }
        }

        binding.cancelButton.setOnClickListener {
            findNavController(root).popBackStack()
        }

        binding.saveButton.setOnClickListener {
            val selectedPosition = binding.dropdownSpinner.selectedItemPosition
            if (binding.productName.text.toString()
                    .isNotEmpty() && selectedPosition != 0
            ) {
                // save product to Room DB
                val imgBitmap = binding.imageView.drawable.toBitmap()
                val resizedBitmap = ImageConverter.resizeBitmap(imgBitmap, 200, 200)
                val compressedByteArray =
                    ImageConverter.compressAndCovertBitmapToByteArray(resizedBitmap, 70)
                val img = ImageConverter.convertByteArrayToBase64(compressedByteArray)

                val product = Product(
                    name = binding.productName.text.toString(),
                    image = img,
                    category = binding.dropdownSpinner.selectedItem.toString(),
                    harmful = state == SwitchState.HARMFUL_STATE,
                    allergens = allergenList.joinToString(","),
                    tags = addProductViewModel.getTags(binding.tagChipGroup).joinToString(",")
                )

                addProductViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                    // if the product name already exists, ask the user to change the name
                    if (products.any { it.name == product.name }) {
                        Toast.makeText(
                            requireContext(),
                            "Product with the same name already exists.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        addProductViewModel.insert(product)

                        // persist the product to firebase DB
                        addProductViewModel.addProductToFireStore(product)

                        // navigate to the list fragment
                        val action =
                            AddProductFragmentDirections.actionNavigationProductToNavigationLists(
                                state
                            )
                        // clear the back stack
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_scan, true)
                            .build()
                        findNavController(root).navigate(action, navOptions)
                    }
                }

            } else Toast.makeText(
                requireContext(),
                "Please fill in all the fields",
                Toast.LENGTH_SHORT
            ).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (this::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }
}