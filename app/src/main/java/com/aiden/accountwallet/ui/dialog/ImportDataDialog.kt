package com.aiden.accountwallet.ui.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ProgressListener
import com.aiden.accountwallet.base.listener.StopTaskListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogImportDataBinding
import com.aiden.accountwallet.ui.viewmodel.ImportDataViewModel

class ImportDataDialog(
    val alertInfo: AlertInfo,
    private val listener: OnDialogClickListener,
    private val stopListener: StopTaskListener? = null
) : BaseDialog<DialogImportDataBinding>(),
    ViewClickListener, ProgressListener {

    // vm
    private lateinit var importDataViewModel: ImportDataViewModel

    override var isInitView: Boolean = false

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_import_data, BR.vm, this.importDataViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        this.importDataViewModel = getDialogScopeViewModel(ImportDataViewModel::class.java)
        importDataViewModel.initVariables(requireContext())
        importDataViewModel.setAlertInfo(alertInfo)
    }

    override fun initView() {
        this.isInitView = true
        // 처음 상태값 init
        val statusMsg:String = getString(R.string.hint_select_backup_file)
        val statusColor:Int = ContextCompat.getColor(requireContext(), R.color.red400)
        this.importDataViewModel.setStatusMsg(statusMsg)
        this.importDataViewModel.setStatusColor(statusColor)
        this.importDataViewModel.setBackupFileName("")
        mBinding.notifyChange()
    }

    override fun getIsInitView():Boolean { return isInitView }

    override fun setDialogProgress(progress:Int) {
        if(!this.isInitView) return
        mBinding.pgDialog.progress = progress
        mBinding.notifyChange()
    }

    override fun setDialogStatus(msg:String){
        if(!this.isInitView) return
        this.importDataViewModel.setStatusMsg(msg)
        mBinding.notifyChange()
    }

    // 종료 알림 함수
    override fun notifyFinishTask(sleep:Long) {
        if(!this.isInitView) return
        mBinding.pgDialog.progress = 100
        Thread.sleep(sleep)
        dismiss()
    }

    private val openJsonLauncher = registerForActivityResult(ActivityResultContracts
        .StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                displayJsonFromUri(uri)
            }
        }
    }

    private fun openJsonFile() {
        val intent:Intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        openJsonLauncher.launch(intent)
    }

    private fun displayJsonFromUri(uri: Uri) {
        val filePath :String = uri.path?:""
        this.importDataViewModel.setBackupFileName(filePath)
        this.importDataViewModel.setSelectFileUri(uri)

        if(filePath.isNotBlank()){
            val statusMsg:String = getString(R.string.status_detect_backup)
            val statusColor:Int = ContextCompat.getColor(
                requireContext(), R.color.sky_blue400
            )
            importDataViewModel.setStatusMsg(statusMsg)
            importDataViewModel.setStatusColor(statusColor)
        }
        mBinding.notifyChange()
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.l_backup_data -> { // 파일 탐색기 띄우기
                openJsonFile()
            }
            R.id.btn_ok -> {
                isCancelable = false
                alertInfo.txtCancel = getString(R.string.btn_stop)
                val context:Context = requireContext()
                val color:Int = ContextCompat.getColor(context, R.color.red400)
                val statusColor:Int = ContextCompat.getColor(context, R.color.mono300)
                val statusMsg:String = getString(R.string.txt_init_progress)

                importDataViewModel.setIsImportStart(true)
                importDataViewModel.setAlertInfo(alertInfo)
                importDataViewModel.setStatusMsg(statusMsg)
                importDataViewModel.setStatusColor(statusColor)

                mBinding.btnCancel.setTextColor(color)
                listener.onImport(importDataViewModel.selectFileUri.get())
            }
            R.id.btn_cancel ->{
                listener.onCancel(view)
                if(importDataViewModel.isImportStart.get()){
                    stopListener?.onRequestStop(true)
                }
            }
        }
    }

    interface OnDialogClickListener{
        fun onImport(fileUri:Uri?)
        fun onCancel(view:View)
    }
}
