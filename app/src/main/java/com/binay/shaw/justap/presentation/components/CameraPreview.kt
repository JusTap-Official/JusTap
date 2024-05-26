package com.binay.shaw.justap.presentation.components

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.binay.shaw.justap.utilities.QRCodeAnalyzer
import timber.log.Timber

/**
 * Composable function that displays a camera preview and handles QR code scanning.
 *
 * @param torchStatus Boolean indicating whether the torch should be enabled or disabled.
 * @param modifier [Modifier] to be applied to the camera preview.
 * @param onScanComplete Callback function that is invoked when a QR code is scanned successfully.
 *
 */
@Composable
fun CameraPreview(
    torchStatus: Boolean,
    modifier: Modifier = Modifier,
    onScanComplete: (String) -> Unit
) {

    val lifeCycleOwner = LocalLifecycleOwner.current
    var cameraDevice by remember { mutableStateOf<Camera?>(null) }

    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.Builder().build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            val imageAnalysis = ImageAnalysis.Builder().build()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QRCodeAnalyzer { value ->

                    // Close the camera -> Very Important!
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        cameraProvider.unbindAll()
                        Timber.d("Closed camera")
                    }, ContextCompat.getMainExecutor(context))

                    onScanComplete(value)
                }
            )

            cameraDevice = ProcessCameraProvider.getInstance(context).get().bindToLifecycle(
                lifeCycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
            Timber.d("Torch status preview: $torchStatus")
            cameraDevice!!.cameraControl.enableTorch(torchStatus)

            previewView
        },
        modifier = Modifier.fillMaxSize().then(modifier),
        update = {
            Timber.d("Torch status preview 2: $torchStatus")
            cameraDevice?.cameraControl?.enableTorch(torchStatus)
        }
    )
}