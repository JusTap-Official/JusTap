package com.binay.shaw.justap.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.model.Accounts

/**
 * Created by binay on 04,February,2023
 */

private var accountsList: List<Accounts> = ArrayList()
private lateinit var currentAccount: Accounts


class ResultItemAdapter(
    val context: Context
) : RecyclerView.Adapter<ResultItemAdapter.AccountsViewHolder>(){

    class AccountsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountName: TextView
        val accountData: TextView
        val accountsIcon: ImageView
        val showAccount: SwitchCompat

        init {
            accountName = itemView.findViewById(R.id.accountTitle)
            accountData = itemView.findViewById(R.id.accountValue)
            accountsIcon = itemView.findViewById(R.id.accountLogo)
            showAccount = itemView.findViewById(R.id.accountSwitch)

            itemView.rootView.setOnClickListener {
                Toast.makeText(itemView.context, "Toast", Toast.LENGTH_SHORT).show()









                val URLString = ""
                val download = Intent(
                Intent.ACTION_VIEW, Uri.parse(URLString))
                itemView.context.startActivity(download)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.social_account_layout, parent, false)
        return AccountsViewHolder(view)
    }



    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = accountsList[position]
        currentAccount = account
        holder.accountName.text = account.accountName
        holder.accountData.text = account.accountData
        holder.showAccount.isChecked = account.showAccount
        holder.showAccount.visibility = View.GONE
        when (account.accountName) {
            "Phone" -> holder.accountsIcon.setImageResource(R.drawable.phone)
            "Email" -> holder.accountsIcon.setImageResource(R.drawable.email)
            "Instagram" -> holder.accountsIcon.setImageResource(R.drawable.instagram)
            "LinkedIn" -> holder.accountsIcon.setImageResource(R.drawable.linkedin)
            "Facebook" -> holder.accountsIcon.setImageResource(R.drawable.facebook)
            "Twitter" -> holder.accountsIcon.setImageResource(R.drawable.twitter)
            "YouTube" -> holder.accountsIcon.setImageResource(R.drawable.youtube)
            "Snapchat" -> holder.accountsIcon.setImageResource(R.drawable.snapchat)
            "Twitch" -> holder.accountsIcon.setImageResource(R.drawable.twitch)
            "Website" -> holder.accountsIcon.setImageResource(R.drawable.website)
            "Discord" -> holder.accountsIcon.setImageResource(R.drawable.discord)
            "LinkTree" -> holder.accountsIcon.setImageResource(R.drawable.linktree)
            "Custom Link" -> holder.accountsIcon.setImageResource(R.drawable.custom_link)
            "Telegram" -> holder.accountsIcon.setImageResource(R.drawable.telegram)
            "Spotify" -> holder.accountsIcon.setImageResource(R.drawable.spotify)
            "WhatsApp" -> holder.accountsIcon.setImageResource(R.drawable.whatsapp)
        }
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    fun setData(accounts: List<Accounts>) {
        accountsList = accounts
        notifyDataSetChanged()
    }
}