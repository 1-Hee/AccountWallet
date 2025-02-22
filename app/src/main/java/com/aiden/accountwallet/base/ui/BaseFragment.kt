package com.aiden.accountwallet.base.ui

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.base.bind.DataBindingFragment

abstract class BaseFragment<D:ViewDataBinding>: DataBindingFragment<D>() {

    // ViewModel Provider의 정의
    private var mFragmentProvider:ViewModelProvider? = null

    private var mApplicationProvider:ViewModelProvider? = null

    // Fragment 수준의 Provider
    protected fun<T: ViewModel> getFragmentScopeViewModel(modelCLass:Class<T>):T{
        if(mFragmentProvider == null){
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelCLass]
    }

    // ViewModel을 Factory를 통해 Provider를 얻어야 할 경우
    // 지우기!
    protected fun<T:ViewModel> getFragmentScopeViewModel(modelClass: Class<T>, factory:ViewModelProvider.NewInstanceFactory):T{
        if(mFragmentProvider == null){
            mFragmentProvider = ViewModelProvider(this, factory)
        }
        return mFragmentProvider!![modelClass]
    }

    // Application 수준의 Provider
    protected fun<T:ViewModel> getApplicationScopeViewModel(modelClass: Class<T>):T{
        if(mApplicationProvider==null){
            mApplicationProvider = ViewModelProvider(requireActivity())
        }
        return mApplicationProvider!![modelClass]
    }

    // ViewModel 중 팩토리를 통해서 생성해야 할 경우 사용할 수 있도록 메서드 오버로딩
    protected fun<T:ViewModel> getApplicationScopeViewModel(modelClass: Class<T>, factory: ViewModelProvider.NewInstanceFactory):T{
        if(mApplicationProvider == null){
            mApplicationProvider = ViewModelProvider(requireActivity(), factory)
        }
        return mApplicationProvider!![modelClass]
    }

    /**
     * 화면 이동을 위한 NavController
     */
    protected fun nav():NavController{
        return NavHostFragment.findNavController(this)
    }
}

