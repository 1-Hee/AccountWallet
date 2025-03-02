package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------

    private val _isStopImportTask: ObservableBoolean = ObservableBoolean(false)
    private val _isStopExportTask: ObservableBoolean = ObservableBoolean(false)

    fun initVariables() {
        this._isStopImportTask.set(false)
        this._isStopExportTask.set(false)
    }

    // * ------------------------------------------------
    // *    ViewModel's Getter
    // * ------------------------------------------------
    val isStopImportTask: ObservableBoolean get() = _isStopImportTask
    val isStopExportTask: ObservableBoolean get() = _isStopExportTask

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------

    fun setIsStopImportTask(flag:Boolean){
        this._isStopImportTask.set(flag)
    }

    fun setIsStopExportTask(flag:Boolean){
        this._isStopExportTask.set(flag)
    }

}