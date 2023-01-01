package com.binay.shaw.justap.ui.mainScreens.appSettingsFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import jp.wasabeef.glide.transformations.BlurTransformation


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var toolbarText: TextView
    private lateinit var toolbarBackButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialization(container)

        toolbarBackButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarText = binding.root.findViewById(R.id.toolbar_title)
        toolbarText.text = "Profile"
        toolbarBackButton = binding.root.findViewById(R.id.goBack)
        toolbarBackButton.visibility = View.VISIBLE
        auth = FirebaseAuth.getInstance()

        Glide.with(this).load(R.drawable.default_banner)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(15, 3)))
            .into(binding.profileBannerIV)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}