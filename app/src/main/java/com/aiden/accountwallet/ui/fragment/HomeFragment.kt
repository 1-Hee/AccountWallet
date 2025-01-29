package com.aiden.accountwallet.ui.fragment

import android.os.Build
import android.view.View
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentHomeBinding
import com.aiden.accountwallet.ui.activity.MainActivity
import com.google.android.gms.ads.AdRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewClickListener {

    // vm
    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.sUserNickName, getString(R.string.str_empty))
            .addBindingParam(BR.sDate, "")
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        userInfoViewModel = getFragmentScopeViewModel(
            UserInfoViewModel::class.java, factory
        )

        userInfoViewModel.readAsyncEntity()
    }

    override fun initView() {
        (requireActivity() as MainActivity).supportActionBar?.show()

        // 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        mBinding.avHome.loadAd(adRequest)
        initUserNickName()
        initCurrentDate()
        mBinding.notifyChange()

    }

    // check current Date & display
    private fun initCurrentDate() {
        val dateFormat: SimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val strDate = dateFormat.format(Date())
        mBinding.setVariable(BR.sDate, strDate)

    }

    // check user nickname & display at home fragment
    private fun initUserNickName() {
        // get nickname from db
        val userInfo:UserInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            userInfoViewModel.entityList.first
        } else {
            userInfoViewModel.entityList[0]
        }

        if(userInfo != null && userInfo.nickName.isNotBlank()){
            mBinding.setVariable(
                BR.sUserNickName,
                userInfo.nickName
            )
        }else {
            mBinding.setVariable(
                BR.sUserNickName,
                getString(R.string.str_empty)
            )
        }

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