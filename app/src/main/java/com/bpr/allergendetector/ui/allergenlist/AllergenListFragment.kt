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

class AllergenListFragment : Fragment() {

    private var _binding: FragmentAllergenListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val allergenListViewModel = ViewModelProvider(this)[AllergenListViewModel::class.java]

        _binding = FragmentAllergenListBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val recyclerView: RecyclerView = binding.allergenList

        val allergenPlaceholderList: ArrayList<Allergen> =
            allergenListViewModel.allergenTempList.value as ArrayList<Allergen>

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AllergenRecyclerViewAdapter(allergenPlaceholderList)

        //hiding back button on top of the screen
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val goBackButton: Button = binding.goBackButton
        goBackButton.setOnClickListener {
            allergenListViewModel.goBack(this)
        }

        //TODO: implement storing for allergens
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
                allergenPlaceholderList.add(
                    Allergen(
                        name,
                        severity.toInt() + 1 //need to add +1 because seekbar goes from 0 to 2 while implementation is 1 to 3
                    )
                )
                recyclerView.adapter?.notifyItemInserted(allergenPlaceholderList.size - 1)
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
}