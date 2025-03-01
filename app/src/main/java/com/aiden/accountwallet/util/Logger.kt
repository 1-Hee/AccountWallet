package com.aiden.accountwallet.util

import android.util.Log
import com.aiden.accountwallet.BuildConfig

object Logger {

    private val TAG:String = this::class.java.simpleName;

    fun d(message: String, vararg args: Any?, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, formatMessage(message, *args))
        }
    }

    fun w(message: String, vararg args: Any?, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, formatMessage(message, *args))
        }
    }

    fun i(message: String, vararg args: Any?, tag: String = TAG) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, formatMessage(message, *args))
        }
    }

    fun e(message: String, vararg args: Any?, tag: String = TAG, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, formatMessage(message, *args), throwable)
        }
    }

    private fun formatMessage(message: String, vararg args: Any?): String {
        return if (args.isNotEmpty()) String.format(message, *args) else message
    }

}