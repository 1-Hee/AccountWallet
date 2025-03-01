package com.aiden.accountwallet.ui.fragment

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.data.dto.Info
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.viewmodel.AccountInfoViewModel
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.ProductKeyViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.data.vo.DownloadType
import com.aiden.accountwallet.databinding.FragmentViewAccountBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog
import com.aiden.accountwallet.ui.dialog.DownloadItemDialog
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.aiden.accountwallet.util.FileManager
import com.aiden.accountwallet.util.Logger
import com.aiden.accountwallet.util.RoomTool
import com.aiden.accountwallet.util.UIManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewAccountFragment : BaseFragment<FragmentViewAccountBinding>(),
    ViewClickListener, AlertDialog.OnDialogClickListener, DownloadItemDialog.DownloadItemCallBack {

    // vm
    private lateinit var infoItemViewModel: InfoItemViewModel
    // db vm
    private lateinit var identityInfoViewModel: IdentityInfoViewModel
    private lateinit var accountInfoViewModel: AccountInfoViewModel
    private lateinit var productKeyViewModel: ProductKeyViewModel
    // vm
    private lateinit var productFormViewModel:ProductFormViewModel
    private lateinit var accountFormViewModel:AccountFormViewModel

    private lateinit var navController: NavController

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_view_account, BR.vm, infoItemViewModel)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.typeName, "")
    }

    override fun initViewModel() {
        infoItemViewModel = getApplicationScopeViewModel(InfoItemViewModel::class.java)

        // db vm init
        identityInfoViewModel = getApplicationScopeViewModel(IdentityInfoViewModel::class.java)
        accountInfoViewModel = getApplicationScopeViewModel(AccountInfoViewModel::class.java)
        productKeyViewModel = getApplicationScopeViewModel(ProductKeyViewModel::class.java)

        accountInfoViewModel.initVariables()
        productKeyViewModel.initVariables()

        // display vm
        productFormViewModel = getApplicationScopeViewModel(ProductFormViewModel::class.java)
        accountFormViewModel = getApplicationScopeViewModel(AccountFormViewModel::class.java)
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_view_form) as NavHostFragment).navController

        infoItemViewModel.mDisplayAccountInfo.observe(viewLifecycleOwner) {
            if (it != null && it.providerName.isNotBlank()) {
                mBinding.setVariable(BR.typeName, it.tagName)
                mBinding.notifyChange()

                // tag color init
                val context:Context = requireContext()
                val colorHex:String = it.tagColor
                val mTagColor:Int = UIManager.getColor(context, colorHex)
                val mTxtColor:Int = UIManager.getContrastingTextColor(context, colorHex)
                mBinding.setVariable(BR.tagColor, mTagColor)
                mBinding.setVariable(BR.txtColor, mTxtColor)
                mBinding.notifyChange()


                when(it.typeIdx){
                    0 -> { // Search Account info Data
                        lifecycleScope.launch(Dispatchers.IO) {
                            val infoId:Long = it.keyIndex
                            val entity:IdAccountInfo =  accountInfoViewModel.readExtraEntity(infoId)
                            Logger.i("View Read Entity (Account) : %s", entity)
                            accountFormViewModel.initVariables(entity)
                            this@ViewAccountFragment.infoItemViewModel.setIdAccountInfo(entity)

                        }
                        navController.navigate(R.id.accountViewFragment)
                    }
                    1 -> { // Search Product info Data
                        lifecycleScope.launch(Dispatchers.IO) {
                            val infoId:Long = it.keyIndex
                            val entity:IdProductKey =  productKeyViewModel.readExtraEntity(infoId)
                            Logger.i("View Read Entity (Product) : %s", entity)
                            productFormViewModel.initVariables(entity)
                            this@ViewAccountFragment.infoItemViewModel.setIdProductKey(entity)
                        }
                        navController.navigate(R.id.productViewFragment)
                    }
                }
            }
        }


    }

    override fun onViewClick(view: View) {
        when(view.id) {
            R.id.btn_edit_account -> {
                nav().navigate(R.id.editAccountFragment)
            }
            R.id.btn_download_data -> {
                // BottomSheetDialog Pop up
                val infoItem:DisplayAccountInfo = infoItemViewModel.mDisplayAccountInfo.value?:return
                val mTypeIdx:Int = infoItem.typeIdx
                val modal = DownloadItemDialog(mTypeIdx, this)
                val fragmentManager:FragmentManager = requireActivity().supportFragmentManager
                modal.show(fragmentManager, modal::class.java.simpleName)

            }
            R.id.iv_delete -> {
                // 삭제 경고창 띄움
                val title:String = getString(R.string.title_warning)
                val content:String = getString(R.string.content_delete_info)
                val btnOkStr = "Delete"
                val tempInfo = AlertInfo(title, content, flag = true, txtOk = btnOkStr)
                val dialog = AlertDialog(tempInfo,this@ViewAccountFragment)
                dialog.show(requireActivity().supportFragmentManager, null)
            }
            else -> {

            }
        }
    }

    // Dialog Listener
    override fun onOk(view: View) {
        when(view.id){
            R.id.btn_ok -> {
                val item:DisplayAccountInfo? = infoItemViewModel.mDisplayAccountInfo.value
                if(item != null){
                    lifecycleScope.launch(Dispatchers.IO) {
                        identityInfoViewModel.removeEntity(item.keyIndex)

                        withContext(Dispatchers.Main){
                            val mView:View = requireView()
                            // Show Snack Bar
                            val snackMsg:String = getString(R.string.msg_delete_complete)
                            Snackbar.make(mView, snackMsg, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .setAnchorView(R.id.sp_bottom).show()
                            nav().popBackStack()
                        }
                    }

                }
            }
        }

    }

    override fun onCancel(view: View) {

    }

    override fun onItemSelected(selectedItem: DownloadType, position: Int) {
        Logger.i( "선택 아이템 [${selectedItem.typeIdx}] : ${selectedItem.typeValue} , position : $position")

        val context:Context = requireContext()
        val mBaseInfo:IdentityInfo
        val infoList:List<Info> = when(selectedItem.typeIdx){
            0 -> { // account
                val item:IdAccountInfo = infoItemViewModel.mIdAccountInfo.get()?:return
                mBaseInfo = item.baseInfo
                RoomTool.parseIdAccountInfo(context, item)
            }
            1 -> { // product
                val item:IdProductKey = infoItemViewModel.mIdProductKey.get()?:return
                mBaseInfo = item.baseInfo
                RoomTool.parseIdProductKey(context, item)
            }
            else -> return
        }

        when(position) { // 저장 유형
            0 -> { // text
                FileManager.saveTextFile(context, mBaseInfo.providerName, infoList)
            }
            1 ->  { // html
                FileManager.saveHTMLFile(requireActivity(), mBaseInfo.providerName, infoList)
                return
            }
            2 -> { // json
                FileManager.saveJsonFile(context, mBaseInfo.providerName, infoList)
                return
            }

        }
    }
}