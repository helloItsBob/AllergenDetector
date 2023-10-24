package com.bpr.allergendetector.ui.allergenlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.databinding.FragmentAllergenListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.roundToInt

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

        val allergenPlaceholderList: MutableList<Allergen> =
            allergenListViewModel.allergenTempList.value.orEmpty().toMutableList()

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AllergenRecyclerViewAdapter(allergenPlaceholderList)

        //hiding back button on top of the screen
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val goBackButton: Button = binding.goBackButton
        goBackButton.setOnClickListener {
            allergenListViewModel.goBack(this)
        }

        //TODO: implement storiing for allergens
        //mock which can add a new, mocked, allergen item and display it on the list immediately
        val addAllergenButton: FloatingActionButton = binding.floatingAddButton
        addAllergenButton.setOnClickListener {
            // Add a new allergen item
            val newItem = Allergen("TestAllergen", (Math.random() * 3).roundToInt())
            allergenPlaceholderList.add(newItem)
            recyclerView.adapter?.notifyItemInserted(allergenPlaceholderList.size - 1)
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