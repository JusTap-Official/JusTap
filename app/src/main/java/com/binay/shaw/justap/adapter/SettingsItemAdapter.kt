package com.binay.shaw.justap.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by binay on 02,January,2023
 */
class SettingsItemAdapter(
    val context: Context,
    private val settingsItemList: ArrayList<SettingsItem>
) :
    RecyclerView.Adapter<SettingsItemAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var switch: SwitchCompat
        lateinit var sharedPreferences: SharedPreferences
        var icon: ImageView
        var id: Int = 0
        lateinit var activity: Activity

        init {
            itemName = itemView.findViewById(R.id.settingsItemName)
            switch = itemView.findViewById(R.id.settingsSwitch)
            icon = itemView.findViewById(R.id.settingsItemIcon)
            id = itemView.id

            switch.setOnClickListener {
                switchTheme()
            }

            itemView.rootView.setOnClickListener {
                when (id) {
                    0 -> {
                        Toast.makeText(it.context, "Edit Profile", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        Toast.makeText(it.context, "Customize QR", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        Toast.makeText(it.context, "About us", Toast.LENGTH_SHORT).show()
                    }
                    4 -> {
//                        Toast.makeText(it.context, "Log out", Toast.LENGTH_SHORT).show()
                        logout()
                    }
                }

            }

        }

        private fun logout() {
            val context = itemView.rootView.context
            AwesomeDialog.build(activity)
                .title(
                    "Logout", ResourcesCompat.getFont(context, R.font.roboto_medium),
                    ContextCompat.getColor(context, R.color.text_color)
                )
                .body(
                    "Are you sure you want to logout?",
                    ResourcesCompat.getFont(context, R.font.roboto),
                    ContextCompat.getColor(context, R.color.text_color)
                )
                .background(R.drawable.card_drawable)
                .onPositive(
                    "Logout",
                    R.color.bg_color,
                    ContextCompat.getColor(context, R.color.negative_red)
                ) {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, SignIn_Screen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(
                        intent
                    ).also { activity.finish() }
                    Util.log("positive")
                }
                .onNegative(
                    "Cancel",
                    R.color.bg_color,
                    ContextCompat.getColor(context, R.color.text_color)
                ) {
                    Toast.makeText(context, "Logout cancelled", Toast.LENGTH_SHORT).show()
                    Util.log("negative ")
                }

        }

        private fun switchTheme() {
            sharedPreferences =
                switch.context.getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("FIRST_TIME", false)
            if (Util.isDarkMode(switch.context)) {
                editor.putBoolean("DARK_MODE", false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                editor.putBoolean("DARK_MODE", true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            editor.apply()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.settings_item, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val newList = settingsItemList[position]
        holder.itemName.text = newList.itemName
        if (newList.isSwitchOn) {
            holder.switch.visibility = View.VISIBLE
            if (Util.isDarkMode(holder.switch.context)) {
                holder.switch.isChecked = true
            }
        }
        holder.icon.setImageResource(newList.drawableInt)
        holder.id = position
        if (position == 4)
            holder.activity = newList.activity!!
    }

    override fun getItemCount(): Int {
        return settingsItemList.size
    }
}