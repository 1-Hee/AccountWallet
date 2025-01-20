package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentStartBinding
import com.aiden.accountwallet.ui.activity.MainActivity

class StartFragment : BaseFragment<FragmentStartBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_start)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        (requireActivity() as MainActivity).supportActionBar?.hide()
    }

    override fun onViewClick(view: View) {
        when(view.id) {
            R.id.btn_start_app -> {
                // nav().navigate(R.id)
                nav().navigate(R.id.action_move_home)
            }
        }
    }
}