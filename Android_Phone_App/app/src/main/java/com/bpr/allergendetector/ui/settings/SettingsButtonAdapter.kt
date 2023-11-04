package com.bpr.allergendetector.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R

class SettingsButtonAdapter(
    private val buttonList: List<String>
) : RecyclerView.Adapter<SettingsButtonAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CustomViewHolder =
        CustomViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.profile_button_item, viewGroup, false)
        )

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.profileButton)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val buttonName = buttonList[position]
        holder.button.text = buttonName
        holder.button.setOnClickListener {
            Toast.makeText(
                holder.button.context,
                "$buttonName button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return buttonList.size
    }
}