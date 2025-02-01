package com.aiden.accountwallet.ui.fragment

import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentHomeBinding
import com.aiden.accountwallet.ui.activity.MainActivity
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ViewClickListener {

    // vm
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var identityInfoViewModel: IdentityInfoViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_home)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.sUserNickName, getString(R.string.str_empty))
            .addBindingParam(BR.sDate, "")
            .addBindingParam(BR.sAccountCnt, "")
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        userInfoViewModel = getFragmentScopeViewModel(
            UserInfoViewModel::class.java, factory
        )
        identityInfoViewModel = getFragmentScopeViewModel(
            IdentityInfoViewModel::class.java, factory
        )
    }

    override fun initView() {
        (requireActivity() as MainActivity).supportActionBar?.show()

        // 배너 광고 로드
        val adRequest = AdRequest.Builder().build()
        mBinding.avHome.loadAd(adRequest)
        initUserNickName()
        initCurrentDate()
        initAccountList();
        mBinding.notifyChange()

    }

    // check Total Account List
    private fun initAccountList() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cnt:Long = identityInfoViewModel.getIdentityInfoCnt()
            withContext(Dispatchers.Main){
                mBinding.setVariable(BR.sAccountCnt, cnt.toString())
            }
        }
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
        lifecycleScope.launch(Dispatchers.IO) {
            val userInfo:UserInfo? = userInfoViewModel.getLastUserInfo()
            if(userInfo != null){
                withContext(Dispatchers.Main){
                    if(userInfo.nickName.isNotBlank()){
                        mBinding.setVariable(
                            BR.sUserNickName,
                            userInfo.nickName
                        )
                    } else {
                        mBinding.setVariable(
                            BR.sUserNickName,
                            getString(R.string.str_empty)
                        )
                    }
                }
            }
        }
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.tv_card_title -> { // change nickname
                Toast.makeText(requireContext(), "Change Nick Name...", Toast.LENGTH_SHORT).show()

            }

            R.id.btn_add_account -> {
                nav().navigate(R.id.action_move_add_account)
            }
            R.id.btn_see_account -> {
                nav().navigate(R.id.action_move_list_account)
            }
        }

    }

}