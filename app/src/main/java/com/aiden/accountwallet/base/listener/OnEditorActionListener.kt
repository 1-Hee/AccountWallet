package com.aiden.accountwallet.base.listener

import android.view.KeyEvent
import android.widget.TextView

interface OnEditorActionListener {
    fun onEditorAction(view: TextView, actionEvent:Int, keyEvent: KeyEvent): Boolean
}