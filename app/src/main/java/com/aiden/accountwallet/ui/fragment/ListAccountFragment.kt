package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.vo.AccountInfo
import com.aiden.accountwallet.databinding.FragmentListAccountBinding

class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {

        val mAccountList = ArrayList<AccountInfo>()
        for(i in 0..9)
        {
            mAccountList.add(
                AccountInfo(
                    (i+1),
                    "Test Name",
                    0,
                    "개인 계정",
                    "2025.01.19."
                )
            )
        }



        return DataBindingConfig(R.layout.fragment_list_account)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.accountList, mAccountList)
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {

    }
}