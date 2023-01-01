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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.databinding.FragmentSettingsBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.ui.authentication.SignIn_Screen
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private lateinit var _binding: FragmentSettingsBinding
    private val binding get() = _binding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        binding.logout.setOnClickListener {
            Util.showDialog(requireContext(), "Logout", "Are you sure you want to logout?", "Logout", "Cancel", { _, it ->
                auth.signOut()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), SignIn_Screen::class.java)).also { requireActivity().finish() }
            }, { _, it ->
                Toast.makeText(requireContext(), "Logout cancelled", Toast.LENGTH_SHORT).show()
            })
        }


        if (Util.isDarkMode(requireContext())) {
            binding.UIModeSwitch.isChecked = true
        }

        binding.UIModeSwitch.setOnTouchListener{
                _, event -> event.actionMasked == MotionEvent.ACTION_MOVE
        }

        binding.UIModeSwitch.setOnClickListener {
            switchTheme()
        }
        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(com.binay.shaw.justap.R.id.toolbar_title)?.text = "Settings"
        auth = FirebaseAuth.getInstance()

    }

    private fun switchTheme() {
        sharedPreferences = requireContext().getSharedPreferences("ThemeHandler", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("FIRST_TIME", false)
        if (Util.isDarkMode(requireContext())) {
            editor.putBoolean("DARK_MODE", false)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            editor.putBoolean("DARK_MODE", true)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        editor.apply()
    }
}