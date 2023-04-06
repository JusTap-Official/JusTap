package com.binay.shaw.justap.model

import android.graphics.drawable.Drawable

class CustomizeQROptions(
    val itemName: String,
    val drawable: Drawable
)

enum class CustomizeQRItems(int: Int) {
    PRIMARY_COLOR(0),
    SECONDARY_COLOR(1),
    ADD_IMAGE(2),
//    CHANGE_SHAPE(3);
    RESET(3);

    companion object {
        fun getOptionState(state: CustomizeQRItems) : Int {
            return when (state) {
                PRIMARY_COLOR -> 0
                SECONDARY_COLOR -> 1
                ADD_IMAGE -> 2
//                CHANGE_SHAPE -> 3
                RESET -> 3
            }
        }
    }

}
