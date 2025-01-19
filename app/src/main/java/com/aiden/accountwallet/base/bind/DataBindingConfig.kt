package com.aiden.accountwallet.base.bind

import android.util.SparseArray
import androidx.lifecycle.ViewModel

class DataBindingConfig {
    // layout id
    val layout:Int
    // Data biding id
    val vmVariableId:Int
    // ViewModel
    val stateViewModel:ViewModel?
    // data binding params
    val bindingParams = SparseArray<Any?>()
    constructor(layout:Int){
        this.layout = layout
        vmVariableId = 0
        stateViewModel = null
    }
    constructor(
        layout: Int,
        vmVariableId:Int,
        stateViewModel: ViewModel
    ){
        this.layout = layout
        this.vmVariableId = vmVariableId
        this.stateViewModel = stateViewModel
    }

    /**
     * Data binding 파라미터 추가
     * @param variableId
     * @param object
     * @return
     */
    fun addBindingParam(
        variableId:Int,
        `object`:Any
    ): DataBindingConfig {
        if(bindingParams[variableId] == null) {
            bindingParams.put(variableId, `object`)
        }
        return this
    }
}
