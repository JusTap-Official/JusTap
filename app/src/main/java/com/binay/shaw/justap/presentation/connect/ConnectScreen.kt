package com.binay.shaw.justap.presentation.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.mainScreens.qrScreens.qrGeneratorScreen.QRGeneratorViewModel
import com.binay.shaw.justap.presentation.themes.medium20

@Composable
fun ConnectScreen(
    modifier: Modifier = Modifier,
    viewModel: QRGeneratorViewModel = hiltViewModel()
) {

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
            ) {
                SmallFloatingActionButton(
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = stringResource(R.string.download_button),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                ExtendedFloatingActionButton(
                    text = {
                        Text(
                            text = "QR Scanner",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.QrCodeScanner,
                            contentDescription = stringResource(R.string.qr_scanner_button),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },

                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .then(modifier)
        ) {
            Text(
                text = "This is connect screen",
                style = medium20
            )
        }
    }
}