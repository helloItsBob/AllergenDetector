package com.bpr.allergendetector.ui.scan

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.HarmfulItemBinding
import com.bpr.allergendetector.ui.allergenlist.Allergen

class DetectionResultAdapter(private val data: List<Allergen>) :
    RecyclerView.Adapter<DetectionResultAdapter.DetectionResultViewHolder>() {

    inner class DetectionResultViewHolder(binding: HarmfulItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val allergenNumber: TextView = binding.numberField
        val allergenName: TextView = binding.nameField
        var allergenSeverity: ImageView = binding.severityField
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): DetectionResultViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.harmful_item, viewGroup, false)
        return DetectionResultViewHolder(HarmfulItemBinding.bind(view))
    }

    override fun onBindViewHolder(holder: DetectionResultViewHolder, position: Int) {
        val allergen = data[position]
        val actualPosition = position + 1
        holder.allergenNumber.text = "$actualPosition"
        holder.allergenName.text = allergen.name
        holder.allergenSeverity.setColorFilter(
            when (allergen.severity) {
                1 -> ContextCompat.getColor(
                    holder.allergenSeverity.context,
                    R.color.yellow
                )

                2 -> ContextCompat.getColor(
                    holder.allergenSeverity.context,
                    R.color.orange
                )

                3 -> ContextCompat.getColor(
                    holder.allergenSeverity.context,
                    R.color.red
                )

                else -> Color.BLACK
            }
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }
}