package com.binay.shaw.justap.ui.mainScreens.settingsScreen.customize_qr

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.binay.shaw.justap.R
import com.binay.shaw.justap.adapter.CustomizeQRAdapter
import com.binay.shaw.justap.base.BaseFragment
import com.binay.shaw.justap.base.ViewModelFactory
import com.binay.shaw.justap.databinding.FragmentCustomizeQRBinding
import com.binay.shaw.justap.helper.Constants
import com.binay.shaw.justap.helper.ImageUtils
import com.binay.shaw.justap.helper.Logger
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.dpToPx
import com.binay.shaw.justap.model.CustomizeQRItems
import com.binay.shaw.justap.model.CustomizeQROptions
import com.binay.shaw.justap.model.QRCode
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.*


class CustomizeQRFragment : BaseFragment() {

    private var _binding: FragmentCustomizeQRBinding? = null
    private val binding get() = _binding!!
    private val args: CustomizeQRFragmentArgs by navArgs()
    private lateinit var adapter: CustomizeQRAdapter
    private val customizeQRViewModel by viewModels<CustomizeQRViewModel> { ViewModelFactory() }

    private lateinit var sharedPreference: SharedPreferences
    private var profileBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBitmap = args.profilePicture
        sharedPreference =
            requireContext().getSharedPreferences(Constants.qrPref, Context.MODE_PRIVATE)
    }

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

            saveQrSettings.setOnClickListener {
                saveColors()
            }

            saveResetSettings.setOnClickListener {
                resetChanges()
            }
        }
    }

    private fun resetChanges() {
        customizeQRViewModel.run {
            val qrObject = qrObjectLiveData.value!!
            qrObject.primaryColor = defaultPrimaryColor.value!!
            qrObject.secondaryColor = defaultSecondaryColor.value!!
            qrObject.overlay = defaultOverlay.value!!
            qrObject.isCircular = false
            qrReset.value = true
            setupQRObject(qrObject)
        }
    }


