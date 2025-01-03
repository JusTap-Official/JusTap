package com.binay.shaw.justap.domain.model

import androidx.annotation.DrawableRes


data class Language(
    val languageName: String,
    val languageId: String,
    @DrawableRes val icon: Int
)
