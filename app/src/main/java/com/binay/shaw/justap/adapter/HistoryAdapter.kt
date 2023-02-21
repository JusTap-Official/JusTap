package com.binay.shaw.justap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.LocalHistory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * Created by binay on 10,February,2023
 */


private var historyList: List<LocalHistory> = ArrayList()
private lateinit var currentUser: LocalHistory

class HistoryAdapter(
    val context: Context,
    private val listener: (LocalHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePreview: TextView
        val pfpPreview: ImageView
        val bioPreview: TextView

        init {
            namePreview = itemView.findViewById(R.id.history_username)
            pfpPreview = itemView.findViewById(R.id.history_userpfp)
            bioPreview = itemView.findViewById(R.id.history_userBio)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.history_item_layout, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentAccount = historyList[position]
        holder.namePreview.text = currentAccount.username.trim()
        currentAccount.userBio?.let {
            if (it.isNotEmpty()) {
                holder.bioPreview.apply {
                    text = it.trim()
                    visibility = View.VISIBLE
                }
            }
        }
        currentAccount.profileImage?.let {
            Glide.with(context)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.pfpPreview)
            val sizeInBytes = it.byteCount
            val sizeInMB = sizeInBytes.toDouble() / (1024 * 1024)
            Util.log("Size of image: $sizeInMB")
        }

        holder.itemView.setOnClickListener {
            listener(currentAccount)
        }

    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun setData(history: List<LocalHistory>) {
        historyList = history
        notifyDataSetChanged()
    }

}