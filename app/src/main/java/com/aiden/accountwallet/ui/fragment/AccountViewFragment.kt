package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.TextView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.databinding.FragmentViewAccountBinding
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat

class AccountViewFragment : BaseFragment<FragmentViewAccountBinding>(),
    ViewClickListener {

    // vm
    private lateinit var infoItemViewModel: InfoItemViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_account_view, BR.vm, infoItemViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )
    }

    override fun initView() {
        val item:IdAccountInfo? = infoItemViewModel.mIdAccountInfo.get()

        // Setup Date String
        if(item != null){
            val str:String = getSimpleDateFormat().format(item.accountInfo.acCreatedAt)
            requireView().findViewById<TextView>(R.id.tv_create_date)
                .text = str
        }

    }

    override fun onViewClick(view: View) {
        when(view.id){
            else -> {

            }
        }
    }
}