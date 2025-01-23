package com.aiden.accountwallet.ui.dialog

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogAlertBinding

class AlertDialog(
    private val alertInfo: AlertInfo,
    private val listener:OnDialogClickListener
) : BaseDialog<DialogAlertBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_alert)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {


    }

    interface OnDialogClickListener{
        fun onOk(view: View)
        fun onCancel(view: View)
    }

    override fun onViewClick(view: View) {
        when(view.id) {
            R.id.btn_ok -> {
                listener.onOk(view)
                dismiss()
            }
            R.id.btn_cancel -> {
                dismiss()
            }
        }
    }
}