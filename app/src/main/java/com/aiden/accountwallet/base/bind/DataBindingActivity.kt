package com.aiden.accountwallet.base.bind

import android.content.res.Configuration
import android.os.Bundle
import android.util.SparseArray
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.aiden.accountwallet.util.Logger

abstract class DataBindingActivity<D:ViewDataBinding>:AppCompatActivity() {

    protected lateinit var  mBinding:D

    // Activity 에서 binding해서 사용하고자 하는 Config값 정의

    protected abstract fun getDataBindingConfig(): DataBindingConfig

    // Activity 초기화
    abstract fun init(savedInstanceState: Bundle?)

    // ViewModel 초기화
    protected open fun initViewModel(){
        Logger.i("initViewModel....")
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        // config load
        val dataBindingConfig: DataBindingConfig = getDataBindingConfig()
        // activity binding
        val bind:D = DataBindingUtil.setContentView(this, dataBindingConfig.layout)
        // set lifecycle owner
        bind.lifecycleOwner = this
        // state ViewModel 대한 값 설정
        bind.setVariable(dataBindingConfig.vmVariableId, dataBindingConfig.stateViewModel)
        val bindingParams:SparseArray<Any?> = dataBindingConfig.bindingParams
        bindingParams.forEach { key, value ->
            bind.setVariable(key, value)
        }
        mBinding = bind
        // Activity 초기화
        init(savedInstanceState)
    }

    override fun onConfigurationChanged(@NonNull newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
        // binding release
        mBinding.unbind()
    }
}

