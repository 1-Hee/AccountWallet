package com.aiden.accountwallet.data.dto

import android.graphics.drawable.Drawable

data class Permission(
    val name:String,
    val guideText:String,
    val iconDrawable: Drawable?,
    var isAllowed:Boolean = false,
)
