package com.bpr.allergendetector.ui.lists

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bpr.allergendetector.databinding.ListItemBinding
import com.bpr.allergendetector.ui.ImageConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.util.Locale

class ListRecyclerViewAdapter(
    private var values: ArrayList<Product>
) : RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>() {

    var buttonClickListener: ButtonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val actualPosition = position + 1
        holder.productNumber.text = "$actualPosition"

        val base64 = item.image
        val image = ImageConverter.convertStringToBitmap(base64)
        Glide.with(holder.productPicture.context)
            .load(image)
            .apply(RequestOptions().transform(RoundedCorners(16)))
            .into(holder.productPicture)

        holder.productName.text = item.name

        holder.viewEditProduct.setOnClickListener {
            buttonClickListener?.onViewEditButtonClick(item)
        }

        holder.deleteProduct.setOnClickListener {
            AlertDialog.Builder(holder.deleteProduct.context)
                .setTitle("Remove ${item.name}?")
                .setMessage("Are you sure that you want remove this item?")
                .setPositiveButton("Yes") { dialog, _ ->
                    buttonClickListener?.onDeleteButtonClick(item)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
                .create()
                .show()
        }
    }

    inner class ViewHolder(binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val productNumber: TextView = binding.number
        val productPicture: ImageView = binding.picture
        val productName: TextView = binding.product
        val viewEditProduct: ImageButton = binding.viewEditProduct
        val deleteProduct: ImageButton = binding.deleteProduct
    }

    interface ButtonClickListener {
        fun onViewEditButtonClick(product: Product)
        fun onDeleteButtonClick(product: Product)
    }

    fun updateData(state: String, data: List<Product>) {
        val filteredList = if (state == "HARMFUL") {
            data.filter { it.harmful }.toMutableList() as ArrayList<Product>
        } else {
            data.filter { !it.harmful }.toMutableList() as ArrayList<Product>
        }
        values = filteredList
        notifyDataSetChanged()
    }

    fun searchFilter(query: String, originalList: List<Product>, state: String) {
        val filteredList = originalList.filter { item ->
            item.name.lowercase(Locale.ROOT).contains(query.lowercase(Locale.ROOT)) ||
            item.tags?.lowercase(Locale.ROOT)?.contains(query.lowercase(Locale.ROOT))!!
        }

        updateData(state, filteredList)
    }
}