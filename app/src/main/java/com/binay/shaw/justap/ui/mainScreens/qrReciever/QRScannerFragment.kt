package com.binay.shaw.justap.ui.mainScreens.qrReciever

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.binay.shaw.justap.MainActivity
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentQRScannerBinding
import com.binay.shaw.justap.databinding.ParagraphModalBinding
import com.binay.shaw.justap.helper.Encryption
import com.binay.shaw.justap.helper.Util.Companion.createBottomSheet
import com.binay.shaw.justap.helper.Util.Companion.setBottomSheet
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScannerFragment : Fragment() {

    companion object {
        private const val PERMISSION_CAMERA_REQUEST = 1
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }


    private lateinit var pvScan: androidx.camera.view.PreviewView
    private lateinit var scanResultTextView: TextView
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { pvScan.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    private var _binding: FragmentQRScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbarTitle: TextView
    private lateinit var toolBarButton: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        initialization(container)

        toolBarButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        binding.scannerInfo.setOnClickListener {
            val dialog = ParagraphModalBinding.inflate(layoutInflater)
            val bottomSheet = requireActivity().createBottomSheet()
            dialog.apply {
                paragraphHeading.text = requireContext().resources.getString(R.string.scanner_info_title)
                paragraphContent.text = requireContext().resources.getString(R.string.scanner_info_content)
            }
            dialog.root.setBottomSheet(bottomSheet)
        }

        setupCamera()

        return binding.root
    }

    private fun initialization(container: ViewGroup?) {
        _binding = FragmentQRScannerBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).supportActionBar?.hide()
        toolbarTitle = binding.root.findViewById(R.id.toolbar_title)
        toolbarTitle.text = requireContext().resources.getString(R.string.Scanner)
        toolBarButton = binding.root.findViewById(R.id.leftIcon)
        toolBarButton.visibility = View.VISIBLE
        scanResultTextView = binding.scanResultTextView
        pvScan = binding.scanPreview

    }


    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener(
            {
                try {
                    cameraProvider = cameraProviderFuture.get()
                    if (isCameraPermissionGranted()) {
                        bindCameraUseCases()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA_REQUEST
                            )
                        }
                    }
                } catch (e: ExecutionException) {
                    // Handle any errors (including cancellation) here.
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                } catch (e: InterruptedException) {
                    Log.e("QrScanViewModel", "Unhandled exception", e)
                }
            }, ContextCompat.getMainExecutor(requireActivity())
        )
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider?.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation).build()

        previewUseCase?.setSurfaceProvider(pvScan.surfaceProvider)

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(this, it, previewUseCase)
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    private fun bindAnalyseUseCase() {
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider?.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(pvScan.display.rotation).build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        try {
            cameraSelector?.let {
                cameraProvider?.bindToLifecycle(/* lifecycleOwner = */this, it, analysisUseCase
                )
            }
        } catch (_: IllegalStateException) {
        } catch (_: IllegalArgumentException) {
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner, imageProxy: ImageProxy
    ) {
        if (imageProxy.image == null) return
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
                val barcode = barcodes.getOrNull(0)
                barcode?.rawValue?.let { code ->
                    cameraProvider?.unbindAll()

                    val vibratorService = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(500)
                    val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))
                    val decrypted = encryption.decryptOrNull(code)

                    val bundle = Bundle()
                    bundle.putString("decryptedString", decrypted)
                    findNavController().navigate(R.id.resultFragment, bundle)
                }
            }.addOnFailureListener {

            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                setupCamera()
            } else {

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean = this.let {
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
    } == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}