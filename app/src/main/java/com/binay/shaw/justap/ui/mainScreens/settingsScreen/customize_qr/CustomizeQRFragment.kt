package com.binay.shaw.justap.ui.mainScreens.settingsScreen.customize_qr

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.binay.shaw.justap.R
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentCustomizeQRBinding
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.ImageUtils
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
import com.binay.shaw.justap.viewModel.LocalUserViewModel
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.*


class CustomizeQRFragment : BaseFragment() {

    private var _binding: FragmentCustomizeQRBinding? = null
    private val binding get() = _binding!!
    private val localUserViewModel by viewModels<LocalUserViewModel> { ViewModelFactory() }
    private lateinit var user: LocalUser
    private val args: CustomizeQRFragmentArgs by navArgs()
    private lateinit var currentOverlay: Bitmap
    private val qrGeneratorViewModel by viewModels<QRGeneratorViewModel> { ViewModelFactory() }
    private lateinit var displayMetrics: DisplayMetrics
    private lateinit var sharedPreference: SharedPreferences
    private var firstSelectedColor: Int = -1
    private var secondSelectedColor: Int = -1
    private var defaultPrimaryColor: Int = -1
    private var defaultSecondaryColor: Int = -1
    private var profileBitmap: Bitmap? = null
    private var byteString: String? = null
    private var isQRCodeCircular: Boolean = false
    private var originalQRShape: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomizeQRBinding.inflate(layoutInflater, container, false)

        initObservers()
        initViews()
        clickHandlers()

