package com.aiden.accountwallet.base.ui

import android.util.Log
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication(
    private val mAppViewModelStore: ViewModelStore = ViewModelStore()
) : MultiDexApplication(), ViewModelStoreOwner {

    override fun onCreate() {
        super.onCreate()
        Log.i(this::class.simpleName, "${this.javaClass.simpleName} init!!!!!!!!!!")
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore
}