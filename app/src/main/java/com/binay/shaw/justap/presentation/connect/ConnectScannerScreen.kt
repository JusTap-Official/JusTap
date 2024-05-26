package com.binay.shaw.justap.presentation.connect

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.components.CameraPreview
import com.binay.shaw.justap.presentation.navigation.LocalNavHost
import com.binay.shaw.justap.presentation.sharedViewModels.PermissionViewModel
import com.binay.shaw.justap.presentation.themes.medium18
import com.binay.shaw.justap.presentation.themes.normal16
import com.binay.shaw.justap.utilities.PermissionsUtil
import com.binay.shaw.justap.utilities.Util.findActivity
import com.binay.shaw.justap.utilities.composeUtils.CameraPermissionTextProvider
import com.binay.shaw.justap.utilities.composeUtils.OnLifecycleEvent
import com.binay.shaw.justap.utilities.composeUtils.PermissionDialog
import com.binay.shaw.justap.utilities.onClick
import com.binay.shaw.justap.utilities.openAppSettings
import timber.log.Timber

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun ConnectScannerScreen(
    modifier: Modifier = Modifier,
    viewModel: PermissionViewModel = hiltViewModel()
) {

    val navController = LocalNavHost.current

    val context = LocalContext.current

    var chargerId by remember { mutableStateOf("") }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var isPermissionGranted by remember { mutableStateOf(false) }

    val dialogQueue = viewModel.visiblePermissionDialogQueue

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            Timber.d("Is granted on result: $isGranted")
            isPermissionGranted = isGranted

            viewModel.onPermissionResult(
                permission = Manifest.permission.CAMERA,
                isGranted = isGranted
            )
        }
    )

    LaunchedEffect(key1 = Unit) {
        cameraPermissionResultLauncher.launch(
            Manifest.permission.CAMERA
        )
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (PermissionsUtil.checkPermissions(context, PermissionsUtil.cameraPermissions)) {
                    isPermissionGranted = true
                }
            }

            Lifecycle.Event.ON_PAUSE -> {
                viewModel.dismissDialog()
            }

            else -> {}
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) { _ ->

        CameraPreview(
            torchStatus = isTorchEnabled,
            modifier = Modifier.fillMaxSize(),
        ) { data ->
            chargerId = data
            Timber.d("Data in QR Scanner is: $data")
        }

        dialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permissionTextProvider = when (permission) {
                        Manifest.permission.CAMERA -> {
                            CameraPermissionTextProvider()
                        }

                        else -> return@forEach
                    },
                    isPermanentlyDeclined = !context.findActivity()
                        ?.shouldShowRequestPermissionRationale(
                            permission
                        )!!,
                    onDismiss = viewModel::dismissDialog,
                    onOkClick = {
                        Timber.d("On OK Click")
                        cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                        viewModel.dismissDialog()
                    },
                    onGoToAppSettingsClick = { context.findActivity()?.openAppSettings() }
                )
            }


        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(38.dp)
                        .onClick { navController.popBackStack() },
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.cancel),
                    colorFilter = ColorFilter.tint(Color.White)
                )

                Spacer(modifier = Modifier.weight(1f))

                if (isPermissionGranted) {
                    Image(
                        modifier = Modifier
                            .size(38.dp)
                            .onClick {
                                isTorchEnabled = !isTorchEnabled
                            },
                        imageVector = if (isTorchEnabled) {
                            Icons.Default.FlashOn
                        } else Icons.Default.FlashOff,
                        contentDescription = stringResource(R.string.toggle_torch),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(108.dp))

            if (isPermissionGranted) {
                Box(
                    modifier = Modifier
                        .size(324.dp)
                        .border(
                            width = 3.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                )

            } else {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        modifier = Modifier.size(128.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                    Text(
                        text = "Camera Permission Required",
                        style = medium18.copy(Color.White)
                    )
                    Button(
                        onClick = {
                            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text(
                            text = "Grant Permission",
                            style = normal16.copy(color = Color.Black)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}