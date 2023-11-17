package com.bpr.allergendetector.ui.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.CustomSwitchBinding
import com.bpr.allergendetector.databinding.FragmentListsBinding
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.SwitchState
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ListsFragment : Fragment(), ListRecyclerViewAdapter.ButtonClickListener {

    private var _binding: FragmentListsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var listsViewModel: ListsViewModel

    private val args: ListsFragmentArgs by navArgs()
    private lateinit var state: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listsViewModel = ViewModelProvider(this)[ListsViewModel::class.java]

        _binding = FragmentListsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // TODO: implement search functionality
        binding.searchButton.setOnClickListener {
            val searchString = binding.searchString.text.trim().toString()
            if (searchString.isNotEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Searching for \"$searchString\"",
                    Toast.LENGTH_SHORT
                ).show()

            } else Toast.makeText(
                requireContext(),
                "Search field is empty",
                Toast.LENGTH_SHORT
            ).show()

            binding.searchString.text.clear()
        }

        // init adapter for the RecyclerView
        val adapter = ListRecyclerViewAdapter(ArrayList())
        val recyclerView: RecyclerView = binding.productList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // set the buttonClickListener for the adapter
        adapter.buttonClickListener = this@ListsFragment

        state = args.state

        listsViewModel.allProducts.observe(viewLifecycleOwner) { productList ->

            // initialize the adapter with the data
            adapter.updateData(state, productList)

            val switchBinding = CustomSwitchBinding.bind(binding.relativeLayout)
            SwitchState.updateSwitchUI(switchBinding, requireContext(), state)

            switchBinding.switchHarmfulHarmless.setOnCheckedChangeListener { _, checked ->
                state = if (checked) SwitchState.HARMLESS_STATE else SwitchState.HARMFUL_STATE
                SwitchState.updateSwitchUI(switchBinding, requireContext(), state)
                adapter.updateData(state, productList)
            }
        }

        // TODO: implement share button
        binding.shareButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Not yet implemented",
                Toast.LENGTH_SHORT
            ).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.navigation_scan, false)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewEditButtonClick(product: Product) {
        val currentName = product.name
        val currentCategory = product.category
        val currentAllergens = product.allergens?.split(",")?.toTypedArray()
        val currentTags = product.tags?.split(",")?.toTypedArray()

        // inflate the dialog with custom view
        val dialogView = layoutInflater.inflate(R.layout.view_edit_modal, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val alertDialog = dialogBuilder.create()

        val productImage = dialogView.findViewById<ImageView>(R.id.imageView)
        val base64 = product.image
        val image = ImageConverter.convertStringToBitmap(base64)
        Glide.with(requireContext())
            .load(image)
            .into(productImage)

        val productName = dialogView.findViewById<EditText>(R.id.productName)
        productName.setText(currentName)

        val productCategory = dialogView.findViewById<Spinner>(R.id.dropdownSpinner)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_items,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productCategory.adapter = adapter
        val position = adapter.getPosition(currentCategory)
        productCategory.setSelection(position)

        val productAllergens = dialogView.findViewById<ChipGroup>(R.id.allergenChipGroup)

        if (state == SwitchState.HARMLESS_STATE) {
            dialogView.findViewById<TextView>(R.id.textViewPossibleAllergens).visibility = View.GONE
            productAllergens.visibility = View.GONE
        }

        listsViewModel.allAllergens.observe(viewLifecycleOwner) { allergenList ->

            allergenList.forEach { allergen ->
                val chip = Chip(requireContext())
                chip.text = allergen.name
                chip.isCheckable = true
                chip.isClickable = true
                if (currentAllergens != null) {
                    chip.isChecked = currentAllergens.contains(allergen.name)
                }

                productAllergens.addView(chip)
            }
        }

        val productTags = dialogView.findViewById<ChipGroup>(R.id.tagChipGroup)
        currentTags?.forEach { tag ->
            val chip = Chip(requireContext())
            chip.text = tag
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                productTags.removeView(chip)
            }
            productTags.addView(chip)
        }

        // add tag chip
        val addTag = dialogView.findViewById<Chip>(R.id.addTagChip)
        addTag.setOnClickListener {
            listsViewModel.showAddTagDialog(
                requireContext(),
                layoutInflater,
                productTags,
                dialogView
            )
        }

        // remove tag chip
        productTags.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val chip: Chip = group.findViewById(checkedId)
                listsViewModel.removeTag(group, chip.text.toString())
            }
        }

        val okButton = dialogView.findViewById<Button>(R.id.okButton)
        okButton.setOnClickListener {

            val newName = productName.text.toString().trim()
            val newCategory = productCategory.selectedItem.toString()

            val newAllergens = ArrayList<String>()
            productAllergens.forEach { chip ->
                chip as Chip
                if (chip.isChecked) {
                    newAllergens.add(chip.text.toString())
                }
            }

            val newTags = listsViewModel.getTags(productTags)

            // only update if there are changes
            if (newName == currentName &&
                newCategory == currentCategory &&
                newAllergens == currentAllergens?.toList() &&
                newTags == currentTags?.toList()
            ) {
                alertDialog.dismiss()
                return@setOnClickListener
            } else if (newName == currentName &&
                newCategory == currentCategory &&
                newTags == currentTags?.toList()
            ) {
                alertDialog.dismiss()
                return@setOnClickListener
            } else {
                val newProduct = Product(
                    product.id,
                    newName,
                    product.image,
                    newCategory,
                    product.harmful,
                    newAllergens.joinToString(","),
                    newTags.joinToString(",")
                )

                listsViewModel.update(newProduct)

                // update in the fireStore
                listsViewModel.updateProductInFireStore(productName, newProduct)

                alertDialog.dismiss()

                Toast.makeText(
                    requireContext(),
                    "Updated product: $newName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        alertDialog.show()
    }

    override fun onDeleteButtonClick(product: Product) {
        listsViewModel.delete(product)

        // delete from the fireStore
        listsViewModel.deleteProductFromFireStore(product)

        Toast.makeText(
            requireContext(),
            "Removed product: ${product.name}",
            Toast.LENGTH_SHORT
        ).show()
    }
}