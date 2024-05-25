package com.binay.shaw.justap.presentation.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.binay.shaw.justap.presentation.navigation.LocalNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectScannerScreen(
    modifier: Modifier = Modifier
) {

    val navController = LocalNavHost.current

    Scaffold(
        topBar = {
             TopAppBar(
                 title = {},
                 navigationIcon = {
                     IconButton(onClick = { navController.popBackStack() }) {
                         Icon(
                             imageVector = Icons.Rounded.ArrowBack,
                             contentDescription = "Back"
                         )
                     }
                 },
                 colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                .then(modifier),
            contentAlignment = Alignment.Center
        ) {

        }
    }
}