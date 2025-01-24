package com.aiden.accountwallet.ui.dialog

import android.view.View
import android.widget.ArrayAdapter
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.DialogSelectBinding

class SelectDialog(
    val alertInfo: AlertInfo,
    private val onSelectListener: OnSelectListener
): BaseDialog<DialogSelectBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {

        // 타입 배열
        val items = arrayOf(
            "_backup_202455413.json",
            "_backup_202477642.json",
            "_backup_202411244.json",
            "_backup_202474552.json"
        )

        // ArrayAdapter에 커스텀 레이아웃 적용
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return DataBindingConfig(R.layout.dialog_select)
            .addBindingParam(BR.alertInfo, alertInfo)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.spinnerAdapter, adapter)
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_ok -> {
                val selectIndex = mBinding?.spSelectType?.selectedItemPosition?:-1
                onSelectListener.onSelect(selectIndex)
                dismiss()
            }
            R.id.btn_cancel -> {
                onSelectListener.onCancel()
                dismiss()
            }
        }

    }

    interface OnSelectListener {
        // Spinner랑 연계해서
        fun onSelect(position: Int)
        fun onCancel()
    }
}