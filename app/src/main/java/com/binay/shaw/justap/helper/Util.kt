package com.binay.shaw.justap.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.binay.shaw.justap.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Created by binay on 29,December,2022
 */
class Util {
    companion object {
        fun log(message: String) {
            Log.d("", message)
        }

        fun isDarkMode(context: Context): Boolean {
            return context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        @SuppressLint("ServiceCast")
        fun checkForInternet(context: Context): Boolean {

            // register activity with the connectivity manager service
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // if the android version is equal to M
            // or greater we need to use the
            // NetworkCapabilities to check what type of
            // network has the internet connection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Returns a Network object corresponding to
                // the currently active default data network.
                val network = connectivityManager.activeNetwork ?: return false

                // Representation of the capabilities of an active network.
                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    // Indicates this network uses a Wi-Fi transport,
                    // or WiFi has network connectivity
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                    // Indicates this network uses a Cellular transport. or
                    // Cellular has network connectivity
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                    // else return false
                    else -> false
                }
            } else {
                // if the android version is below M
                @Suppress("DEPRECATION") val networkInfo =
                    connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }


        @Throws(WriterException::class)
        fun String.encodeAsQrCodeBitmap(
            dimension: Int,
            overlayBitmap: Bitmap? = null,
            color1: Int,
            color2: Int
        ): Bitmap? {

            val result: BitMatrix
            try {
                result = MultiFormatWriter().encode(
                    this,
                    BarcodeFormat.QR_CODE,
                    dimension,
                    dimension,
                    hashMapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H)
                )
            } catch (e: IllegalArgumentException) {
                // Unsupported format
                return null
            }

            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = if (result.get(x, y)) color1 else color2
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, dimension, 0, 0, w, h)

            return if (overlayBitmap != null) {
                bitmap.addOverlayToCenter(overlayBitmap)
            } else {
                bitmap
            }
        }

        private fun Bitmap.addOverlayToCenter(overlayBitmap: Bitmap): Bitmap {

            val bitmap2Width = overlayBitmap.width
            val bitmap2Height = overlayBitmap.height
            val marginLeft = (this.width * 0.5 - bitmap2Width * 0.5).toFloat()
            val marginTop = (this.height * 0.5 - bitmap2Height * 0.5).toFloat()
            val canvas = Canvas(this)
            canvas.drawBitmap(this, Matrix(), null)
            canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null)
            return this
        }

        fun Int.dpToPx(): Int {
            return (this * Resources.getSystem().displayMetrics.density).toInt()
        }


        fun saveMediaToStorage(bitmap: Bitmap, context: Context): Boolean {
            var success = false
            val filename = "${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                log("Saved to Photos")
                success = true
            }
            return success
        }

        fun loadImagesWithGlide(imageView: ImageView, url: String) {
            Glide.with(imageView)
                .load(url)
                .centerCrop()
                .error(R.drawable.default_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.movie_loading_animation)
                .into(imageView)
        }

        fun ImageView.loadImagesWithGlideExt(url: String) {
            Glide.with(this)
                .load(url)
                .centerCrop()
                .error(R.drawable.default_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.movie_loading_animation)
                .into(this)
        }

    }
}