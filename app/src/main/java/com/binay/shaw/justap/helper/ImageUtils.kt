package com.binay.shaw.justap.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.view.LayoutInflater
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentImagePreviewBinding
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream


object ImageUtils {

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        // Convert the Bitmap object to a byte array
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun getRoundedCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val widthLight = if (bitmap.width > 274) 274 else bitmap.width
        val heightLight = if (bitmap.height > 274) 274 else bitmap.height
        Util.log("Wid: $widthLight hit: $heightLight")
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paintColor = Paint()
        paintColor.flags = Paint.ANTI_ALIAS_FLAG
        val rectF = RectF(Rect(0, 0, widthLight, heightLight))
        canvas.drawRoundRect(
            rectF,
            (widthLight / 2).toFloat(),
            (heightLight / 2).toFloat(),
            paintColor
        )
        val paintImage = Paint()
        paintImage.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        // Create a rect object to define the bounds of the original bitmap
        val rect = Rect(0, 0, widthLight, heightLight)
        canvas.drawBitmap(bitmap, rect, rect, paintImage)
        return output
    }

    fun showImagePreviewDialog(
        context: Context,
        isProfilePreview: Boolean,
        imageUrl: String?,
        isFromAboutMe: Boolean,
        dialogTitle: String? = null
    ): Dialog {
        // Inflate the dialog layout using ViewBinding
        val binding = FragmentImagePreviewBinding.inflate(LayoutInflater.from(context))

        // Load the image into the ImageView using Glide
        if (imageUrl.isNullOrEmpty().not())
            Glide.with(context).load(imageUrl).into(binding.imagePreviewLayout)
        else {
            if (isFromAboutMe) {
                if (isProfilePreview)
                    binding.imagePreviewLayout.setImageResource(R.drawable.aboutme_pfp)
                else
                    binding.imagePreviewLayout.setImageResource(R.drawable.aboutme_banner)
            } else {
                if (isProfilePreview)
                    binding.imagePreviewLayout.setImageResource(R.drawable.default_user)
                else
                    binding.imagePreviewLayout.setImageResource(R.drawable.default_banner)
            }
        }

        // Create a new instance of the dialog builder
        val builder = AlertDialog.Builder(context)

        // Set the dialog layout using ViewBinding
        builder.setView(binding.root)

        // Set the dialog title if provided
        if (!dialogTitle.isNullOrEmpty()) {
            builder.setTitle(dialogTitle)
        }

        // Create and return the dialog
        return builder.create()
    }

    fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is VectorDrawable -> {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
            else -> null
        }
    }
}