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

/**
 * Created by binay on 10,February,2023
 */


private var historyList: List<LocalHistory> = ArrayList()
private lateinit var currentUser: LocalHistory

class HistoryAdapter(
    context: Context,
    private val listener: (LocalHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namePreview: TextView
        val pfpPreview: ImageView

        init {
            namePreview = itemView.findViewById(R.id.history_username)
            pfpPreview = itemView.findViewById(R.id.history_userpfp)
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
        currentAccount.userPFPBase64?.let {
            if (it.isNotEmpty()) {
                val bitmap = Util.base64ToImage(it)
                holder.pfpPreview.setImageBitmap(bitmap)
            }
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