package com.bpr.allergendetector.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.FragmentStatisticsBinding


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var statisticsViewModel: StatisticsViewModel

    private var isBackgroundChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        statisticsViewModel = StatisticsViewModel(requireActivity().application)

        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        statisticsViewModel.allScanCounters.observe(viewLifecycleOwner) { scanCounters ->
            // set up the charts
            statisticsViewModel.setupBarChart(binding.barChart, isBackgroundChanged, scanCounters)
            statisticsViewModel.setupLineChart(binding.lineChart, isBackgroundChanged, scanCounters)

            // week/year filter
            binding.weekYearButton.setOnClickListener {
                if (isBackgroundChanged) {
                    binding.weekYearButton.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.button_bg, null)
                } else {
                    binding.weekYearButton.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.button_bg_reverse, null)
                }
                isBackgroundChanged = !isBackgroundChanged

                statisticsViewModel.setupBarChart(
                    binding.barChart,
                    isBackgroundChanged,
                    scanCounters
                )
                statisticsViewModel.setupLineChart(
                    binding.lineChart,
                    isBackgroundChanged,
                    scanCounters
                )
            }
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
