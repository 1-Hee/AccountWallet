package com.aiden.accountwallet.ui.fragment

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
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
import com.aiden.accountwallet.ui.viewmodel.ListAccountViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListAccountFragment : BaseFragment<FragmentListAccountBinding>(),
    ViewClickListener, ItemClickListener<DisplayAccountInfo>,
    TextWatcher, ListAccountViewModel.SortOptionCallback {

    // vm
    private lateinit var identityInfoViewModel:IdentityInfoViewModel
    private lateinit var infoItemViewModel: InfoItemViewModel
    private lateinit var listAccountViewModel: ListAccountViewModel

    // Adapter
    private lateinit var identityAdapter: IdentityAdapter
    // Sort List
    private val sortOptionList:ArrayList<String> = arrayListOf()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_list_account, BR.vm, listAccountViewModel)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.searchWatcher, this)
            .addBindingParam(BR.isVisible, false)
    }

    override fun initViewModel() {
        identityInfoViewModel = getApplicationScopeViewModel(
            IdentityInfoViewModel::class.java
        )
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )
        listAccountViewModel = getFragmentScopeViewModel(ListAccountViewModel::class.java)

        // 정렬 배열
        val items:Array<String> = resources.getStringArray(R.array.arr_sort_option)
        // ArrayAdapter에 커스텀 레이아웃 적용
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listAccountViewModel.initSortOptions(items, adapter)
        listAccountViewModel.setSortCallback(this)

        // 정렬 쿼리값 init
        val queryItems:Array<String> = resources.getStringArray(R.array.key_sort_option)
        sortOptionList.clear()
        sortOptionList.addAll(queryItems)
    }

    override fun initView() {
        // Paging 라이브러리를 사용한 어댑터 init
        val context:Context = requireContext()
        identityAdapter = IdentityAdapter(context, this)
        mBinding.rvAccountList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = identityAdapter
        }
        // loadIdentityInfoList() // 조건 없이 load
        mBinding.notifyChange()
    }

    // load items
    private fun loadIdentityInfoList(
        query:String? = null,
        sortType:String? = null,
        isChecked:Boolean = false
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            identityInfoViewModel.readPageQuerySortCheckList(
                query, sortType, isChecked
            ).collectLatest { pagingData ->
                withContext(Dispatchers.Main){
                    identityAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.cb_account_only -> {
                // add filter

                val isChecked:Boolean = (view as CheckBox).isChecked
                listAccountViewModel.setIsChecked(isChecked)

                val mQuery:String = listAccountViewModel.searchQuery
                val sortIdx:Int = listAccountViewModel.sortIdx.value ?: 0
                val sortType:String? = if(sortIdx >= this.sortOptionList.size) null
                else this.sortOptionList[sortIdx]

                loadIdentityInfoList(mQuery, sortType, isChecked)

            }
            R.id.iv_search_clear -> {
                mBinding.etSearchAccount.setText("")
                // load init!
            }
        }
    }

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


    // Text Watcher
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        val mQuery:String = s.toString()
        mBinding.setVariable(BR.isVisible, mQuery.isNotBlank())
        listAccountViewModel.setSearchQuery(mQuery)

        val sortIdx:Int = listAccountViewModel.sortIdx.value ?: 0
        val sortType:String? = if(sortIdx >= this.sortOptionList.size) null
        else this.sortOptionList[sortIdx]
        val isChecked:Boolean = listAccountViewModel.isChecked.value?:false

        loadIdentityInfoList(mQuery, sortType, isChecked)
    }

    // Sort Callback
    override fun onItemSelected(selectedItem: String?, position: Int) {
        listAccountViewModel.setSortIdx(position)

        val mQuery:String = listAccountViewModel.searchQuery
        val sortType:String? = if(position >= this.sortOptionList.size) null
        else this.sortOptionList[position]
        val isChecked:Boolean = listAccountViewModel.isChecked.value?:false

        loadIdentityInfoList(mQuery, sortType, isChecked)
        // Toast.makeText(requireContext(), "[$position] 정렬 옵션 : $selectedItem", Toast.LENGTH_SHORT).show()

    }
}