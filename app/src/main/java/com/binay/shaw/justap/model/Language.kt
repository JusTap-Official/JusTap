package com.binay.shaw.justap.model

import androidx.annotation.DrawableRes


data class Language(
    val languageName: String,
    val languageId: String,
    @DrawableRes val icon: Int
)
