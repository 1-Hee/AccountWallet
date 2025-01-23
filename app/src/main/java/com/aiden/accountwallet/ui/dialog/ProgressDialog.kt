package com.aiden.accountwallet.ui.dialog

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogProgressBinding

class ProgressDialog(
    private val alertInfo: AlertInfo,
    private val listener: OnProgressListener
) : BaseDialog<DialogProgressBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_progress)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    interface OnProgressListener {
        // 텍스트 변화
        // 프로그레스 변화
        fun onOk(view: View)
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_ok -> {
                listener.onOk(view)
                dismiss()
            }
        }

    }
}