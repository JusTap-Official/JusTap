package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.SettingsItemAdapter
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.SettingsItem
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var settingsItemList:ArrayList<SettingsItem>
    private lateinit var settingsItemAdapter: SettingsItemAdapter

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        /**set List*/
        settingsItemList = ArrayList()

        settingsItemList.add(SettingsItem(1, R.drawable.edit_icon,"Edit profile", false))
        settingsItemList.add(SettingsItem(2, R.drawable.moon,"Dark mode", true))
        settingsItemList.add(SettingsItem(3, R.drawable.scanner_fullscale_icon,"Customize QR", false))
        settingsItemList.add(SettingsItem(4, R.drawable.info_icon, "About us", false))
        settingsItemList.add(SettingsItem(5, R.drawable.logout_icon,"Log out", false))
        /**set find Id*/
        recyclerView = binding.settingsRV
        /**set Adapter*/
        settingsItemAdapter = SettingsItemAdapter(requireContext(), settingsItemList)
        /**setRecycler view Adapter*/
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = settingsItemAdapter





        binding.toProfile.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_settings_to_profileFragment)
        }

        binding.logout.setOnClickListener {
            showDialog()
        }

        return binding.root
    }

    private fun showDialog() {
        AwesomeDialog.build(requireActivity())
            .title(
                "Logout", ResourcesCompat.getFont(requireContext(), R.font.roboto_medium),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .body(
                "Are you sure you want to logout?",
                ResourcesCompat.getFont(requireContext(), R.font.roboto),
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
            .background(R.drawable.card_drawable)
            .onPositive(
                "Logout",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.negative_red)
            ) {
                auth.signOut()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        requireContext(),
                        SignIn_Screen::class.java
                    )
                ).also { requireActivity().finish() }
                Util.log("positive")
            }
            .onNegative(
                "Cancel",
                R.color.bg_color,
                ContextCompat.getColor(requireContext(), R.color.text_color)
            ) {
                Toast.makeText(requireContext(), "Logout cancelled", Toast.LENGTH_SHORT).show()
                Util.log("negative ")
            }

    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "Settings"
        auth = FirebaseAuth.getInstance()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}