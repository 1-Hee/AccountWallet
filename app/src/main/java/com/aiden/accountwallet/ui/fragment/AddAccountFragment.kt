package com.aiden.accountwallet.ui.fragment

import android.content.Context
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
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentAddAccountBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoTypeViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.aiden.accountwallet.util.RoomTool
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
    private lateinit var userInfoViewModel: UserInfoViewModel

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
        val items:Array<String> = resources.getStringArray(R.array.spinner_info_type)
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
        identityInfoViewModel = getApplicationScopeViewModel(IdentityInfoViewModel::class.java)
        accountInfoViewModel = getApplicationScopeViewModel(AccountInfoViewModel::class.java)
        productKeyViewModel = getApplicationScopeViewModel(ProductKeyViewModel::class.java)
        userInfoViewModel = getApplicationScopeViewModel(UserInfoViewModel::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            userInfoViewModel.getLastUserInfo()
        }

        accountFormViewModel.initVariables()
        productFormViewModel.initVariables()
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_add_form) as NavHostFragment).navController
    }

    override fun onDestroyView() {
        super.onDestroyView()
        accountFormViewModel.initVariables()
    }

    @Deprecated("user other")
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

        val mFkUserId: Long? = userInfoViewModel.entity.get()?.userId
        return IdentityInfo(
            fkUserId = mFkUserId,
            infoType = mInfoType,
            providerName = mProviderName,
            memo = mMemo,
            tagColor = mTagColor
        )
    }

    @Deprecated("user other")
    private fun getAccountInfo(infoId: Long?): AccountInfo {
        return AccountInfo(
            fkInfoId = infoId,
            userAccount = accountFormViewModel.personalAccount.get()?:"",
            userPassword = accountFormViewModel.password.get()?:"",
            acCreatedAt = accountFormViewModel.createDate.get()?:Date(),
            officialUrl = accountFormViewModel.siteUrl.get()?:""
        )
    }

    @Deprecated("user other")
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
        val context:Context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(100)
            val mFkUserId: Long? = userInfoViewModel.entity.get()?.userId
            val iInfo:IdentityInfo = RoomTool.getIdentifyInfo(
                context,
                typeIdx = 0,
                fkUserId = mFkUserId,
                accountFormViewModel,
                productFormViewModel
            )
            var aInfo:AccountInfo = RoomTool.getAccountInfo(
                iInfo.infoId, accountFormViewModel
            )

            if(iInfo.providerName.isBlank()
                || aInfo.userAccount.isBlank() || aInfo.userPassword.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_add_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val iId:Long = identityInfoViewModel.addEntity(iInfo)
            aInfo = RoomTool.getAccountInfo(iId, accountFormViewModel)
            val aId:Long = accountInfoViewModel.addEntity(aInfo)

            if(aId > 0){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_add_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    nav().popBackStack()
                    accountFormViewModel.initVariables()
                }
            }
        }
    }

    // 제품키 추가 작업
    private fun addProductKeyTask() {
        productFormViewModel.updateStatus.postValue(true)
        val context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(100)
            // here
            val mFkUserId: Long? = userInfoViewModel.entity.get()?.userId
            val iInfo:IdentityInfo = RoomTool.getIdentifyInfo(
                context,
                typeIdx = 1,
                fkUserId = mFkUserId,
                accountFormViewModel, productFormViewModel
            )
            var pInfo:ProductKey = RoomTool.getProductKey(
                iInfo.infoId, productFormViewModel
            )

            if(iInfo.providerName.isBlank() || pInfo.productKey.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_add_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val iId:Long = identityInfoViewModel.addEntity(iInfo)
            pInfo = RoomTool.getProductKey(iId, productFormViewModel)
            val pId:Long = productKeyViewModel.addEntity(pInfo)

            if(pId > 0){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_add_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    nav().popBackStack()
                    productFormViewModel.initVariables()
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