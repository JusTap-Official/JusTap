package com.binay.shaw.justap.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.SettingsItemBinding
import com.binay.shaw.justap.model.SettingsItem


class SettingsItemAdapter(
    val context: Context,
    private val settingsItemList: ArrayList<SettingsItem>,
    private val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<SettingsItemAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(val binding: SettingsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            SettingsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val newList = settingsItemList[position]
        holder.binding.settingsItemName.apply {
            text = newList.itemName
            if (newList.itemID == 6) {
                setTextColor(ResourcesCompat.getColor(resources, R.color.negative_red, null))
                setTypeface(null, Typeface.BOLD)
            }
        }

        holder.binding.root.setOnClickListener {
            listener(position)
        }
        holder.binding.settingsItemIcon.setImageResource(newList.drawableInt)
    }

    override fun getItemCount() = settingsItemList.size
}