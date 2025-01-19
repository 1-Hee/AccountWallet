package com.aiden.accountwallet.base.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aiden.accountwallet.base.bind.DataBindingActivity
import io.reactivex.rxjava3.annotations.NonNull
import timber.log.Timber

abstract class BaseActivity<D:ViewDataBinding> : DataBindingActivity<D>() {

    // Viewmodel Provider 정의
    private var mApplicationProvider:ViewModelProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
    }

    /**
     * Application 간 사용되는 ViewModel Provider
     * @param modelClass
     * @return
     * @param <T>
     */
    protected fun <T: ViewModel> getApplicationScopeViewModel(@NonNull modelClass:Class<T>):T{
        if(mApplicationProvider == null){
            mApplicationProvider = ViewModelProvider(this.application as BaseApplication)
        }
        return mApplicationProvider!![modelClass]
    }

    // ViewModel을 Factory를 통해 Provider를 얻어야 할 경우
    protected fun<T:AndroidViewModel> getApplicationScopeViewModel(modelClass:Class<T>, factory:ViewModelProvider.NewInstanceFactory):T{
        if(mApplicationProvider == null){
            mApplicationProvider = ViewModelProvider(this, factory)
        }
        return mApplicationProvider!![modelClass]
    }

    @Suppress("DEPRECATION")
    protected fun hideSystemUI(){
        Timber.w("at %s, System UI is hidden....", this.javaClass.simpleName)
        val window: Window = window

        // Android 11(R) 대응
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if(controller != null){
                controller.hide(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else {
            val decorView: View = window.decorView
            // version for Lollipop
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                window.statusBarColor = Color.TRANSPARENT
            }
            val uiOption = View.SYSTEM_UI_FLAG_IMMERSIVE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOption
        }
    }

}