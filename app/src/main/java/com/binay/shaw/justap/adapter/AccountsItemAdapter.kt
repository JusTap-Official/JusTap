package com.binay.shaw.justap.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.ui.mainScreens.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar

/**
 * Created by binay on 30,January,2023
 */
private var accountsList: List<Accounts> = ArrayList()
private lateinit var currentAccount: Accounts

class AccountsItemAdapter(
    val context: Context,
    private val listener: (Accounts) -> Unit
) : RecyclerView.Adapter<AccountsItemAdapter.AccountsViewHolder>() {

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
                val action = HomeFragmentDirections.actionHomeToAddEditFragment(1, Accounts(
                    -1,
                    accountName.text.toString(),
                    accountData.text.toString(),
                    showAccount.isEnabled)
                )
                it.findNavController().navigate(action)
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

        holder.showAccount.setOnTouchListener { _, event ->
                event.actionMasked == MotionEvent.ACTION_MOVE
        }

        holder.showAccount.setOnClickListener {
            if (!Util.checkForInternet(context)) {
                Snackbar.make(holder.itemView.rootView, "No Internet available", Snackbar.LENGTH_SHORT).show()
                holder.showAccount.isChecked = account.showAccount
                return@setOnClickListener
            }
            listener(account)
        }
        holder.accountsIcon.setImageResource(Util.getImageDrawableFromAccountName(account.accountName))
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    fun setData(accounts: List<Accounts>) {
        accountsList = accounts
        notifyDataSetChanged()
    }

}