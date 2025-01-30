package com.aiden.accountwallet.util

import java.text.SimpleDateFormat
import java.util.Locale

object TimeParser {

    const val DATE_HOUR_MIN_FORMAT = 0
    const val DATE_TIME_FORMAT = 1
    const val DATE_FORMAT = 2

    fun getSimpleDateFormat(
        type:Int = DATE_HOUR_MIN_FORMAT, locale: Locale = Locale.getDefault()
    ) : SimpleDateFormat {
        return when(type){
            2 -> {
                SimpleDateFormat("yyyy-MM-dd", locale)
            }
            1 -> {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
            }
            else -> {
                SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
            }
        }
    }
}