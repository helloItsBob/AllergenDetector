package com.bpr.allergendetector.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultViewModel =
            ViewModelProvider(this).get(ResultViewModel::class.java)

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val resultText = args.result
        binding.textViewResult.text = resultText

        binding.buttonScanAgain.setOnClickListener {
            findNavController().popBackStack(
                R.id.navigation_scan,
                false
            )
        }

        resultViewModel.handleOnBackPressed(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}