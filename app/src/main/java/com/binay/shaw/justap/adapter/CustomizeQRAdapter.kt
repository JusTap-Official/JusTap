package com.binay.shaw.justap.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.LayoutCustomizeQrRecyclerViewItemBinding
import com.binay.shaw.justap.model.CustomizeQROptions

private var options: List<CustomizeQROptions> = ArrayList()

class CustomizeQRAdapter(
    val context: Context,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<CustomizeQRAdapter.ViewHolder>() {

    class ViewHolder(val binding: LayoutCustomizeQrRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutCustomizeQrRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.binding.apply {
            imageView.setImageDrawable(option.drawable)
            titleTextView.text = option.itemName
        }
        holder.itemView.setOnClickListener {
            listener(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(customizeOptions: List<CustomizeQROptions>) {
        options = customizeOptions
        notifyDataSetChanged()
    }

    override fun getItemCount() = options.size
}