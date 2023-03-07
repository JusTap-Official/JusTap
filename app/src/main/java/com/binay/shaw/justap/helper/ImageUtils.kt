package com.binay.shaw.justap.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.binay.shaw.justap.R
import com.binay.shaw.justap.databinding.FragmentImagePreviewBinding
import com.bumptech.glide.Glide


object ImageUtils {

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
}