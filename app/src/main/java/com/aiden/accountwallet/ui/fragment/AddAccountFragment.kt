package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.databinding.FragmentAddAccountBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoTypeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddAccountFragment : BaseFragment<FragmentAddAccountBinding>(),
    ViewClickListener, InfoTypeViewModel.InfoTypeCallback {

    // vm
    private lateinit var identityInfoViewModel: IdentityInfoViewModel
    private lateinit var accountInfoViewModel:AccountInfoViewModel

    private lateinit var accountFormViewModel: AccountFormViewModel


    private lateinit var infoTypeViewModel: InfoTypeViewModel
    private lateinit var navController:NavController

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_add_account)
            .addBindingParam(BR.infoVm, infoTypeViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        infoTypeViewModel = getFragmentScopeViewModel(InfoTypeViewModel::class.java)
        // 타입 배열
        val items = resources.getStringArray(R.array.spinner_info_type)
        // ArrayAdapter에 커스텀 레이아웃 적용
        val adapter:ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        infoTypeViewModel.initInfoTypes(items, adapter)
        infoTypeViewModel.setCallback(this)

        accountFormViewModel = getApplicationScopeViewModel(AccountFormViewModel::class.java)

        // db model init
        val factory = ApplicationFactory(requireActivity().application)
        identityInfoViewModel = getFragmentScopeViewModel(
            IdentityInfoViewModel::class.java, factory
        )
        accountInfoViewModel = getFragmentScopeViewModel(
            AccountInfoViewModel::class.java, factory
        )
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_add_form) as NavHostFragment).navController
        // navController.navigate(R.id.productFormFragment)

        accountFormViewModel.updateStatus.observe(viewLifecycleOwner){
            if(it){
                accountFormViewModel.updateStatus.postValue(false)
            }
        }
    }

    private fun getIdentifyInfo():IdentityInfo {
        return IdentityInfo(
            fkUserId = 1, // todo user 모델과 연계
            memo = accountFormViewModel.memo.get()?:"",
            tagColor = accountFormViewModel.tagColor.get()?:"#93534C"
        )
    }

    private fun getAccountInfo(infoId: Long?): AccountInfo {
        return AccountInfo(
            fkInfoId = infoId,
            siteName = accountFormViewModel.siteName.get()?:"",
            userAccount = accountFormViewModel.personalAccount.get()?:"",
            userPassword = accountFormViewModel.password.get()?:"",
            acCreatedAt = accountFormViewModel.createDate.get()?:Date(),
            officialUrl = accountFormViewModel.siteUrl.get()?:""
        )
    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_save_account -> {
                // save...
                // nav().popBackStack()

                lifecycleScope.launch(Dispatchers.IO) {
                    val iInfo:IdentityInfo = getIdentifyInfo()
                    val pId:Long = identityInfoViewModel.addEntity(iInfo)
                    val aInfo:AccountInfo = getAccountInfo(pId)
                    val aId:Long = accountInfoViewModel.addEntity(aInfo)
                    if(aId > 0){
                        Toast.makeText(requireContext(), "등록 성공", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
    }

    override fun onItemSelected(selectedItem: String?, position: Int) {
        when(position){
            1 -> {
                navController.navigate(R.id.productFormFragment)
            }
            else ->{
                navController.navigate(R.id.accountFormFragment)
            }
        }
    }
}