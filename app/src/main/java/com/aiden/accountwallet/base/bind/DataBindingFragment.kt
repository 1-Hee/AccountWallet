package com.aiden.accountwallet.base.bind

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class DataBindingFragment<D:ViewDataBinding>:Fragment() {

    protected lateinit var mActivity: AppCompatActivity

    protected lateinit var mBinding: D

    // Fragment 에서 binding해서 사용하고자 하는 Config값 정의
    protected abstract fun getDataBindingConfig(): DataBindingConfig

    // Viewmodel 초기화
    protected abstract fun initViewModel()

    // Fragment 레이아웃 초기화시
    protected abstract fun initView()

    // override onAttach
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Fragment 가 화면에 추가되었을 때, Activity 저장 및 Viewmodel 초기화
        mActivity = context as AppCompatActivity
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // config load
        val dataBindingConfig: DataBindingConfig = getDataBindingConfig()
        // fragment data binding
        val bind: D = DataBindingUtil.inflate(inflater, dataBindingConfig.layout, container, false)
        // set lifecycle owner
        bind.lifecycleOwner = viewLifecycleOwner
        // set view data binding values
        bind.setVariable(dataBindingConfig.vmVariableId, dataBindingConfig.stateViewModel)
        val bindingParams: SparseArray<Any?> = dataBindingConfig.bindingParams
        bindingParams.forEach { key, value ->
            bind.setVariable(key, value)
        }
        mBinding = bind
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // binding release
        mBinding.unbind()
    }
}
