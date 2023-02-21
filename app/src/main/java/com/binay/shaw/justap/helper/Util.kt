package com.binay.shaw.justap.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import com.binay.shaw.justap.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Created by binay on 29,December,2022
 */
class Util {
    companion object {

        const val resumeURL: String =
            "https://binayshaw7777.github.io/BinayShaw.github.io/Binay%20Shaw%20CSE%2024.pdf"

        var userID: String = "0"

        var unusedAccounts = mutableListOf<String>()

        fun log(message: String) {
            Log.d("", message)
        }

        fun getByteFromUrl(context: Context, url: String): ByteArray? {
            val drawable = getDrawableFromUrl(context, url)
            log("Drawable is: $drawable")
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                return outputStream.toByteArray()
            }
            return null
        }

        private fun getDrawableFromUrl(context: Context, url: String): Drawable? {
            return try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()

                BitmapDrawable(context.resources, bitmap)
            } catch (e: Exception) {
                null
            }
        }

        fun getFirstName(fullName: String): String {
            if (fullName.isNotEmpty())
                return fullName.split(" ")[0]
            return ""
        }

        fun isUserLoggedIn(auth: FirebaseAuth): Boolean {
            return if (auth.currentUser == null) {
                log("You are not logged in")
                false
            } else {
                log("You are logged in!")
                true
            }
        }

        @SuppressLint("ServiceCast")
        fun checkForInternet(context: Context): Boolean {

            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val network = connectivityManager.activeNetwork ?: return false

                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
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
                .into(imageView)
        }

        fun ImageView.loadImagesWithGlideExt(url: String) {
            Glide.with(this)
                .load(url)
                .centerCrop()
                .error(R.drawable.default_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(this)
        }

        /** Status values
         * 0 - Default
         * 1 - name is empty
         * 2 - email is empty
         * 3 - email is not valid
         * 4 - password is empty
         * 5 - password is less than 8 characters
         * 6 - password must contains Uppercase, lowercase and symbols
         * 7 - success
         * */
        fun validateUserAuthInput(name: String?, email: String, password: String): Int {

            if (name != null)
                if (name.isEmpty())
                    return 1

            if (email.isEmpty())
                return 2
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return 3
            else if (password.isEmpty())
                return 4
            else if (password.length < 8)
                return 5
            else if (!isValidPassword(password))
                return 6
            return 7
        }

        private fun isValidPassword(password: String): Boolean {
            if (password.length < 8) return false
            if (password.firstOrNull { it.isDigit() } == null) return false
            if (password.filter { it.isLetter() }
                    .firstOrNull { it.isUpperCase() } == null) return false
            if (password.filter { it.isLetter() }
                    .firstOrNull { it.isLowerCase() } == null) return false
            if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false

            return true
        }

        fun Context.createBottomSheet(): BottomSheetDialog {
            return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        }

        fun Activity.createBottomSheet(): BottomSheetDialog {
            return BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        }

        fun View.setBottomSheet(bottomSheet: BottomSheetDialog) {
            bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheet.setContentView(this)
            bottomSheet.create()
            bottomSheet.show()
        }


        fun getImageDrawableFromAccountName(accountName: String): Int {

            val map = HashMap<String, Int>()
            map["Phone"] = R.drawable.phone
            map["Email"] = R.drawable.email
            map["Instagram"] = R.drawable.instagram
            map["LinkedIn"] = R.drawable.linkedin
            map["Facebook"] = R.drawable.facebook
            map["Twitter"] = R.drawable.twitter
            map["YouTube"] = R.drawable.youtube
            map["Snapchat"] = R.drawable.snapchat
            map["Twitch"] = R.drawable.twitch
            map["Website"] = R.drawable.website
            map["Discord"] = R.drawable.discord
            map["LinkTree"] = R.drawable.linktree
            map["Custom Link"] = R.drawable.custom_link
            map["Telegram"] = R.drawable.telegram
            map["Spotify"] = R.drawable.spotify
            map["WhatsApp"] = R.drawable.whatsapp

            return map[accountName]!!

        }

    }
}