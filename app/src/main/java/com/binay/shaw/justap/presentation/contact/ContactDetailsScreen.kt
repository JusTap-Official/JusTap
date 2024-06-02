package com.binay.shaw.justap.presentation.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.navigation.LocalNavHost
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val navController = LocalNavHost.current

    var contactData by remember { mutableStateOf("") }
    val enableSaveButton by remember { derivedStateOf { contactData.isNotEmpty() } }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Button(
                        onClick = {
                            // Save
                        },
                        enabled = enableSaveButton,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .then(modifier),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.phone)
                        .build(), contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = "Phone",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = DMSansFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "9051427724",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = DMSansFontFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButtonWithLabel(
                    imageVector = Icons.Default.OpenInNew,
                    label = "Open",
                    modifier = Modifier.weight(1f)
                ) {
                    // Handle Open action
                }
                IconButtonWithLabel(
                    imageVector = Icons.Default.Share, label = "Share",
                    modifier = Modifier.weight(1f)
                ) {
                    // Handle Share action
                }
                IconButtonWithLabel(
                    imageVector = Icons.Default.DeleteOutline, label = "Delete",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    textColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    // Handle Delete action
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = contactData,
                onValueChange = { contactData = it },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                placeholder = { Text(text = "Enter contact detail") },
                singleLine = true
            )
        }
    }
}

@Composable
fun IconButtonWithLabel(
    imageVector: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .then(modifier)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(containerColor)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = DMSansFontFamily,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}