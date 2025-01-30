package com.aiden.accountwallet.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.factory.ApplicationFactory
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.FragmentListAccountBinding
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.util.TimeParser.DATE_FORMAT
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat
import timber.log.Timber
import java.text.SimpleDateFormat

class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(),
    ViewClickListener, ItemClickListener<DisplayAccountInfo> {

    data class TagInfo(
        var tagName:String = "",
        val tagColor:String = "#93534C"
    )

    // vm
    private lateinit var identityInfoViewModel:IdentityInfoViewModel
    private lateinit var infoItemViewModel: InfoItemViewModel

    // variables
    private lateinit var mDisplayAccountList:MutableList<DisplayAccountInfo>


    override fun getDataBindingConfig(): DataBindingConfig {
        mDisplayAccountList = mutableListOf()

        return DataBindingConfig(R.layout.fragment_list_account)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.itemClick, this)
            .addBindingParam(BR.displayAccountList, mDisplayAccountList)
    }

    override fun initViewModel() {
        val factory = ApplicationFactory(requireActivity().application)
        identityInfoViewModel = getFragmentScopeViewModel(
            IdentityInfoViewModel::class.java, factory
        )
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )

        identityInfoViewModel.readAsyncEntity()
    }

    private fun getTagInfo(typeIdx:Int, tagColor: String):TagInfo{
        return when(typeIdx) {
            0 -> TagInfo(getString(R.string.tag_str_account), tagColor)
            1 -> TagInfo(getString(R.string.tag_str_product_key), tagColor)
            else -> TagInfo("None", tagColor)
        }
    }

    override fun initView() {
        val sFormat:SimpleDateFormat = getSimpleDateFormat(DATE_FORMAT)
        identityInfoViewModel.entityList.forEach { item ->
            val tagInfo = getTagInfo(item.infoType, item.tagColor)
            val mAccountInfo = DisplayAccountInfo (
                item.infoId,
                item.providerName,
                item.infoType,
                tagInfo.tagName,
                tagInfo.tagColor,
                sFormat.format(item.createAt)
            )
            Timber.i("item info : %s", mAccountInfo)
            this.mDisplayAccountList.add(mAccountInfo)
        }
        mBinding.setVariable(BR.displayAccountList, mDisplayAccountList)
        mBinding.notifyChange()
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.mcv_account_info -> {
                // nav().navigate(R.id.action_move_view_account)
            }
            else ->{

            }
        }
    }

    override fun onItemClick(view: View, item: DisplayAccountInfo) {
        when(view.id){
            R.id.mcv_account_info -> {
                infoItemViewModel.setDisplayAccountInfo(item)
                nav().navigate(R.id.action_move_view_account)
            }
            else ->{

            }
        }

    }
}