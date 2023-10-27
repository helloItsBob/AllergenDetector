package com.bpr.allergendetector.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R

class ProfileButtonAdapter(
    private val buttonData: List<String>,
    private val navController: NavController
) :
    RecyclerView.Adapter<ProfileButtonAdapter.ButtonViewHolder>() {

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.profileButton)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ButtonViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.profile_button_item, viewGroup, false)
        return ButtonViewHolder(view)
    }

    //TODO: Add navigation for other buttons later
    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val buttonText = buttonData[position]
        holder.button.text = buttonText
        holder.button.setOnClickListener {
            when (position) {
                0 -> {
                    navController.navigate(R.id.navigation_allergen_list) //TODO: Add navigation
                }

                else -> {
                    Toast.makeText(
                        holder.button.context,
                        "You clicked on $buttonText and it doesn't do anything yet.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return buttonData.size
    }
}