//            if (byteString != null) {
//                val byteArray = android.util.Base64.decode(byteString, android.util.Base64.DEFAULT)
//                // use the byteArray as needed
//                currentOverlay = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//            }


    private fun saveColors() {
        val qrObject = customizeQRViewModel.qrObjectLiveData.value!!
        val savedPrimaryColor = customizeQRViewModel.savedQRPrimaryColor.value!!
        val savedSecondaryColor = customizeQRViewModel.savedQRSecondaryColor.value!!
        val defaultOverlay = customizeQRViewModel.defaultOverlay.value!!
        val savedIsCircular = customizeQRViewModel.savedQRisCircular.value!!
        val resetColor = customizeQRViewModel.qrReset.value!!

        val changesAreOkay =
            checkTheQR(
                qrObject,
                savedPrimaryColor,
                savedSecondaryColor,
                resetColor,
                defaultOverlay,
                savedIsCircular
            )
        if (changesAreOkay.not()) {
            return
        }

        saveInSharedPrefs(qrObject)

        showAlerter(
            resources.getString(R.string.changesSaved),
            resources.getString(R.string.changesSavedDescription),
            ContextCompat.getColor(requireContext(), R.color.positive),
            R.drawable.check,
            2500L
        )

        findNavController().popBackStack()
    }

    private fun saveInSharedPrefs(qrObject: QRCode) {
        val editor = sharedPreference.edit()
        val byteString: String = android.util.Base64.encodeToString(
            ImageUtils.bitmapToByteArray(qrObject.overlay!!),
            android.util.Base64.DEFAULT
        )
        editor.putInt(Constants.firstColor, qrObject.primaryColor)
        editor.putInt(Constants.secondColor, qrObject.secondaryColor)
        editor.putString(Constants.image_pref, byteString)
        editor.putBoolean(Constants.isQRCodeCircular, qrObject.isCircular)
        editor.apply()
    }

    private fun checkTheQR(
        qrObject: QRCode,
        savedPrimaryColor: Int,
        savedSecondaryColor: Int,
        isChangesReset: Boolean,
        defaultOverlay: Bitmap,
        savedIsCircular: Boolean
    ): Boolean {
        try {
            val contrastRatio = 1.5f
            val contrastVar1 =
                ColorUtils.calculateContrast(qrObject.primaryColor, qrObject.secondaryColor)
            val contrastVar2 =
                ColorUtils.calculateContrast(qrObject.primaryColor, qrObject.secondaryColor)
            Logger.debugLog("Contrast is: $contrastVar1 and $contrastVar2")

            if (contrastVar1 > contrastRatio || contrastVar2 > contrastRatio) {

                Logger.debugLog("Contrast Choice is Okay")
                Logger.debugLog("First Colors is same: ${qrObject.primaryColor == savedPrimaryColor}")
                Logger.debugLog("Second Colors is same: ${qrObject.secondaryColor == savedSecondaryColor}")
                Logger.debugLog("Overlay is same: ${qrObject.overlay == defaultOverlay}")
                Logger.debugLog("isCircular is same: ${qrObject.isCircular == savedIsCircular}")

                return if (Util.colorIsNotTheSame(qrObject.primaryColor, savedPrimaryColor)
                    || Util.colorIsNotTheSame(qrObject.secondaryColor, savedSecondaryColor)
                    || isChangesReset || qrObject.overlay!!.sameAs(defaultOverlay).not() ||
                    qrObject.isCircular != savedIsCircular
                ) {
                    true
                } else {
                    Toast.makeText(requireContext(), getString(R.string.no_changes_made), Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } else {
                showAlerter(
                    resources.getString(R.string.badContrast),
                    resources.getString(R.string.badContrastDescription),
                    ContextCompat.getColor(requireContext(), R.color.negative),
                    R.drawable.warning,
                    2000L
                )
                Logger.debugLog("Contrast choice is bad")
                return false
            }
        } catch (e: java.lang.IllegalArgumentException) {
            Util.log("Exception: $e")
            showAlerter(
                resources.getString(R.string.anErrorOccurred),
                e.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    .substring(36),
                ContextCompat.getColor(requireContext(), R.color.negative),
                R.drawable.warning,
                2500L
            )
            return false
        }
    }

    private fun initViews() {
        binding.run {
            include.apply {
                toolbarTitle.text = getString(R.string.customizeQR)
                leftIcon.visibility = View.VISIBLE
            }

            setUpRecyclerView()
        }
    }

    private fun setUpRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.customizeOptionsRecyclerView.layoutManager = layoutManager

        adapter = CustomizeQRAdapter(
            requireContext()
        ) {
            when (it) {
                CustomizeQRItems.getOptionState(CustomizeQRItems.PRIMARY_COLOR) -> {
                    pickColor(true)
                }
                CustomizeQRItems.getOptionState(CustomizeQRItems.SECONDARY_COLOR) -> {
                    pickColor(false)
                }
                CustomizeQRItems.getOptionState(CustomizeQRItems.ADD_IMAGE) -> {

                }
                CustomizeQRItems.getOptionState(CustomizeQRItems.CHANGE_SHAPE) -> {
                    customizeQRViewModel.run {
                        val qrObject = qrObjectLiveData.value!!
                        qrObject.isCircular = qrObject.isCircular.not()
                        setupQRObject(qrObject)
                    }
                }
            }
        }

        val options = listOf(
            CustomizeQROptions(
                "Primary Color",
                ResourcesCompat.getDrawable(resources, R.drawable.colors_icon, null)!!
            ),
            CustomizeQROptions(
                "Secondary Color",
                ResourcesCompat.getDrawable(resources, R.drawable.colors_icon, null)!!
            ),
            CustomizeQROptions(
                "Add Overlay",
                ResourcesCompat.getDrawable(resources, R.drawable.add_image_icon, null)!!
            ),
            CustomizeQROptions(
                "Change Shape",
                ResourcesCompat.getDrawable(resources, R.drawable.shape_icon, null)!!
            ),
        )
        adapter.setData(options)

        binding.customizeOptionsRecyclerView.adapter = adapter
    }

    private fun pickColor(isPrimaryColor: Boolean) {
        try {
            val builder = ColorPickerDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.chooseColorForForeground))
                .setPositiveButton(
                    resources.getString(R.string.SaveChanges),
                    ColorEnvelopeListener { envelope, _ ->
                        customizeQRViewModel.run {
                            val qrObject = qrObjectLiveData.value!!
                            if (isPrimaryColor) {
                                qrObject.primaryColor = envelope.color
                            } else {
                                qrObject.secondaryColor = envelope.color
                            }
                            setupQRObject(qrObject)
                        }
                    }
                )
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.colorPickerView.flagView =
                BubbleFlag(requireContext()).apply { flagMode = FlagMode.FADE }
            builder.show()
        } catch (e: java.lang.IllegalArgumentException) {
            Logger.debugLog("Exception: $e")
            showAlerter(
                resources.getString(R.string.anErrorOccurred),
                e.toString()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                    .substring(36),
                ContextCompat.getColor(requireContext(), R.color.negative),
                R.drawable.warning,
                2000L
            )
        }
    }

    private fun initObservers() {
        customizeQRViewModel.run {
            val displayMetrics = resources.displayMetrics
            defaultOverlay.value = ContextCompat.getDrawable(requireContext(), R.drawable.logo)
                ?.toBitmap(72.dpToPx(), 72.dpToPx())!!

            val savedPrimaryColor = sharedPreference.getInt(
                Constants.firstColor,
                ResourcesCompat.getColor(resources, R.color.qr_code_primary, null)
            )
            savedQRPrimaryColor.value = savedPrimaryColor

            val savedSecondaryColor = sharedPreference.getInt(
                Constants.secondColor,
                ResourcesCompat.getColor(resources, R.color.qr_code_secondary, null)
            )
            savedQRSecondaryColor.value = savedSecondaryColor

            val isCircular = sharedPreference.getBoolean(Constants.isQRCodeCircular, false)
            savedQRisCircular.value = isCircular

            val qrObject = QRCode(
                displayMetrics =
                displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels),
                primaryColor = savedPrimaryColor,
                secondaryColor = savedSecondaryColor,
                overlay = defaultOverlay.value,
                isCircular = isCircular
            )

            setupQRObject(qrObject)

            defaultPrimaryColor.value = ResourcesCompat.getColor(
                resources,
                R.color.text_color,
                null
            )
            defaultSecondaryColor.value = ResourcesCompat.getColor(
                resources,
                R.color.white_and_black,
                null
            )

            qrResultLiveData.observe(viewLifecycleOwner) {
                binding.qrCodePreview.setImageBitmap(it)
            }

            errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}