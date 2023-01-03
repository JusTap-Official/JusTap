package com.binay.shaw.justap.viewModel

import android.graphics.Bitmap
import android.util.DisplayMetrics
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binay.shaw.justap.helper.Encryption
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.helper.Util.Companion.encodeAsQrCodeBitmap

/**
 * Created by binay on 31,December,2022
 */
class QRGenerator_ViewModel : ViewModel() {
    var status = MutableLiveData<Int>()
    private val errorMessage = MutableLiveData<String>()
    var bitmap = MutableLiveData<Bitmap?>()

    init {
        status.value = 0
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
        color2: Int
    ) {
        val message = "binayshaw7777@gmail.com"

        val encryption = Encryption.getDefault("Key", "Salt", ByteArray(16))

        val encrypted = encryption.encryptOrNull(message)
        Util.log("Encrypted Key $encrypted")
        try {

            val size = displayMetrics.widthPixels.coerceAtMost(displayMetrics.heightPixels)

            bitmap.value = encrypted.encodeAsQrCodeBitmap(size, overlay, color1, color2)

            status.value = 1
        } catch (e: Exception) {
            errorMessage.value = e.message.toString()
            status.value = 2
        }
    }

}