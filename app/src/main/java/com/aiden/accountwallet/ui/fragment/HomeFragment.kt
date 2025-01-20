package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentHomeBinding
import com.aiden.accountwallet.ui.activity.MainActivity
import com.google.android.gms.ads.AdRequest

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        (requireActivity() as MainActivity).supportActionBar?.show()

        // 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        mBinding.avHome.loadAd(adRequest)
        mBinding.notifyChange()
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_add_account -> {
                nav().navigate(R.id.action_move_add_account)
            }
            R.id.btn_see_account -> {
                nav().navigate(R.id.action_move_list_account)
            }
        }

    }

}