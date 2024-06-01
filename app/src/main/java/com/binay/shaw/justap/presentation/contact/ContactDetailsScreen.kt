package com.binay.shaw.justap.presentation.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.binay.shaw.justap.presentation.navigation.LocalNavHost

@Composable
fun ContactDetailsScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val navController = LocalNavHost.current

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .then(modifier)
        ) {
            Text(text = "Contact Details")
        }
    }
}