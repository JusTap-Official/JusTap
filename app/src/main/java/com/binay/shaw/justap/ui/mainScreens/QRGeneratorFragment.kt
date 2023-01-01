package com.binay.shaw.justap.ui.mainScreens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
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

    @SuppressLint("ClickableViewAccessibility")
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
            Toast.makeText(requireContext(), "Long press to save in Gallery", Toast.LENGTH_SHORT).show()
        }

        binding.qrInfo.setOnClickListener {
            Toast.makeText(requireContext(), "Add info here", Toast.LENGTH_SHORT).show()
        }

        binding.qrCodePreview.setOnLongClickListener {
            Util.saveMediaToStorage(viewModel.bitmap.value as Bitmap, requireContext()).also { status ->
                if (status)
                    Toast.makeText(requireContext(), "Saved to photos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.onlineOfflineModeSwitch.setOnTouchListener{
                _, event -> event.actionMasked == MotionEvent.ACTION_MOVE
        }

        binding.onlineOfflineModeSwitch.setOnClickListener {
            Toast.makeText(requireContext(), "Tapped", Toast.LENGTH_SHORT).show()
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