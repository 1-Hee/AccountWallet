package com.aiden.accountwallet.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentAccountFormBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.util.Calendar


class AccountFormFragment : BaseFragment<FragmentAccountFormBinding>(), ViewClickListener {

    private lateinit var accountFormViewModel: AccountFormViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_account_form, BR.vm, accountFormViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        accountFormViewModel = getFragmentScopeViewModel(AccountFormViewModel::class.java)
    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.et_create_date -> {
                val context = requireContext()
                // 날짜 & 시간 다이얼로그
                val calendar: Calendar = Calendar.getInstance()

                // 날짜 선택 다이얼로그
                val datePickerDialog = DatePickerDialog(
                    context,
                    { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        // 시간 선택 다이얼로그
                        val timePickerDialog = TimePickerDialog(
                            context,
                            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)

                                // 선택된 날짜 및 시간 업데이트
                                accountFormViewModel.setCreateDate(calendar.getTime())
                                // selectedDateTime.set(dateTimeFormat.format(calendar.getTime()))
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        )
                        timePickerDialog.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }

            R.id.v_color_tag -> {
                ColorPickerDialog.Builder(requireContext())
                    .setTitle(getString(R.string.title_select_tag))
                    .setPreferenceName("SelectTagColorDialog")
                    .setPositiveButton(
                        getString(R.string.btn_select),
                        ColorEnvelopeListener { envelope, fromUser ->
                            mBinding.vColorTag.setBackgroundColor(envelope.color)
                            mBinding.tvColorTag.text = "#${envelope.hexCode.substring(2)}"
                        })
                    .setNegativeButton(
                        getString(R.string.btn_cancel)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                    .attachAlphaSlideBar(false) // the default value is true.
                    .attachBrightnessSlideBar(false) // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show()
            }
        }

    }
}