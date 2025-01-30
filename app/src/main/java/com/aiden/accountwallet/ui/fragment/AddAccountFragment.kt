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
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.ProductKeyViewModel
import com.aiden.accountwallet.databinding.FragmentAddAccountBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoTypeViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddAccountFragment : BaseFragment<FragmentAddAccountBinding>(),
    ViewClickListener, InfoTypeViewModel.InfoTypeCallback {

    // db vm
    private lateinit var identityInfoViewModel: IdentityInfoViewModel
    private lateinit var accountInfoViewModel:AccountInfoViewModel
    private lateinit var productKeyViewModel: ProductKeyViewModel

    // ui vm
    private lateinit var accountFormViewModel: AccountFormViewModel
    private lateinit var productFormViewModel: ProductFormViewModel


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
        val adapter:ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        infoTypeViewModel.initInfoTypes(items, adapter)
        infoTypeViewModel.setCallback(this)

        // ui vm init
        accountFormViewModel = getApplicationScopeViewModel(AccountFormViewModel::class.java)
        productFormViewModel = getApplicationScopeViewModel(ProductFormViewModel::class.java)

        // db model init
        val factory = ApplicationFactory(requireActivity().application)
        identityInfoViewModel = getFragmentScopeViewModel(
            IdentityInfoViewModel::class.java, factory
        )
        accountInfoViewModel = getFragmentScopeViewModel(
            AccountInfoViewModel::class.java, factory
        )
        productKeyViewModel = getFragmentScopeViewModel(
            ProductKeyViewModel::class.java, factory
        )
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_add_form) as NavHostFragment).navController
        // navController.navigate(R.id.productFormFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        accountFormViewModel.initVariables()
    }

    private fun getIdentifyInfo(typeIdx:Int = -1):IdentityInfo {
        var mInfoType:Int = 0
        var mProviderName:String = "None"
        var mMemo:String = ""
        var mTagColor:String = getString(R.string.def_tag_color)
        when(typeIdx){
            0 -> {
                mProviderName = accountFormViewModel.siteName.get()?:""
                mMemo = accountFormViewModel.memo.get()?:""
                mTagColor = accountFormViewModel.tagColor.get()?:""
                mInfoType = typeIdx
            }
            1 -> {
                mProviderName = productFormViewModel.providerName.get()?:""
                mMemo = productFormViewModel.memo.get()?:""
                mTagColor = productFormViewModel.tagColor.get()?:""
                mInfoType = typeIdx
            }
        }

        return IdentityInfo(
            fkUserId = 1, // todo user 모델과 연계
            infoType = mInfoType,
            providerName = mProviderName,
            memo = mMemo,
            tagColor = mTagColor
        )
    }

    private fun getAccountInfo(infoId: Long?): AccountInfo {
        return AccountInfo(
            fkInfoId = infoId,
            userAccount = accountFormViewModel.personalAccount.get()?:"",
            userPassword = accountFormViewModel.password.get()?:"",
            acCreatedAt = accountFormViewModel.createDate.get()?:Date(),
            officialUrl = accountFormViewModel.siteUrl.get()?:""
        )
    }

    private fun getProductKey(infoId: Long?): ProductKey {
        return ProductKey(
            fkInfoId = infoId,
            productKey = productFormViewModel.productKey.get()?:"",
            officialUrl = productFormViewModel.siteUrl.get()?:""
        )
    }

    // 사용자 계정 추가 작업
    private fun addUserAccountTask() {
        accountFormViewModel.updateStatus.postValue(true)
        // save...
        // nav().popBackStack()

        lifecycleScope.launch(Dispatchers.IO) {
            // delay(100)
            val iInfo:IdentityInfo = getIdentifyInfo(0)
            var aInfo:AccountInfo = getAccountInfo(iInfo.infoId)

            if(iInfo.providerName.isBlank()
                || aInfo.userAccount.isBlank() || aInfo.userPassword.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(), "등록 실패", Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val iId:Long = identityInfoViewModel.addEntity(iInfo)
            aInfo = getAccountInfo(iId)
            val aId:Long = accountInfoViewModel.addEntity(aInfo)

            if(aId > 0){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        "등록 성공",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 제품키 추가 작업
    private fun addProductKeyTask() {
        productFormViewModel.updateStatus.postValue(true)
        // save...
        // nav().popBackStack()

        lifecycleScope.launch(Dispatchers.IO) {
            // delay(100)
            val iInfo:IdentityInfo = getIdentifyInfo(1)
            var pInfo:ProductKey = getProductKey(iInfo.infoId)

            if(iInfo.providerName.isBlank() || pInfo.productKey.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(), "등록 실패", Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val iId:Long = identityInfoViewModel.addEntity(iInfo)
            pInfo = getProductKey(iId)
            val pId = productKeyViewModel.addEntity(pInfo)

            if(pId > 0){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        "등록 성공",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_save_account -> {
                val typeIdx:Int = infoTypeViewModel.selectedType.get()?:0
                when(typeIdx){
                    0 -> {
                        addUserAccountTask()
                    }
                    1 -> {
                        addProductKeyTask()
                    }
                }
            }
        }
    }

    override fun onItemSelected(selectedItem: String?, position: Int) {
        when(position){
            1 -> {
                navController.navigate(R.id.productFormFragment)
                infoTypeViewModel.setSelectedType(position)
            }
            else ->{
                navController.navigate(R.id.accountFormFragment)
                infoTypeViewModel.setSelectedType(0)
            }
        }
    }
}