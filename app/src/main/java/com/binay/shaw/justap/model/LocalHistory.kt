package com.binay.shaw.justap.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

/**
 * Created by binay on 10,February,2023
 */

@Entity(tableName = "historyDB")
data class LocalHistory(
    @PrimaryKey(autoGenerate = false)
    var userID: String,
    var username: String,
    var userBio: String?,
    @TypeConverters(Converters::class)
    var profileImage: Bitmap?
)

class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, outputStream)
            return outputStream.toByteArray()
        }
        return null
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray != null)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        else null
    }
}
