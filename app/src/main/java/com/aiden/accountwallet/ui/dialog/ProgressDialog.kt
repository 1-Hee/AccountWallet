package com.aiden.accountwallet.ui.dialog

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ProgressListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogProgressBinding

class ProgressDialog(
    private val alertInfo: AlertInfo,
    private val listener: OnProgressListener,
) : BaseDialog<DialogProgressBinding>(),
    ViewClickListener, ProgressListener {

    override var isInitView: Boolean = false

    // private var isInitView:Boolean = false

    override fun getDataBindingConfig(): DataBindingConfig {
        setCanceledOutside(false) // 꺼지지 않게 세팅
        val initStr:String = getString(R.string.txt_init_progress)
        return DataBindingConfig(R.layout.dialog_progress)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.statusMsg, initStr)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        this.isInitView = true

    }

    // 다이얼로그가 준비 되었는지 리턴하는 함수
    override fun getIsInitView():Boolean { return isInitView }

    // 다이얼로그 진행율 반영 함수
    override fun setDialogProgress(progress:Int) {
        if(!this.isInitView) return
        mBinding.pgDialog.progress = progress
        mBinding.notifyChange()
    }

    override fun setDialogStatus(msg:String){
        if(!this.isInitView) return
        mBinding.setVariable(BR.statusMsg, msg)
        mBinding.notifyChange()
    }

    // 종료 알림 함수
    override fun notifyFinishTask(sleep:Long) {
        if(!this.isInitView) return
        mBinding.pgDialog.progress = 100
        Thread.sleep(sleep)
        dismiss()
    }


    interface OnProgressListener {
        // 텍스트 변화
        // 프로그레스 변화
        fun onAction(view: View)
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_ok -> {
                listener.onAction(view)
                notifyFinishTask()
                dismiss()
            }
        }

    }
}