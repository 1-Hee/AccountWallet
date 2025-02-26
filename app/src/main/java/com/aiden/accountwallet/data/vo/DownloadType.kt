package com.aiden.accountwallet.data.vo

import android.graphics.drawable.Drawable

data class DownloadType(
    val typeIdx:Int = -1,
    val iconDrawable: Drawable?,
    val typeValue:String = ""
)
