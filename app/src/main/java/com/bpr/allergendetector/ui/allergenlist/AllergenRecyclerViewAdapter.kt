package com.bpr.allergendetector.ui.allergenlist

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bpr.allergendetector.databinding.FragmentAllergenItemBinding

class AllergenRecyclerViewAdapter(
    private val values: List<Allergen>
) : RecyclerView.Adapter<AllergenRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentAllergenItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = position.toString()
        holder.contentView.text = item.name
        holder.severityView = when (item.severity) {
            1 -> {
                holder.severityView.setColorFilter(Color.GREEN)
                holder.severityView
            }

            2 -> {
                holder.severityView.setColorFilter(Color.YELLOW)
                holder.severityView
            }

            3 -> {
                holder.severityView.setColorFilter(Color.RED)
                holder.severityView
            }

            else -> {
                holder.severityView.setColorFilter(Color.BLACK)
                holder.severityView
            }
        }
        //TODO implement edit for allergens
        holder.editButtonView.setOnClickListener {
            Toast.makeText(
                holder.editButtonView.context,
                "Edit is not implemented yet.",
                Toast.LENGTH_SHORT
            ).show()
        }
        //TODO implement remove for allergens
        holder.deleteButtonView.setOnClickListener {
            Toast.makeText(
                holder.editButtonView.context,
                "Remove is not implemented yet.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAllergenItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.allergenNumber
        val contentView: TextView = binding.allergenContent
        var severityView: ImageView = binding.allergenSeverity
        val editButtonView: ImageButton = binding.allergenEdit
        val deleteButtonView: ImageButton = binding.allergenDelete

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}