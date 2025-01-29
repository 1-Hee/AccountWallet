package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.Toast
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentStartBinding
import com.aiden.accountwallet.ui.activity.MainActivity

class StartFragment : BaseFragment<FragmentStartBinding>(), ViewClickListener {

    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_start)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        userInfoViewModel = getFragmentScopeViewModel(
            UserInfoViewModel::class.java, factory
        )
    }

    override fun initView() {
        (requireActivity() as MainActivity).supportActionBar?.hide()

        userInfoViewModel.addStatus.observe(this) { it ->
            if(it > 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_success_nickname),
                    Toast.LENGTH_SHORT
                ).show()
                nav().navigate(R.id.action_move_home)
            }
        }
    }

    override fun onViewClick(view: View) {
        when(view.id) {
            R.id.btn_start_app -> {
                // nav().navigate(R.id)
                // nav().navigate(R.id.action_move_home)

                val nickName = mBinding.etEnterNickName.text.toString()
                if(nickName.isBlank()){
                    Toast.makeText(
                        requireContext(),
                        "닉네임을 입력해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val userInfo = UserInfo(
                    nickName = nickName
                )

                userInfoViewModel.addAsyncEntity(userInfo)

            }
        }
    }
}