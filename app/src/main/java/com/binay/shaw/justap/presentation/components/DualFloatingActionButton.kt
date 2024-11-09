package com.binay.shaw.justap.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * A dual floating action button with a small button on top and a large button with text on the bottom.
 *
 * @param smallFloatingActionButtonIcon The icon for the small floating action button.
 * @param largeFloatingActionButtonIcon The icon for the large floating action button.
 * @param smallFloatingActionButtonContentDescription The content description for the small floating action button.
 * @param largeFloatingActionButtonContentDescription The content description for the large floating action button.
 * @param largeFloatingActionButtonContentText The text to display on the large floating action button.
 * @param onClick The callback to be invoked when the small floating action button is clicked.
 * @param modifier The modifier to be applied to the dual floating action button.
 * @param onClickLarge The callback to be invoked when the large floating action button is clicked.
 *
 * Sample:
 *  ```
 *  DualFloatingActionButton(
 *      smallFloatingActionButtonIcon = Icons.Rounded.Download,
 *      largeFloatingActionButtonIcon = Icons.Rounded.QrCodeScanner,
 *      smallFloatingActionButtonContentDescription = "Download
 *      largeFloatingActionButtonContentDescription = "QR Scanner",
 *      largeFloatingActionButtonContentText = "QR Scanner",
 *      onClick = { },
 *      onClickLarge = { }
 *  )
 *  ```
 */
@Composable
fun DualFloatingActionButton(
    smallFloatingActionButtonIcon: ImageVector,
    largeFloatingActionButtonIcon: ImageVector,
    smallFloatingActionButtonContentDescription: String,
    largeFloatingActionButtonContentDescription: String,
    largeFloatingActionButtonContentText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClickLarge: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.then(modifier)
    ) {
        SmallFloatingActionButton(
            onClick = { onClick() },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = smallFloatingActionButtonIcon,
                contentDescription = smallFloatingActionButtonContentDescription,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExtendedFloatingActionButton(
            text = {
                Text(
                    text = largeFloatingActionButtonContentText,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            icon = {
                Icon(
                    imageVector = largeFloatingActionButtonIcon,
                    contentDescription = largeFloatingActionButtonContentDescription,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            onClick = { onClickLarge() },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    }
}