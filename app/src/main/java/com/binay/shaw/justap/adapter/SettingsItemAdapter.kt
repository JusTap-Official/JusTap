package com.binay.shaw.justap.adapter

import android.content.Context
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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.SettingsItem

/**
 * Created by binay on 02,January,2023
 */
class SettingsItemAdapter(val context: Context, val settingsItemList: ArrayList<SettingsItem>) :
    RecyclerView.Adapter<SettingsItemAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var itemName: TextView
        lateinit var switch :SwitchCompat
        lateinit var sharedPreferences: SharedPreferences
        lateinit var icon: ImageView
        var id: Int = 0

        init {
            itemName = itemView.findViewById(R.id.settingsItemName)
            switch = itemView.findViewById(R.id.settingsSwitch)
            icon = itemView.findViewById(R.id.settingsItemIcon)
            id = itemView.id

            switch.setOnTouchListener { _, event ->
                event.actionMasked == MotionEvent.ACTION_MOVE
            }

            switch.setOnClickListener {
                switchTheme()
            }

            itemView.setOnClickListener {
                when(id) {
                    1 -> {
                        Toast.makeText(itemView.context, "Edit profile", Toast.LENGTH_SHORT).show()
                    } 3 -> {
                        Toast.makeText(itemView.context, "Customize QR", Toast.LENGTH_SHORT).show()
                    } 4 -> {
                        Toast.makeText(itemView.context, "About us", Toast.LENGTH_SHORT).show()
                    } 5 -> {
                        Toast.makeText(itemView.context, "Logout", Toast.LENGTH_SHORT).show()
                    }
                }
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
        val view  = inflater.inflate(R.layout.settings_item,parent,false)
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
    }

    override fun getItemCount(): Int {
        return settingsItemList.size
    }
}