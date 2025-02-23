package com.aiden.accountwallet.util

import android.util.Log

object Logger {

    private val TAG:String = this::class.java.simpleName;

    fun d(message: String, vararg args: Any?, tag: String = TAG) {
        Log.d(tag, formatMessage(message, *args))
    }

    fun i(message: String, vararg args: Any?, tag: String = TAG) {
        Log.i(tag, formatMessage(message, *args))
    }

    fun e(message: String, vararg args: Any?, tag: String = TAG, throwable: Throwable? = null) {
        Log.e(tag, formatMessage(message, *args), throwable)
    }

    private fun formatMessage(message: String, vararg args: Any?): String {
        return if (args.isNotEmpty()) String.format(message, *args) else message
    }

}