        return binding.root
    }

    private fun clickHandlers() {
        binding.apply {
            include.leftIcon.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            val originalBitmap = currentOverlay

            if (byteString != null) {
                val byteArray = android.util.Base64.decode(byteString, android.util.Base64.DEFAULT)
                // use the byteArray as needed
                currentOverlay = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            }

            val overlayBitmap = currentOverlay

            var isColorReset = false


            if (profileBitmap == null) {
                linearLayoutCompat.visibility = View.INVISIBLE
            }
            if (originalBitmap.sameAs(currentOverlay).not())
                showProfileCheckBox.isChecked = true

            if (isQRCodeCircular) {
                switchQrShapeCheckbox.isChecked = true
            }


            generateQR(firstSelectedColor, secondSelectedColor, currentOverlay, isQRCodeCircular)

            switchQrShapeCheckbox.setOnCheckedChangeListener { _, _ ->
                isQRCodeCircular = isQRCodeCircular.not()
                generateQR(
                    firstSelectedColor, secondSelectedColor,
                    currentOverlay, isQRCodeCircular
                )
            }

            showProfileCheckBox.setOnCheckedChangeListener { _, isClicked ->
                currentOverlay = if (isClicked) profileBitmap!! else originalBitmap
                generateQR(
                    firstSelectedColor, secondSelectedColor,
                    currentOverlay, isQRCodeCircular
                )
            }

            pickColorFor1.setOnClickListener {
                try {
                    val builder = ColorPickerDialog.Builder(requireContext())
                        .setTitle(resources.getString(R.string.chooseColorForForeground))
                        .setPositiveButton(
                            resources.getString(R.string.SaveChanges),
                            ColorEnvelopeListener { envelope, _ ->
                                firstSelectedColor = envelope.color
                                generateQR(
                                    firstSelectedColor,
                                    secondSelectedColor, currentOverlay, isQRCodeCircular
                                )
                            }
                        )
                        .setNegativeButton(
                            resources.getString(R.string.cancel)
                        ) { dialogInterface, _ -> dialogInterface.dismiss() }
                    builder.colorPickerView.flagView =
                        BubbleFlag(requireContext()).apply { flagMode = FlagMode.FADE }
                    builder.show()
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2000L
                    )
                }
            }

            pickColorFor2.setOnClickListener {
                try {

                    val builder = ColorPickerDialog.Builder(requireContext())
                        .setTitle(resources.getString(R.string.chooseColorForBackground))
                        .setPositiveButton(
                            resources.getString(R.string.SaveChanges),
                            ColorEnvelopeListener { envelope, _ ->
                                secondSelectedColor = envelope.color
                                generateQR(
                                    firstSelectedColor,
                                    secondSelectedColor, currentOverlay, isQRCodeCircular
                                )
                            }
                        )
                    BubbleFlag(requireContext()).apply { flagMode = FlagMode.FADE }
                    builder.show()
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2000L
                    )
                }
            }

            resetColors.setOnClickListener {
                generateQR(
                    defaultPrimaryColor, defaultSecondaryColor,
                    overlayBitmap, isQRCodeCircular
                )
                firstSelectedColor = defaultPrimaryColor
                secondSelectedColor = defaultSecondaryColor
                isColorReset = true
            }

            positiveOption.setOnClickListener {
                try {

                    val contrastRatio = 1.5f
                    val contrastVar1 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    val contrastVar2 =
                        ColorUtils.calculateContrast(firstSelectedColor, secondSelectedColor)
                    Util.log("Contrast is: $contrastVar1 and $contrastVar2")

                    if (contrastVar1 > contrastRatio || contrastVar2 > contrastRatio) {
                        Util.log("Contrast Choice is Okay")


                        if (Util.colorIsNotTheSame(firstSelectedColor, defaultPrimaryColor)
                            || Util.colorIsNotTheSame(secondSelectedColor, defaultSecondaryColor)
                            || isColorReset || overlayBitmap.sameAs(currentOverlay).not() ||
                            originalQRShape != isQRCodeCircular
                        ) {

                            saveColors(
                                sharedPreference, firstSelectedColor, secondSelectedColor,
                                currentOverlay, isQRCodeCircular
                            )
                            showAlerter(
                                resources.getString(R.string.changesSaved),
                                resources.getString(R.string.changesSavedDescription),
                                ContextCompat.getColor(requireContext(), R.color.positive_green),
                                R.drawable.check,
                                2500L
                            )
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        } else {
                            Util.log("First Colors are : $firstSelectedColor and $defaultPrimaryColor")
                            Util.log("Second Colors are : $secondSelectedColor and $defaultSecondaryColor")
                            Util.log("Overlay : $currentOverlay")
                            Util.log("isCircular : $isQRCodeCircular")
                            Toast.makeText(requireContext(), "No changes made", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        showAlerter(
                            resources.getString(R.string.badContrast),
                            resources.getString(R.string.badContrastDescription),
                            ContextCompat.getColor(requireContext(), R.color.negative_red),
                            R.drawable.warning,
                            2000L
                        )
                        Util.log("Contrast choice is bad")
                    }
                } catch (e: java.lang.IllegalArgumentException) {
                    Util.log("Exception: $e")
                    showAlerter(
                        resources.getString(R.string.anErrorOccurred),
                        e.toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                            .substring(36),
                        ContextCompat.getColor(requireContext(), R.color.negative_red),
                        R.drawable.warning,
                        2500L
                    )
                }
            }
            negativeOption.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                Util.log("Customization of QR is Cancelled.")
            }
        }
    }

    private fun saveColors(
        sharedPreference: SharedPreferences,
        firstSelectedColor: Int,
        secondSelectedColor: Int,
        overlayBitmap: Bitmap,
        isQRCodeCircular: Boolean
    ) {
        val editor = sharedPreference.edit()
        val byteString: String = android.util.Base64.encodeToString(
            ImageUtils.bitmapToByteArray(overlayBitmap),
            android.util.Base64.DEFAULT
        )
        editor.putInt(Constants.firstColor, firstSelectedColor)
        editor.putInt(Constants.secondColor, secondSelectedColor)
        editor.putString(Constants.image_pref, byteString)
        editor.putBoolean(Constants.isQRCodeCircular, isQRCodeCircular)
        editor.apply()
        Util.log("Saved")
    }

    private fun generateQR(color1: Int, color2: Int, overlay: Bitmap, isShapeCircular: Boolean?) {
        qrGeneratorViewModel.generateQR(
            displayMetrics, overlay,
            color1,
            color2,
            isShapeCircular
        )
    }

    private fun initViews() {
        binding.run {
            include.apply {
                toolbarTitle.text = getString(R.string.customizeQR)
                leftIcon.visibility = View.VISIBLE
            }
        }

        profileBitmap = args.profilePicture

        sharedPreference =
            requireContext().getSharedPreferences(Constants.qrPref, Context.MODE_PRIVATE)
        displayMetrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        firstSelectedColor = sharedPreference.getInt(
            Constants.firstColor,
            ResourcesCompat.getColor(resources, R.color.text_color, null)
        )
        secondSelectedColor = sharedPreference.getInt(
            Constants.secondColor,
            ResourcesCompat.getColor(resources, R.color.bg_color, null)
        )

        defaultPrimaryColor = ResourcesCompat.getColor(
            resources,
            R.color.text_color,
            null
        )
        defaultSecondaryColor = ResourcesCompat.getColor(
            resources,
            R.color.bg_color,
            null
        )

        byteString = sharedPreference.getString(Constants.image_pref, null)
        isQRCodeCircular = sharedPreference.getBoolean(Constants.isQRCodeCircular, false)
        originalQRShape = isQRCodeCircular

        currentOverlay = ContextCompat.getDrawable(requireContext(), R.drawable.logo)
            ?.toBitmap(72.dpToPx(), 72.dpToPx())!!
    }

    private fun initObservers() {
        localUserViewModel.fetchUser.observe(viewLifecycleOwner) {
            user = LocalUser(
                it.userID,
                it.userName,
                it.userEmail,
                it.userBio,
                it.userProfilePicture,
                it.userBannerPicture
            )
        }
        qrGeneratorViewModel.run {
            bitmap.observe(viewLifecycleOwner) {
                binding.qrCodePreview.setImageBitmap(it)
            }
        }
    }
}