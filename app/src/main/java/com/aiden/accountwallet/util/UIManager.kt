package com.aiden.accountwallet.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object UIManager {

    // 키보드 숨김 메서드
    fun hideKeyPad(activity: Activity) {
        val view: View = activity.currentFocus ?: return
        val isEditText:Boolean = view is EditText
        if (!isEditText) return
        if (view.hasFocus()) {
            view.clearFocus()
        }
        val imm:InputMethodManager = activity.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}