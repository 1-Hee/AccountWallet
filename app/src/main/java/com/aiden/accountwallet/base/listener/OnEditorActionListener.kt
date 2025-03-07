package com.aiden.accountwallet.base.listener

import android.view.KeyEvent
import android.widget.TextView
import androidx.annotation.Nullable

interface OnEditorActionListener {
    fun onEditorAction(
        @Nullable view: TextView?,
        actionEvent:Int,
        @Nullable keyEvent: KeyEvent?
    ): Boolean
}