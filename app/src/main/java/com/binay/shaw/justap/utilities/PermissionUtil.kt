package com.binay.shaw.justap.utilities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat


object PermissionsUtil {

    private const val REQUEST_CODE = 1

    val storagePermissions = getStoragePermissions()
    val cameraPermissions = getCameraPermissions()
    val locationPermissions = getLocationPermission()
    val chargerScreenPermissions = getChargerScreenPermission()

    private fun getChargerScreenPermission(): List<String> {
        val permissions = mutableListOf<String>()
        permissions.addAll(locationPermissions)
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        return permissions
    }

    /**
     * Retrieves the location permissions required for accessing fine and coarse location.
     *
     * @return An array of location-related permissions.
     */
    private fun getLocationPermission() = listOf(Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    /**
     * Retrieves the appropriate camera permissions based on the device's API level.
     *
     * On devices with API level higher than Android 9 (P), only the camera permission is included
     * in the array of permissions.
     *
     * On devices with API level lower than or equal to Android 9 (P), both camera, write external storage,
     * and read external storage permissions are included in the array of permissions.
     *
     * @return An array of camera-related permissions based on the device's API level.
     */
    @JvmName("getCameraPermissions1")
    private fun getCameraPermissions(): Array<String> {
        //        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            permissions = arrayOf(Manifest.permission.CAMERA)
//        }
        return arrayOf(
            Manifest.permission.CAMERA
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    /**
     * Retrieves the appropriate storage permissions based on the device's API level.
     *
     * On devices with API level higher than 32 (Android 12 or higher), no explicit storage permissions
     * are required, so an empty array is returned.
     *
     * On devices with API level between Android 9 (P) and 32, only the read external storage permission
     * is included in the array of permissions.
     *
     * On devices with API level lower than Android 9 (P), both read and write external storage permissions
     * are included in the array of permissions.
     *
     * @return An array of storage-related permissions based on the device's API level.
     */
    @JvmName("getStoragePermissions1")
    private fun getStoragePermissions(): Array<String> {
        var permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT > 32) {
            permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return permissions
    }

    /**
     * Checks if the given permissions are granted.
     *
     * @param context The context object representing the current state of the application.
     * @param permissions An array of permission strings that need to be checked.
     * @return Returns true if all the permissions are granted, false otherwise.
     */
    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    fun Context.startSettingAppPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}