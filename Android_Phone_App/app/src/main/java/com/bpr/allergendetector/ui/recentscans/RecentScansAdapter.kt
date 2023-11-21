package com.bpr.allergendetector.ui.recentscans

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.RecentScanItemBinding
import com.bpr.allergendetector.ui.ImageConverter
import com.bpr.allergendetector.ui.SwitchState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class RecentScansAdapter(private var values: List<RecentScan>) :
    RecyclerView.Adapter<RecentScansAdapter.ViewHolder>() {

    var buttonClickListener: ButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecentScanItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class ViewHolder(binding: RecentScanItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val scanNumber: TextView = binding.recentScanNumber
        val scanPicture: ImageView = binding.recentScanImage
        val scanResult: TextView = binding.recentScanResult
        val viewScan: ImageButton = binding.viewRecentScan
    }

    override fun onBindViewHolder(holder: RecentScansAdapter.ViewHolder, position: Int) {
        val item = values[position]
        val actualPosition = position + 1
        holder.scanNumber.text = "$actualPosition"

        val base64String = item.image
        val image = ImageConverter.convertStringToBitmap(base64String)
        Glide.with(holder.scanPicture.context)
            .load(image)
            .apply(RequestOptions().transform(RoundedCorners(16)))
            .into(holder.scanPicture)

        if (item.result == SwitchState.HARMFUL_STATE) {
            holder.scanResult.setTextColor(
                ContextCompat.getColor(
                    holder.scanResult.context,
                    R.color.red
                )
            )
        } else {
            holder.scanResult.setTextColor(
                ContextCompat.getColor(
                    holder.scanResult.context,
                    R.color.green
                )
            )
        }
        holder.scanResult.text = item.result

        holder.viewScan.setOnClickListener {
            buttonClickListener?.onViewScanButtonClick(item)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun updateData(data: List<RecentScan>) {
        values = data as ArrayList<RecentScan>
        notifyDataSetChanged()
    }

    interface ButtonClickListener {
        fun onViewScanButtonClick(recentScan: RecentScan)
    }
}