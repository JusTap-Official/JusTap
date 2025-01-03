package com.binay.shaw.justap.domain.model

import android.graphics.Bitmap


data class QRCode(
    var displayMetrics: Int?,
    var primaryColor: Int,
    var secondaryColor: Int,
    var overlay: Bitmap?,
    var isCircular: Boolean = false
)
