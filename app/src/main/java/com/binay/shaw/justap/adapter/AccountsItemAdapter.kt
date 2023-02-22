package com.binay.shaw.justap.adapter

import android.annotation.SuppressLint
import android.app.Activity
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
import com.binay.shaw.justap.databinding.SocialAccountLayoutBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.ui.mainScreens.HomeFragmentDirections
import com.google.android.material.snackbar.Snackbar
import com.tapadoo.alerter.Alerter


private var accountsList: List<Accounts> = ArrayList()
private lateinit var currentAccount: Accounts

class AccountsItemAdapter(
    val context: Context,
    val activity: Activity,
    private val listener: (Accounts) -> Unit
) : RecyclerView.Adapter<AccountsItemAdapter.AccountsViewHolder>() {

    class AccountsViewHolder(val binding: SocialAccountLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        return AccountsViewHolder(
            SocialAccountLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val account = accountsList[position]
        currentAccount = account
        holder.binding.apply {
            accountTitle.text = account.accountName
            accountValue.text = account.accountData

            accountSwitch.apply {
                isChecked = account.showAccount
                setOnTouchListener { _, event ->
                    event.actionMasked == MotionEvent.ACTION_MOVE
                }
                setOnClickListener {
                    if (!Util.checkForInternet(context)) {
                        Alerter.create(activity)
                            .setTitle("No Internet available")
                            .setText("Please make sure you're connected to the Internet")
                            .setBackgroundColorInt(activity.resources.getColor(R.color.negative_red))
                            .setIcon(R.drawable.wifi_off)
                            .setDuration(2000L)
                            .show()
                        holder.binding.accountSwitch.isChecked = account.showAccount
                        return@setOnClickListener
                    }
                    listener(account)
                }
            }

            accountLogo.setImageResource(Util.getImageDrawableFromAccountName(account.accountName))

            root.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeToAddEditFragment(
                    1, Accounts(
                        -1,
                        accountTitle.text.toString(),
                        accountValue.text.toString(),
                        accountSwitch.isEnabled
                    )
                )
                it.findNavController().navigate(action)
            }
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