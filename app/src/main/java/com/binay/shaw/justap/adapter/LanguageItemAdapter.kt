package com.binay.shaw.justap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.LanguageItemBinding
import com.binay.shaw.justap.domain.model.Language


class LanguageItemAdapter(
    private val listOfLanguage: List<Language>,
    val onLanguageSelect: (String) -> Unit
) : RecyclerView.Adapter<LanguageItemAdapter.LanguageViewHolder>() {

    class LanguageViewHolder(val binding: LanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageViewHolder {
        return LanguageViewHolder(
            LanguageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = listOfLanguage.size

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentItem = listOfLanguage[position]
        holder.binding.apply {
            languageItemIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    this.root.resources,
                    currentItem.icon,
                    null
                )
            )
            languageItemName.text = currentItem.languageName
            root.setOnClickListener {
                onLanguageSelect(currentItem.languageId)
            }
        }
    }
}