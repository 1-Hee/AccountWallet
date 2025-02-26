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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class DataBottomSheetDialog<D:ViewDataBinding> : BottomSheetDialogFragment() {

    protected lateinit var mActivity: AppCompatActivity
    protected lateinit var mBinding:D

    // define Dialog Config
    protected abstract fun getDataBindingConfig(): DataBindingConfig

    // ViewModel init
    protected abstract fun intiViewModel()

    // init layout
    protected abstract fun initView()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mActivity = context as AppCompatActivity
        intiViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // config load
        val dbConfig:DataBindingConfig = getDataBindingConfig()
        val bind:D = DataBindingUtil.inflate(
            inflater, dbConfig.layout, container, false
        )
        bind.lifecycleOwner = viewLifecycleOwner
        bind.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        val bindParams:SparseArray<Any?> = dbConfig.bindingParams
        bindParams.forEach { key, value ->
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
        mBinding.unbind()
    }
}