package com.aiden.accountwallet.ui.fragment

import android.view.View
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.vo.AccountInfo
import com.aiden.accountwallet.databinding.FragmentListAccountBinding
import com.aiden.accountwallet.util.TimeParser.DATE_FORMAT
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat
import timber.log.Timber

class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(), ViewClickListener {

    data class TagInfo(
        var tagStr:String = "",
        val tagColor:String = "93534C"
    )

    // vm
    private lateinit var identityInfoViewModel:IdentityInfoViewModel

    // variables
    private lateinit var mAccountList:MutableList<AccountInfo>


    override fun getDataBindingConfig(): DataBindingConfig {
        mAccountList = mutableListOf()

        return DataBindingConfig(R.layout.fragment_list_account)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.accountList, mAccountList)
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        identityInfoViewModel = getFragmentScopeViewModel(
            IdentityInfoViewModel::class.java, factory
        )
        identityInfoViewModel.readAsyncEntity()
    }


    override fun initView() {

        val format = getSimpleDateFormat(DATE_FORMAT)

        identityInfoViewModel.entityList.forEach { item ->

            val tagInfo = getTagInfo(item.infoType, item.tagColor)

            val mAccountInfo = AccountInfo(
                item.infoId,
                item.providerName,
                item.infoType,
                tagInfo.tagStr,
                format.format(item.createAt)
            )
            Timber.i("item info : %s", mAccountInfo)
            this.mAccountList.add(mAccountInfo)
        }
        mBinding.setVariable(BR.accountList, mAccountList)
        mBinding.notifyChange()

    }

    private fun getTagInfo(typeIdx:Int, tagColor: String):TagInfo{
        return when(typeIdx) {
            0 -> TagInfo(getString(R.string.tag_str_account), tagColor)
            1 -> TagInfo(getString(R.string.tag_str_product_key), tagColor)
            else -> TagInfo("None", tagColor)
        }
    }

    override fun onViewClick(view: View) {

        when(view.id){
            R.id.mcv_account_info -> {
                nav().navigate(R.id.action_move_view_account)
            }
            else ->{

            }
        }

    }
}