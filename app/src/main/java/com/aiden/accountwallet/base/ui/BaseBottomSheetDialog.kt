package com.aiden.accountwallet.base.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aiden.accountwallet.base.bind.DataBottomSheetDialog

abstract class BaseBottomSheetDialog<D:ViewDataBinding> : DataBottomSheetDialog<D>() {

    // define ViewModel Provider
    private var mDialogProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null

    // Fragment Scope Provider
    protected fun<T: ViewModel> getDialogScopeViewModel(modelClass: Class<T>):T {
        if(this.mDialogProvider == null){
            this.mDialogProvider = ViewModelProvider(this)
        }
        return mDialogProvider!![modelClass]
    }

    // Application Scope Provider
    protected fun<T: ViewModel> getApplicationScopeViewModel(modelClass: Class<T>):T {
        if(this.mApplicationProvider == null){
            this.mApplicationProvider = ViewModelProvider(requireActivity())
        }
        return mApplicationProvider!![modelClass]
    }

    protected fun<T: ViewModel> getApplicationScopeViewModel(
        modelClass: Class<T>,
        factory: ViewModelProvider.Factory
    ) : T {
        if(mApplicationProvider == null){
            this.mApplicationProvider = ViewModelProvider(requireActivity(), factory)
        }
        return mApplicationProvider!![modelClass]
    }

}