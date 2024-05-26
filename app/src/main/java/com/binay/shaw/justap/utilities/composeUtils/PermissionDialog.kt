package com.binay.shaw.justap.utilities.composeUtils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.binay.shaw.justap.R
import com.binay.shaw.justap.presentation.themes.DMSansFontFamily

/**
 * Composable function that displays a dialog requesting permission from the user.
 *
 * @param permissionTextProvider An object that provides the text content for the dialog based on the permission type.
 * @param isPermanentlyDeclined A boolean flag indicating whether the user has permanently declined the permission.
 * @param onDismiss A callback function to be invoked when the dialog is dismissed.
 * @param onOkClick A callback function to be invoked when the user clicks the "OK" button.
 * @param icon The icon to be displayed in the dialog. Defaults to a camera icon.
 * @param onGoToAppSettingsClick A callback function to be invoked when the user clicks the "Go to Settings" button.
 */
@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    icon: ImageVector = Icons.Default.PhotoCamera,
    onGoToAppSettingsClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss.invoke() },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettingsClick()
                    } else {
                        onOkClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = permissionTextProvider.getConfirmButtonText(
                        isPermanentlyDeclined = isPermanentlyDeclined
                    ),
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    stringResource(id = R.string.cancel),
                    fontFamily = DMSansFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        title = { Text(text = "Permission required") },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        containerColor = Color.White
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String

    fun getConfirmButtonText(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined camera permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your camera so that your friends " +
                    "can see you in a call."
        }
    }

    override fun getConfirmButtonText(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Go to settings"
        } else {
            "Okay"
        }
    }
}

class GalleryPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined storage permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your storage so that you " +
                    "can choose image from your storage."
        }
    }

    override fun getConfirmButtonText(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Go to settings"
        } else {
            "Okay"
        }
    }
}