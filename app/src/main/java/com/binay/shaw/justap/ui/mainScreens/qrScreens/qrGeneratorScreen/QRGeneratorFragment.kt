package com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.binay.shaw.justap.ui.mainScreens.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentQRGeneratorBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.createBottomSheet
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.helper.Util.setBottomSheet
import java.util.*

@SuppressLint("ClickableViewAccessibility")
class QRGeneratorFragment : BaseFragment() {

    private var _binding: FragmentQRGeneratorBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<QRGeneratorViewModel> {  ViewModelFactory() }
    private lateinit var displayMetrics: DisplayMetrics
    private var overlay: Bitmap? = null
    private lateinit var sharedPreference: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQRGeneratorBinding.inflate(layoutInflater, container, false)

        initialization()
        initObservers()
        clickHandlers()

        return binding.root
    }

    private fun clickHandlers() {
        binding.apply {
            qrCodePreview.setOnClickListener {
                Toast.makeText(requireContext(), "Long press to save in Gallery", Toast.LENGTH_SHORT).show()
            }

            qrInfo.setOnClickListener {
                val dialog = ParagraphModalBinding.inflate(layoutInflater)
                val bottomSheet = requireActivity().createBottomSheet()
                dialog.apply {
                    paragraphHeading.text = requireContext().resources.getString(R.string.OfflineMode)
                    paragraphContent.text = requireContext().resources.getString(R.string.OfflineModeInfo)
                }
                dialog.root.setBottomSheet(bottomSheet)
            }

            qrCodePreview.setOnLongClickListener {
                Util.saveMediaToStorage(viewModel.bitmap.value as Bitmap, requireContext()).also { status ->
                    if (status) {
                        showAlerter(
                            resources.getString(R.string.saved_successfully),
                            resources.getString(R.string.your_qr_code_is_saved_in_gallery),
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.positive_green
                            ),
                            R.drawable.check,
                            800L
                        )
                    }
                }
            }

            onlineOfflineModeSwitch.setOnTouchListener{
                    _, event -> event.actionMasked == MotionEvent.ACTION_MOVE
            }

            onlineOfflineModeSwitch.setOnClickListener {
                Toast.makeText(requireContext(), resources.getString(R.string.tapped), Toast.LENGTH_SHORT).show()
            }

            scanQRCode.setOnClickListener {
                Navigation.findNavController(binding.root).navigate(R.id.action_scanner_to_scannerFragment)
            }
        }
    }

    private fun initObservers() {

        val firstSelectedColor = sharedPreference.getInt(
            Constants.firstColor,
            ResourcesCompat.getColor(resources, R.color.text_color, null)
        )
        val secondSelectedColor = sharedPreference.getInt(
            Constants.secondColor,
            ResourcesCompat.getColor(resources, R.color.bg_color, null)
        )

        val sharedPreference =
            requireContext().getSharedPreferences(Constants.qrPref, Context.MODE_PRIVATE)

        val byteString = sharedPreference.getString(Constants.image_pref, null)
        if (byteString != null) {
            val byteArray = android.util.Base64.decode(byteString, android.util.Base64.DEFAULT)
            // use the byteArray as needed
            overlay = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        viewModel.run {
            generateQR(displayMetrics, overlay,
                firstSelectedColor,
                secondSelectedColor)

            bitmap.observe(viewLifecycleOwner) {
                binding.qrCodePreview.setImageBitmap(it)
            }

            errorMessage.observe(viewLifecycleOwner) {
                showAlerter(
                    resources.getString(R.string.anErrorOccurred),
                    "",
                    ContextCompat.getColor(requireContext(), R.color.negative_red),
                    R.drawable.warning,
                    2000L
                )
            }
        }
    }

    private fun initialization() {

        (activity as MainActivity).supportActionBar?.hide()
        binding.include.toolbarTitle.text = requireContext().resources.getString(R.string.MyQRCode)
        displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        overlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo_black_stroke)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())
        sharedPreference = requireContext().getSharedPreferences(Constants.qrPref, Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}