package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentProductFormBinding

class ProductFormFragment : BaseFragment<FragmentProductFormBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_product_form)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
    }

    override fun onViewClick(view: View) {

    }
}