package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentAccountFormBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class AccountFormFragment : BaseFragment<FragmentAccountFormBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_account_form)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
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