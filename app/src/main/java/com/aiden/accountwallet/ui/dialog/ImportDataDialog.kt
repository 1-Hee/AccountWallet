package com.aiden.accountwallet.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.view.View
import androidx.core.content.ContextCompat
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.BuildConfig
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogImportDataBinding
import java.io.File

class ImportDataDialog(
    val alertInfo: AlertInfo,
    private val listener: OnDialogClickListener
) : BaseDialog<DialogImportDataBinding>(), ViewClickListener {

    interface OnDialogClickListener{
        fun onImport(fileName:String)
        fun onCancel()
    }

    private var mFileName:String = ""

    override fun getDataBindingConfig(): DataBindingConfig {
        val initStr:String = getString(R.string.txt_init_progress)
        return DataBindingConfig(R.layout.dialog_import_data)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.statusMsg, initStr)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        // 파일 탐색기로 읽어오기
        var fileName:String = loadDownloadFiles()
        var statusMsg:String = ""
        var statusColor:Int = 0

        if(fileName.isBlank()){
            fileName = "No Data"
            statusMsg = getString(R.string.status_not_fount_backup)
            statusColor = ContextCompat.getColor(requireContext(), R.color.red400)
        } else {
            this.mFileName = fileName
            val displayPath:String = fileName.substringAfter("/emulated/0")
            fileName = displayPath
            statusMsg = getString(R.string.status_detect_backup)
            statusColor = ContextCompat.getColor(requireContext(), R.color.sky_blue400)
        }

        mBinding.setVariable(BR.statusMsg, statusMsg)
        mBinding.tvSelectStatus.setTextColor(statusColor)
        mBinding.setVariable(BR.backupFileName, fileName)
        mBinding.notifyChange()
    }

    private fun loadDownloadFiles(): String {
        val packageName = BuildConfig.APPLICATION_ID
        val lastSegment = packageName.substringAfterLast(".")
        val mFileName = "_backup_$lastSegment.json"

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, mFileName)

        return if (file.exists()) {
           file.path
        } else {
            ""
        }
    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.l_backup_data -> { // 파일 탐색기 띄우기

            }
            R.id.btn_ok -> {
                listener.onImport(mFileName)
                dismiss()
            }
            R.id.btn_cancel ->{
                listener.onCancel()
                dismiss()
            }
        }

    }
}

/*
class ImportDataDialog(
    val alertInfo: AlertInfo,
 ): BaseDialog<DialogImportDataBinding>(), ViewClickListener {

    interface OnDialogClickListener{
        fun onOk(view: View)
        fun onCancel(view: View)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_import_data)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_ok -> {
            }
            R.id.btn_cancel -> {

            }
        }

    }

}
 */