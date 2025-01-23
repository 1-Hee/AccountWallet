package com.aiden.accountwallet.base.bind

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class DataBindDialog<D:ViewDataBinding>:DialogFragment() {

    protected lateinit var mActivity:AppCompatActivity

    protected lateinit var mBinding:D

    // Dialog에서 사용하고자 하는 Config 정의
    protected abstract fun getDataBindingConfig(): DataBindingConfig

    // Viewmodel 초기화
    protected abstract fun initViewModel()

    // 다이얼로그 레이아웃 초기화 시
    protected abstract fun initView()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Dialog가 화면에 추가되었을 때 뷰모델을 추가한다.
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
        val bind:D = DataBindingUtil.inflate(inflater, dataBindingConfig.layout, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경화면 투명
        // set lifecycle owner
        bind.lifecycleOwner = viewLifecycleOwner
        // set additional variables
        bind.setVariable(dataBindingConfig.vmVariableId, dataBindingConfig.stateViewModel)
        val bindingParams:SparseArray<Any?> = dataBindingConfig.bindingParams
        bindingParams.forEach{ key, value ->
            bind.setVariable(key, value)
        }
        mBinding = bind
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // view init
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}