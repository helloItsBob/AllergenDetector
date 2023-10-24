package com.bpr.allergendetector.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageView: ImageView = binding.profileImage
        profileViewModel.image.observe(viewLifecycleOwner) {
            imageView.setImageResource(it)
        }

        val textView: TextView = binding.profileName
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it.asString(context)
        }

        val buttonDataList: List<String> = profileViewModel.buttons.value.orEmpty().map {
            it.asString(context)
        }

        val navController = activity?.findNavController(R.id.nav_host_fragment_activity_main)
        val profileButtonAdapter = ProfileButtonAdapter(buttonDataList, navController!!)


        val recyclerView: RecyclerView = binding.profileButtonList
        recyclerView.adapter = profileButtonAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val logoutButton: Button = binding.logoutButton
        logoutButton.setOnClickListener {
            Toast.makeText(
                logoutButton.context,
                "Logout is not implemented.",
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