package com.binay.shaw.justap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.databinding.HistoryItemLayoutBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalHistory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


private var historyList: List<LocalHistory> = ArrayList()

class HistoryAdapter(
    val context: Context,
    private val listener: (LocalHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: HistoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            HistoryItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentAccount = historyList[position]
        holder.binding.historyUsername.text = currentAccount.username.trim()
        currentAccount.userBio?.let {
            if (it.isNotEmpty()) {
                holder.binding.historyUserBio.apply {
                    text = it.trim()
                    visibility = View.VISIBLE
                }
            }
        }
        currentAccount.profileImage?.let {
            Glide.with(context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.binding.historyUserpfp)
            val sizeInBytes = it.byteCount
            val sizeInMB = sizeInBytes.toDouble() / (1024 * 1024)
            Util.log("Size of image: $sizeInMB")
        }

        holder.itemView.setOnClickListener {
            listener(currentAccount)
        }

    }

    override fun getItemCount() = historyList.size

    fun setData(history: List<LocalHistory>) {
        historyList = history
        notifyDataSetChanged()
    }
}