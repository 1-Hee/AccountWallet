package com.aiden.accountwallet.ui.fragment

import android.annotation.SuppressLint
import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentProductFormBinding
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ProductFormFragment : BaseFragment<FragmentProductFormBinding>(),
    ViewClickListener {

    private lateinit var productFormViewModel: ProductFormViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_product_form, BR.vm, productFormViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        productFormViewModel = getFragmentScopeViewModel(ProductFormViewModel::class.java)
    }

    override fun initView() {

    }

    @SuppressLint("SetTextI18n")
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