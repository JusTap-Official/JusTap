package com.binay.shaw.justap.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts


/**
 * Created by binay on 04,February,2023
 */

private var accountsList: List<Accounts> = ArrayList()
private lateinit var currentAccount: Accounts
private var userName: String = ""
private var userEmail: String = ""


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

                val context = itemView.context
                val data = accountData.text.toString().trim()

                when (accountName.text.toString().trim()) {
                    "Phone" -> {
                        addToContacts(data, context)

                    }
                    "Email" -> {
                        sendEmail(data, context)
                    }
                    "Instagram" -> {
                        val regex = "instagram.com/"
                        if (data.contains(regex) || isLink(data)) {
                            openLink(data, context)
                        } else {
                            val instagramLink = "https://www.instagram.com/$data/"
                            openLink(instagramLink, context)
                        }
                    }
                    "LinkedIn" -> {
                        val regex = "linkedin.com/"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val linkedInLink = "https://www.linkedin.com/in/$data"
                            openLink(linkedInLink, context)
                        }
                    }
                    "Facebook" -> {
                        val regex = "facebook.com"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val faceBookLink = "https://www.facebook.com/$data"
                            openLink(faceBookLink, context)
                        }
                    }
                    "Twitter" -> {
                        val regex = "twitter.com/"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val twitterLink = "https://twitter.com/$data"
                            openLink(twitterLink, context)
                        }
                    }
                    "YouTube" -> {
                        val regex = "youtube.com/"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val youtubeLink = "https://www.youtube.com/$data"
                            openLink(youtubeLink, context)
                        }
                    }
                    "Snapchat" -> {
                        val regex = "snapchat.com/add/"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val twitterLink = "https://www.snapchat.com/add/$data"
                            openLink(twitterLink, context)
                        }
                    }
                    "Twitch" -> {
                        val regex = "https://www.twitch.tv/"
                        if (isLink(data) || data.contains(regex)) {
                            openLink(data, context)
                        } else {
                            val twitchLink = "https://www.twitch.tv/$data"
                            openLink(twitchLink, context)
                        }
                    }
                    "Website" -> {
                        openLink(data, context)
                    }
                    "Discord" -> {
                        openLink(data, context)
                    }
                    "LinkTree" -> {
                        openLink(data, context)
                    }
                    "Custom Link" -> {
                        openLink(data, context)
                    }
                    "Telegram" -> {

                        val isPhoneNumber = data.matches("^[0-9]+\$".toRegex())
                        if (isPhoneNumber) {
                            addTelegramAccountByPhone(data, context)
                        } else {
                            addTelegramAccountByUsername(data, context)
                        }
                    }
                    "Spotify" -> {
                        openLink(data, context)
                    }
                    "WhatsApp" -> {
                        addContactToWhatsApp(data, context)
                    }
                }
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
        holder.accountsIcon.setImageResource(Util.getImageDrawableFromAccountName(account.accountName))
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    fun setData(accounts: List<Accounts>) {
        accountsList = accounts
        notifyDataSetChanged()
    }
    fun setUserData(name: String, email: String) {
        userName = name
        userEmail = email
    }

    companion object {

        fun isLink(data: String) : Boolean {
            if (data.contains("https://") || data.contains("http://"))
                return true
            return false
        }

        fun addTelegramAccountByUsername(username: String, context: Context) {
            val telegramPackageName = "org.telegram.messenger"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tg://resolve?domain=$username")
            intent.setPackage(telegramPackageName)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Open Telegram in browser if the app is not installed
                intent.data = Uri.parse("https://t.me/$username")
                context.startActivity(intent)
            }
        }

        fun addTelegramAccountByPhone(phoneNumber: String, context: Context) {
            val telegramPackageName = "org.telegram.messenger"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tg://add?phone=$phoneNumber")
            intent.setPackage(telegramPackageName)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Open Telegram in browser if the app is not installed
                intent.data = Uri.parse("https://t.me/add_contact/?number=$phoneNumber")
                context.startActivity(intent)
            }
        }

        fun openLink(link: String, context: Context) {
            Util.log("Link Opened is: $link")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        }

        fun addContactToWhatsApp(number: String, context: Context) {
            val whatsAppURL = "https://api.whatsapp.com/send/?phone=$number&text=Hi%20there!&type=phone_number"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsAppURL))
            context.startActivity(intent)
        }

        fun addToContacts(phone: String, context: Context) {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.type = ContactsContract.RawContacts.CONTENT_TYPE
            intent.putExtra(ContactsContract.Intents.Insert.NAME, userName)
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            context.startActivity(intent)
        }
        fun sendEmail(email: String, context: Context) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.setPackage("com.google.android.gm")
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        }
    }
}