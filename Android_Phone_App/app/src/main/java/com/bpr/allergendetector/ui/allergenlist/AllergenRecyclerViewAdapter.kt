package com.bpr.allergendetector.ui.allergenlist

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.AddAllergenItemBinding

import com.bpr.allergendetector.databinding.AllergenItemBinding
import com.bpr.allergendetector.ui.UiText

class AllergenRecyclerViewAdapter(
    private var values: ArrayList<Allergen>
) : RecyclerView.Adapter<AllergenRecyclerViewAdapter.ViewHolder>() {

    var buttonClickListener: ButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            AllergenItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        // start numbering from 1
        val displayedPosition = position + 1
        holder.idView.text = displayedPosition.toString()
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

        //Edit button implementation
        holder.editButtonView.setOnClickListener {
            val allergenItem = values[position]
            val editItemBinding = AddAllergenItemBinding.inflate(
                LayoutInflater.from(holder.editButtonView.context),
                null,
                false
            )
            val name = editItemBinding.allergenName
            val severity = editItemBinding.severitySeekBar
            val title = editItemBinding.addAllergenTitle
            name.setText(allergenItem.name)
            severity.progress = allergenItem.severity - 1
            title.text = UiText.StringResource(R.string.edit_allergen)
                .asString(holder.editButtonView.context)

            AlertDialog.Builder(holder.editButtonView.context)
                .setView(editItemBinding.root)
                .setPositiveButton("Ok") { dialog, _ ->
                    allergenItem.name = name.text.toString()
                    allergenItem.severity = severity.progress.toString().toInt() + 1

                    if (allergenItem.name.isNotEmpty()) {
                        buttonClickListener?.onEditButtonClicked(allergenItem)
                    } else {
                        AlertDialog.Builder(holder.editButtonView.context)
                            .setTitle("Empty name")
                            .setMessage("Please enter a name for the allergen")
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }

                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }

        //Remove button implementation
        holder.deleteButtonView.setOnClickListener {
            AlertDialog.Builder(holder.deleteButtonView.context)
                .setTitle("Remove ${item.name}?")
                .setMessage("Are you sure that you want to do this?")
                .setPositiveButton("Yes") { dialog, _ ->
                    buttonClickListener?.onDeleteButtonClicked(item)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: AllergenItemBinding) :
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

    interface ButtonClickListener {
        fun onEditButtonClicked(allergen: Allergen)
        fun onDeleteButtonClicked(allergen: Allergen)
    }

    fun updateData(newData: List<Allergen>) {
        values = newData as ArrayList<Allergen>
        notifyDataSetChanged()
    }
}