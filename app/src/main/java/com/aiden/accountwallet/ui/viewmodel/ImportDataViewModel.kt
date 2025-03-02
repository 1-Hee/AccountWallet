package com.aiden.accountwallet.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.dto.AlertInfo

class ImportDataViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------

    private val _alertInfo: ObservableField<AlertInfo> = ObservableField()
    private val _statusMsg: ObservableField<String> = ObservableField()
    private val _statusColor: ObservableField<Int> = ObservableField()
    private val _backupFileName: ObservableField<String> = ObservableField()
    private val _selectFileUri: ObservableField<Uri> = ObservableField()
    private val _isImportStart: ObservableBoolean = ObservableBoolean(false)

    fun initVariables(context: Context) {
        this._alertInfo.set(AlertInfo(""))
        this._statusMsg.set(context.getString(R.string.txt_init_progress))
        this._statusColor.set(ContextCompat.getColor(context, R.color.mono300))
        this._backupFileName.set("")
        this._isImportStart.set(false)
    }


    // * ------------------------------------------------
    // *    ViewModel's Getter
    // * ------------------------------------------------
    val alertInfo: ObservableField<AlertInfo> get() = _alertInfo
    val statusMsg: ObservableField<String> get() = _statusMsg
    val statusColor: ObservableField<Int> get() = _statusColor
    val backupFileName: ObservableField<String> get() = _backupFileName
    val selectFileUri: ObservableField<Uri> get() = _selectFileUri
    val isImportStart: ObservableBoolean get() = _isImportStart


    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------

    fun setAlertInfo(alertInfo: AlertInfo){
        this._alertInfo.set(alertInfo)
    }

    fun setStatusMsg(msg:String){
        this._statusMsg.set(msg)
    }

    fun setStatusColor(color:Int){
        this._statusColor.set(color)
    }

    fun setBackupFileName(fileName: String){
        this._backupFileName.set(fileName)
    }

    fun setSelectFileUri(uri: Uri){
        this._selectFileUri.set(uri)
    }

    fun setIsImportStart(flag:Boolean){
        this._isImportStart.set(flag)
    }


}