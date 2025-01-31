package com.aiden.accountwallet.ui.fragment

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentIntroBinding
import com.aiden.accountwallet.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroFragment : BaseFragment<FragmentIntroBinding>() {

    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_intro)
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        userInfoViewModel = getFragmentScopeViewModel(
            UserInfoViewModel::class.java, factory
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as AppCompatActivity).supportActionBar?.hide()
    }

    override fun initView() {
        // check nickname
        lifecycleScope.launch(Dispatchers.IO){
            val nickNameList:List<UserInfo> = userInfoViewModel.readEntityList()
            delay(800)
            if(nickNameList.isNotEmpty()){
                withContext(Dispatchers.Main){
                    nav().popBackStack(R.id.introFragment, true)
                    nav().navigate(R.id.homeFragment)
                }
            }else {
                withContext(Dispatchers.Main){
                    nav().popBackStack(R.id.introFragment, true)
                    nav().navigate(R.id.startFragment)
                }
            }
        }
    }
}