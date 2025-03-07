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
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.ProductKeyViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.FragmentEditAccountBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoTypeViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.aiden.accountwallet.util.Logger
import com.aiden.accountwallet.util.RoomTool
import com.aiden.accountwallet.util.RoomTool.getDisplayAccountInfo
import com.aiden.accountwallet.util.UIManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditAccountFragment : BaseFragment<FragmentEditAccountBinding>(),
    ViewClickListener {

    // vm
    private lateinit var infoTypeViewModel: InfoTypeViewModel
    // vm for data edit...
    private lateinit var infoItemViewModel: InfoItemViewModel
    private lateinit var accountFormViewModel: AccountFormViewModel
    private lateinit var productFormViewModel: ProductFormViewModel
    // vm db data
    private lateinit var identityInfoViewModel:IdentityInfoViewModel
    private lateinit var accountInfoViewModel: AccountInfoViewModel
    private lateinit var productKeyViewModel: ProductKeyViewModel

    // for child Nav Controller
    private lateinit var navController: NavController
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_edit_account, BR.vm, infoItemViewModel)
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
        // vm for data edit init
        infoItemViewModel = getApplicationScopeViewModel(InfoItemViewModel::class.java)
        accountFormViewModel = getApplicationScopeViewModel(AccountFormViewModel::class.java)
        productFormViewModel = getApplicationScopeViewModel(ProductFormViewModel::class.java)

        // db vm init
        identityInfoViewModel = getApplicationScopeViewModel(IdentityInfoViewModel::class.java)
        accountInfoViewModel = getApplicationScopeViewModel(AccountInfoViewModel::class.java)
        productKeyViewModel = getApplicationScopeViewModel(ProductKeyViewModel::class.java)
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_edit_form) as NavHostFragment).navController
        loadEntityData()
    }


    private fun loadEntityData() {
        infoItemViewModel.mDisplayAccountInfo.observe(viewLifecycleOwner){
            if(it != null && it.keyIndex > 0){
                lifecycleScope.launch(Dispatchers.Main) {
                    val parentIndex:Long = it.keyIndex
                    withContext(Dispatchers.IO){
                        identityInfoViewModel.readEntity(parentIndex)
                    }

                    // tag color init
                    val context:Context = requireContext()
                    val colorHex:String = it.tagColor
                    val mTagColor:Int = UIManager.getColor(context, colorHex)
                    val mTxtColor:Int = UIManager.getContrastingTextColor(context, colorHex)
                    mBinding.setVariable(BR.tagColor, mTagColor)
                    mBinding.setVariable(BR.txtColor, mTxtColor)
                    mBinding.notifyChange()

                    // type...
                    when(it.typeIdx){
                        0 -> { // Account Info
                            val entity:IdAccountInfo
                            withContext(Dispatchers.IO){
                                entity = accountInfoViewModel.readExtraEntity(parentIndex)
                            }
                            navController.navigate(R.id.accountFormFragment)
                            accountFormViewModel.initVariables(entity)
                            accountFormViewModel.setUpdateStatus(true)
                        }
                        1 -> { // Product Key
                            val entity:IdProductKey
                            withContext(Dispatchers.IO){
                                entity = productKeyViewModel.readExtraEntity(parentIndex)
                            }
                            navController.navigate(R.id.productFormFragment)
                            productFormViewModel.initVariables(entity)
                            productFormViewModel.setUpdateStatus(true)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "로딩 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 사용자 계정 수정 작업
    private fun editUserAccountTask() {
        // TODO : 사용자 정보를 어딘가에 저장해둬서 그걸 기반으로 저장하게 하기

        accountFormViewModel.updateStatus.postValue(true)
        val context: Context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(100)
            val iInfo: IdentityInfo = RoomTool.getIdentifyInfo(
                context, 0,
                fkUserId = 1,
                accountFormViewModel,
                productFormViewModel
            )
            var aInfo: AccountInfo = RoomTool.getAccountInfo(
                iInfo.infoId, accountFormViewModel
            )

            if(iInfo.providerName.isBlank()
                || aInfo.userAccount.isBlank() || aInfo.userPassword.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_edit_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val item: IdAccountInfo = this@EditAccountFragment.infoItemViewModel.mIdAccountInfo.get()
                ?: return@launch

            val parentId:Long = item.baseInfo.infoId
            val accountId:Long = item.accountInfo.accountId
            iInfo.infoId = parentId
            aInfo = RoomTool.getAccountInfo(
                parentId,  accountFormViewModel
            )
            aInfo.accountId = accountId

            identityInfoViewModel.editEntity(iInfo)
            Logger.i("[EDIT] parent entity : %s", iInfo)
            accountInfoViewModel.editEntity(aInfo)
            Logger.i("[EDIT] child entity : %s", aInfo)

            // 변화 기록
            infoItemViewModel.setIdAccountInfo(item)
            val newItem:IdentityInfo = identityInfoViewModel.readEntity(iInfo.infoId)
            val newDisplayItem:DisplayAccountInfo = getDisplayAccountInfo(context, newItem)
            infoItemViewModel.setDisplayAccountInfo(newDisplayItem)

            withContext(Dispatchers.Main){
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_edit_success),
                    Toast.LENGTH_SHORT
                ).show()
                nav().popBackStack()
                accountFormViewModel.initVariables()
            }
        }
    }

    // 제품키 수정 작업
    private fun editProductKeyTask() {
        productFormViewModel.updateStatus.postValue(true)
        val context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            delay(100)
            val iInfo:IdentityInfo = RoomTool.getIdentifyInfo(
                context,
                1,
                fkUserId = 1,
                accountFormViewModel, productFormViewModel
            )
            var pInfo: ProductKey = RoomTool.getProductKey(
                iInfo.infoId, productFormViewModel
            )

            if(iInfo.providerName.isBlank() || pInfo.productKey.isBlank()){
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_edit_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }

            val item: IdProductKey = this@EditAccountFragment.infoItemViewModel.mIdProductKey.get()
                ?: return@launch

            val parentId:Long = item.baseInfo.infoId
            val productId:Long = item.productKey.productId
            iInfo.infoId = parentId
            pInfo = RoomTool.getProductKey(
                parentId, productFormViewModel
            )
            pInfo.productId = productId
            identityInfoViewModel.editEntity(iInfo)
            productKeyViewModel.editEntity(pInfo)

            // 변화 기록
            infoItemViewModel.setIdProductKey(item)
            val newItem:IdentityInfo = identityInfoViewModel.readEntity(iInfo.infoId)
            val newDisplayItem:DisplayAccountInfo = getDisplayAccountInfo(context, newItem)
            infoItemViewModel.setDisplayAccountInfo(newDisplayItem)

            withContext(Dispatchers.Main){
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_edit_success),
                    Toast.LENGTH_SHORT
                ).show()
                nav().popBackStack()
                productFormViewModel.initVariables()
            }
        }
    }


    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_edit_account -> {
                val typeIdx:Int = infoItemViewModel.mDisplayAccountInfo.value?.typeIdx?:return;
                when(typeIdx){
                    0 -> {
                        editUserAccountTask()
                    }
                    1 -> {
                        editProductKeyTask()
                    }
                }
            }
            else ->{

            }
        }
    }

}