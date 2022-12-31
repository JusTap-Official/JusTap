package com.binay.shaw.justap.ui.mainScreens

import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentQRGeneratorBinding
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.dpToPx
import com.binay.shaw.justap.viewModel.QRGenerator_ViewModel


class QRGeneratorFragment : Fragment() {

    private lateinit var _binding: FragmentQRGeneratorBinding
    private val binding get() = _binding
    private lateinit var viewModel : QRGenerator_ViewModel
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initialization(container)

        viewModel.generateQR(displayMetrics, overlay)

        viewModel.status.observe(viewLifecycleOwner) {
            binding.qrCodePreview.setImageBitmap(viewModel.bitmap.value)
        }

        binding.qrCodePreview.setOnClickListener {
            Util.saveMediaToStorage(viewModel.bitmap.value as Bitmap, requireContext()).also {
                if (it)
                    Toast.makeText(requireContext(), "Saved to photos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.scanQRCode.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_scanner_to_scannerFragment)
        }

        return binding.root
    }

    private fun initialization(container: ViewGroup?) {

        _binding = FragmentQRGeneratorBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        binding.root.findViewById<TextView>(R.id.toolbar_title)?.text = "My QR Code"
        viewModel = ViewModelProvider(this@QRGeneratorFragment)[QRGenerator_ViewModel::class.java]
        displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        overlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo_black_stroke)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())

    }
}