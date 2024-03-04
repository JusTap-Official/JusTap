package com.binay.shaw.justap.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.sharedViewModels.ThemeViewModel
import com.binay.shaw.justap.presentation.themes.normal16
import com.binay.shaw.justap.presentation.themes.normal24
import com.binay.shaw.justap.utilities.onClick

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDialog(
    onDismissRequest: () -> Unit,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {

    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            Modifier
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_theme),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Change Theme",
                style = normal24.copy(MaterialTheme.colorScheme.onSurface)
            )

            Text(
                text = "Dark Mode",
                modifier = Modifier
                    .onClick { themeViewModel.switchToDarkMode() }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                style = normal16
            )

            Text(
                text = "Light Mode",
                modifier = Modifier
                    .onClick { themeViewModel.switchToLightMode() }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                style = normal16
            )

            Text(
                text = "Dynamic Theme",
                modifier = Modifier
                    .onClick { themeViewModel.switchToDynamicThemeMode() }
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                style = normal16
            )

            TextButton(
                onClick = { onDismissRequest() },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                Text("Dismiss")
            }
        }
    }
}