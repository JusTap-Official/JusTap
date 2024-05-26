package com.binay.shaw.justap.presentation.connect

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.components.DualFloatingActionButton
import com.binay.shaw.justap.presentation.components.ProgressDialog
import com.binay.shaw.justap.presentation.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
import com.binay.shaw.justap.presentation.navigation.LocalNavHost
import com.binay.shaw.justap.presentation.navigation.Screens
import com.binay.shaw.justap.utilities.Util.saveBitmapToDevice
import com.binay.shaw.justap.utilities.composeUtils.rememberQrBitmap
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ConnectScreen(
    modifier: Modifier = Modifier,
    viewModel: QRGeneratorViewModel = hiltViewModel()
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val qrSize = (screenWidth.value * 0.75).dp
    val context = LocalContext.current
    val captureController = rememberCaptureController()
    var isSavingQrCode by remember { mutableStateOf(false) }
    val navController = LocalNavHost.current


    val userId by viewModel.userId.collectAsState()
    val bitmap = rememberQrBitmap(content = userId, size = qrSize)

    LaunchedEffect(Unit) {
        viewModel.fetchFirebaseUserId()
    }

    if (isSavingQrCode) {
        ProgressDialog {}
    }

    LaunchedEffect(isSavingQrCode) {
        if (isSavingQrCode) {
            val bitmapAsync = captureController.captureAsync()
            try {
                val saveBitmap = bitmapAsync.await().asAndroidBitmap()
                saveBitmapToDevice(bitmap = saveBitmap, context = context)
                isSavingQrCode = false
                Toast.makeText(
                    context,
                    context.getString(R.string.qr_code_saved_to_device), Toast.LENGTH_SHORT
                ).show()
            } catch (error: Throwable) {
                isSavingQrCode = false
                Toast.makeText(
                    context,
                    context.getString(R.string.error_occurred_while_saving_qr_code),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            DualFloatingActionButton(
                smallFloatingActionButtonIcon = Icons.Rounded.Download,
                largeFloatingActionButtonIcon = Icons.Rounded.QrCodeScanner,
                smallFloatingActionButtonContentDescription = stringResource(R.string.download_button),
                largeFloatingActionButtonContentDescription = stringResource(R.string.qr_scanner_button),
                largeFloatingActionButtonContentText = stringResource(R.string.qr_scanner),
                onClick = { isSavingQrCode = true },
                onClickLarge = { navController.navigate(Screens.ConnectScannerScreen.name) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .capturable(captureController)
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {

            if (bitmap != null) {
                Image(
                    painter = remember(bitmap) { BitmapPainter(bitmap.asImageBitmap()) },
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(qrSize),
                )
            } else {
                CircularProgressIndicator()
            }

        }
    }
}