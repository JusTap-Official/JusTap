package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily
import timber.log.Timber

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    imagePainter: Painter = rememberAsyncImagePainter(model = R.drawable.empty_state),
    imageDescription: String = stringResource(R.string.empty_state),
    imageSize: Dp = 200.dp,
    text: String = stringResource(R.string.quickly_share_your_contacts_by_adding_them),
    showTextButton: Boolean = false,
    textButtonText: String = "Add an account",
    onTextButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize().then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = imagePainter,
            contentDescription = imageDescription,
            modifier = Modifier.heightIn(min = imageSize, max = imageSize),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = DMSansFontFamily,
            modifier = Modifier.fillMaxWidth(0.7f),
            textAlign = TextAlign.Center
        )

        if (showTextButton) {
            TextButton(
                onClick = {
                    onTextButtonClick()
                }
            ) {
                Text(
                    text = textButtonText,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}