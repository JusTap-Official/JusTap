package com.binay.shaw.justap.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.R
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.Accounts
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.mainScreens.SettingsFragmentDirections

/**
 * Created by binay on 02,January,2023
 */
class SettingsItemAdapter(
    val context: Context,
    private val settingsItemList: ArrayList<SettingsItem>,
    private val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<SettingsItemAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemName: TextView
        var parentLayout: ConstraintLayout

        //        var switch: SwitchCompat
        lateinit var sharedPreferences: SharedPreferences
        var icon: ImageView
        var id: Int = 0

        init {
            itemName = itemView.findViewById(R.id.settingsItemName)
//            switch = itemView.findViewById(R.id.settingsSwitch)
            icon = itemView.findViewById(R.id.settingsItemIcon)
            id = itemView.id
            parentLayout = itemView.findViewById(R.id.settingsItemLayout)

//            switch.setOnTouchListener { _, event ->
//                event.actionMasked == MotionEvent.ACTION_MOVE
//            }
//
//            switch.setOnClickListener {
//                switchTheme()
//            }

            /** IDs relation
             * 0 -> Edit profile Fragment
             * 1 -> DarK Mode
             * 2 -> Customize QR
             * 3 -> About us
             * 4 -> Need help?
             * */

//            itemView.rootView.setOnClickListener {
//                when (id) {
//                    0 -> {
//                        Navigation.findNavController(it)
//                            .navigate(R.id.action_settings_to_editProfileFragment)
//                    }
//                    2 -> {
//                        val action = SettingsFragmentDirections.actionSettingsToResultFragment(
//                            resultString = null,
//                            isResult = false
//                        )
//                        Navigation.findNavController(it).navigate(action)
//                    }
//                    3 -> {
//
//                    } 4 -> {
//
//                    }
//                }
//
//            }

        }


//        private fun switchTheme() {
//            sharedPreferences =
//                switch.context.getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            editor.putBoolean("FIRST_TIME", false)
//            if (Util.isDarkMode(switch.context)) {
//                editor.putBoolean("DARK_MODE", false)
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            } else {
//                editor.putBoolean("DARK_MODE", true)
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }
//            editor.apply()
//        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.settings_item, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val newList = settingsItemList[position]
        holder.itemName.text = newList.itemName
        holder.parentLayout.setOnClickListener {
            listener(holder.id)
        }
//        if (newList.isSwitchOn) {
//            holder.switch.visibility = View.VISIBLE
//            if (Util.isDarkMode(holder.switch.context)) {
//                holder.switch.isChecked = true
//            }
//        }
        holder.icon.setImageResource(newList.drawableInt)
        holder.id = position
    }

    override fun getItemCount(): Int {
        return settingsItemList.size
    }

}