package com.aiden.accountwallet.ui.fragment

import android.content.Context
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.FragmentListAccountBinding
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.util.RoomTool.getDisplayAccountInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(),
    ViewClickListener, ItemClickListener<DisplayAccountInfo> {

    // vm
    private lateinit var identityInfoViewModel:IdentityInfoViewModel
    private lateinit var infoItemViewModel: InfoItemViewModel

    // TODO 검색기능..
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
        identityInfoViewModel = getApplicationScopeViewModel(
            IdentityInfoViewModel::class.java
        )

        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )
    }

    override fun initView() {
        val context:Context = requireContext()
        lifecycleScope.launch(Dispatchers.IO) {
            this@ListAccountFragment.mDisplayAccountList.clear()
            val itemList:List<IdentityInfo> = identityInfoViewModel.readEntityList()
            itemList.forEach { item ->
                val mAccountInfo:DisplayAccountInfo = getDisplayAccountInfo(context, item)
                this@ListAccountFragment.mDisplayAccountList.add(mAccountInfo)
            }
            mBinding.setVariable(BR.displayAccountList, mDisplayAccountList)
        }
    }

    override fun onViewClick(view: View) {}

    override fun onItemClick(view: View, item: DisplayAccountInfo) {
        when(view.id){
            R.id.mcv_account_info -> {
                infoItemViewModel.setDisplayAccountInfo(item) // todo remove
                nav().navigate(R.id.action_move_view_account)
            }
            else ->{

            }
        }
    }
}