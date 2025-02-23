package com.aiden.accountwallet.util

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.aiden.accountwallet.R

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

    fun getColor(context: Context, tagColor:String):Int {
        val color: Int = tagColor.let {
            try {
                Color.parseColor(it)
            } catch (e: IllegalArgumentException) {
                try {
                    val defStr: String =
                        context.getString(R.string.def_tag_color)
                    Color.parseColor(defStr)
                } catch (e: IllegalArgumentException) {
                    Color.GRAY
                }
            }
        }
        return color
    }

    fun getColor(tagColor:String):Int {
        val color: Int = tagColor.let {
            try {
                Color.parseColor(it)
            } catch (e: IllegalArgumentException) {
                Color.GRAY
            }
        }
        return color
    }

    fun getContrastingTextColor(context: Context, hexColor: String): Int {
        // HEX 코드에서 RGB 값을 추출
        val color = Color.parseColor(hexColor)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        // 밝기(Luminance) 계산
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b)

        // 밝으면 검은색(#000000), 어두우면 흰색(#FFFFFF)
        val colorBlack:Int = context.getColor(R.color.mono800)
        val colorWhite:Int = context.getColor(R.color.white)
        return if (luminance >= 128) colorBlack else colorWhite
    }
}