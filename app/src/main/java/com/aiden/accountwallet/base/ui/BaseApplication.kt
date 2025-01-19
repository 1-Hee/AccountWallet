package com.aiden.accountwallet.base.ui

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDexApplication
import timber.log.Timber

class BaseApplication(
    private val mAppViewModelStore: ViewModelStore = ViewModelStore()
) : MultiDexApplication(), ViewModelStoreOwner {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore
}