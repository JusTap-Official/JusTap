package com.binay.shaw.justap.ui.mainScreens.qrScreens.qrGeneratorScreen

import android.graphics.Bitmap
import android.util.DisplayMetrics
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binay.shaw.justap.helper.Encryption
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.encodeAsQrCodeBitmap
import com.binay.shaw.justap.helper.roundedQRGenerator


class QRGeneratorViewModel : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val bitmap = MutableLiveData<Bitmap?>()

    init {
        bitmap.value = null
    }

    /*
    * 0 - Default
    * 1 - Successful
    * 2 - Error
    */

    fun generateQR(
        displayMetrics: DisplayMetrics,
        overlay: Bitmap?,
        color1: Int,
        color2: Int,
        isCircular: Boolean? = false
    ) {
        val message = Util.userID

        val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))

        val encrypted = encryption.encryptOrNull(message)
        Util.log("Encrypted Key $encrypted")
        try {

            val size = displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels)

            if (isCircular == true) {
                bitmap.value = encrypted.roundedQRGenerator(size, overlay, color1, color2)
            } else {
                bitmap.value = encrypted.encodeAsQrCodeBitmap(size, overlay, color1, color2)
            }

        } catch (e: Exception) {
            errorMessage.value = e.message.toString()
        }
    }

}