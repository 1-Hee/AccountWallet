package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.TextView
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.databinding.FragmentProductViewBinding
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat

class ProductViewFragment : BaseFragment<FragmentProductViewBinding>(),
    ViewClickListener {

    // vm
    private lateinit var infoItemViewModel: InfoItemViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_product_view, BR.vm, infoItemViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )
    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
            else ->{

            }
        }
    }
}