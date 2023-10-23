package com.bpr.allergendetector.ui.allergenlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.databinding.FragmentAllergenListBinding

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

        val allergenPlaceholderList: List<Allergen> =
            allergenListViewModel.allergenTempList.value.orEmpty()

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AllergenRecyclerViewAdapter(allergenPlaceholderList)

        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}