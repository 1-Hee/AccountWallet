package com.aiden.accountwallet.ui.fragment

import android.content.Context
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.ui.adapter.IdentityAdapter
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.FragmentListAccountBinding
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(),
    ViewClickListener, ItemClickListener<DisplayAccountInfo> {

    // vm
    private lateinit var identityInfoViewModel:IdentityInfoViewModel
    private lateinit var infoItemViewModel: InfoItemViewModel

    // Adapter
    private lateinit var identityAdapter: IdentityAdapter

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_list_account)
            .addBindingParam(BR.click, this)
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
        // Paging 라이브러리를 사용한 어댑터 init
        val context:Context = requireContext()
        identityAdapter = IdentityAdapter(context, this)
        mBinding.rvAccountList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = identityAdapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            identityInfoViewModel.readPageEntityList().collectLatest { pagingData ->
                withContext(Dispatchers.Main){
                    identityAdapter.submitData(pagingData)
                }
            }
        }

        mBinding.notifyChange()
    }

    override fun onViewClick(view: View) {}

    override fun onItemClick(view: View, position:Int, item: DisplayAccountInfo) {
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