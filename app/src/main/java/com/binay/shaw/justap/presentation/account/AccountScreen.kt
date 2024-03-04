package com.binay.shaw.justap.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.binay.shaw.justap.BuildConfig
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.components.OptionItem
import com.binay.shaw.justap.presentation.components.ThemeDialog
import com.binay.shaw.justap.presentation.themes.medium14
import com.binay.shaw.justap.presentation.themes.medium16
import com.binay.shaw.justap.presentation.themes.normal14

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier
) {

    var openThemeDialog by remember { mutableStateOf(false) }

    when {
        openThemeDialog -> {
            ThemeDialog(onDismissRequest = { openThemeDialog = false })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .then(modifier)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.aboutme_pfp)
                    .size(coil.size.Size.ORIGINAL) // Set the target size to load the image at.
                    .build(), contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Column {
                Text(text = "John Doe", style = medium16)
                Text(
                    text = "johndoe123@gmail.com",
                    style = normal14.copy(color = MaterialTheme.colorScheme.surfaceTint)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Account",
                    style = medium14.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, bottom = 18.dp)
                )
            }
            items(AccountOptions.entries.subList(0, 3)) {
                OptionItem(it) { onClickOption ->
                    when (onClickOption) {
                        AccountOptions.EDIT_PROFILE -> {

                        }

                        AccountOptions.CUSTOMIZE_QR -> {

                        }

                        AccountOptions.THEME -> {
                            openThemeDialog = true
                        }

                        else -> {}
                    }
                }
            }
            item {
                Text(
                    text = "General",
                    style = medium14.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 18.dp, bottom = 18.dp)
                )
            }
            items(AccountOptions.entries.subList(3, AccountOptions.entries.size)) {
                OptionItem(it) { onClickOption ->
                    when (onClickOption) {
                        AccountOptions.INVITE_FRIENDS -> {

                        }

                        AccountOptions.LANGUAGE -> {

                        }

                        AccountOptions.PRIVACY_POLICY -> {

                        }

                        AccountOptions.RATE_US -> {

                        }

                        AccountOptions.HELP_AND_SUPPORT -> {

                        }

                        AccountOptions.LOGOUT -> {

                        }

                        else -> {}
                    }
                }
            }
            item {
                Text(
                    text = "App Version: ${BuildConfig.VERSION_NAME}",
                    style = normal14.copy(color = MaterialTheme.colorScheme.surfaceTint)
                )
            }
        }
    }
}


@Preview
@Composable
private fun AccountScreenPreview() {
    AccountScreen()
}