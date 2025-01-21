package com.aiden.accountwallet.base.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.data.vo.AccountInfo
import com.aiden.accountwallet.databinding.ItemAccountInfoBinding

class StaticAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["accountList", "vClick"], requireAll = true)
        fun setStorageAdapter(
            recyclerView: RecyclerView,
            accountList:List<AccountInfo>,
            clickListener: ViewClickListener
        ){
            val llm = LinearLayoutManager(
                recyclerView.context,
                RecyclerView.VERTICAL,
                false
            );

            recyclerView.layoutManager = llm

            val adapter = object : BaseDataBindingAdapter<
                    AccountInfo, ItemAccountInfoBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_account_info)
                }

                override fun onBindItem(
                    binding: ItemAccountInfoBinding,
                    position: Int,
                    item: AccountInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.setVariable(BR.accountInfo, item)
                    binding.mcvAccountInfo.setOnClickListener { v ->
                        clickListener.onViewClick(v)
                    }
                    binding.notifyChange()
                }

            }
            adapter.setItemList(accountList)
            recyclerView.adapter = adapter
        }


    }
}