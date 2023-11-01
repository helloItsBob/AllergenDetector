package com.bpr.allergendetector.ui.allergenlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.databinding.AddAllergenItemBinding
import com.bpr.allergendetector.databinding.FragmentAllergenListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AllergenListFragment : Fragment(), AllergenRecyclerViewAdapter.ButtonClickListener {

    private var _binding: FragmentAllergenListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var allergenListViewModel: AllergenListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        allergenListViewModel = ViewModelProvider(this)[AllergenListViewModel::class.java]

        _binding = FragmentAllergenListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Create the adapter and assign it to the RecyclerView
        val adapter = AllergenRecyclerViewAdapter(ArrayList())

        val recyclerView: RecyclerView = binding.allergenList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Observe the LiveData from your ViewModel
        allergenListViewModel.allAllergens.observe(viewLifecycleOwner) { allergenList ->
            // Update the data in the adapter when LiveData changes
            adapter.updateData(allergenList)
        }

        // Set the buttonClickListener for the adapter
        adapter.buttonClickListener = this@AllergenListFragment

        //hiding back button on top of the screen
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val goBackButton: Button = binding.goBackButton
        goBackButton.setOnClickListener {
            allergenListViewModel.goBack(this)
        }

        // add allergen
        val addAllergenButton: FloatingActionButton = binding.floatingAddButton
        addAllergenButton.setOnClickListener {

            //assigning the layout to the dialog
            var addItemBinding: AddAllergenItemBinding? = null
            addItemBinding = AddAllergenItemBinding.inflate(inflater, container, false)
            val allergenName = addItemBinding.allergenName
            val allergenSeverity = addItemBinding.severitySeekBar

            val addDialog = AlertDialog.Builder(requireContext())

            addDialog.setView(addItemBinding.root)

            //setting "ok" and "cancel" buttons for the dialog
            addDialog.setPositiveButton("Ok") { dialog, _ ->
                val name = allergenName.text.toString()
                val severity = allergenSeverity.progress.toString()
                for (allergen in allergenListViewModel.allAllergens.value!!) {
                    if (allergen.name == name) {
                        Toast.makeText(
                            requireContext(),
                            "Allergen already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    } else if (name.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Allergen name cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }
                }
                // store the allergen in the room database
                allergenListViewModel.insert(Allergen(0, name, severity.toInt() + 1))
                allergenListViewModel.allAllergens.value?.let { it1 ->
                    recyclerView.adapter?.notifyItemInserted(
                        it1.size
                    )
                }
                Toast.makeText(
                    requireContext(),
                    "Successfully added allergen to the list",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            addDialog.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(requireContext(), "Canceled process", Toast.LENGTH_SHORT).show()
            }
            addDialog.create()
            addDialog.show()
        }

        //TODO: implement sharing of allergens
        val shareAllergensButton: FloatingActionButton = binding.floatingShareButton
        shareAllergensButton.setOnClickListener {
            Toast.makeText(
                shareAllergensButton.context,
                "Share functionality is not implemented.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEditButtonClicked(allergen: Allergen) {
        allergenListViewModel.update(allergen)
        Toast.makeText(
            requireContext(),
            "Allergen information has been edited",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDeleteButtonClicked(allergen: Allergen) {
        allergenListViewModel.delete(allergen)
        Toast.makeText(
            requireContext(),
            "Removed allergen: ${allergen.name}",
            Toast.LENGTH_SHORT
        ).show()
    }
}