package com.aiden.accountwallet.base.listener

import android.view.KeyEvent
import android.view.View
import androidx.annotation.Nullable

interface OnKeyListener {
    fun onKey (
        @Nullable view:View?,
        keyCode:Int,
        @Nullable keyEvent: KeyEvent?
    ): Boolean
}