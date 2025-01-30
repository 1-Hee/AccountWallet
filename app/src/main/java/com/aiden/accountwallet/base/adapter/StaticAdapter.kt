package com.aiden.accountwallet.base.adapter

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.OnEditorActionListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.data.dto.Permission
import com.aiden.accountwallet.data.dto.SettingItem
import com.aiden.accountwallet.data.vo.AccountInfo
import com.aiden.accountwallet.databinding.ItemAccountInfoBinding
import com.aiden.accountwallet.databinding.ItemPermissionBinding
import com.aiden.accountwallet.databinding.ItemSettingBinding

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
                        .addBindingParam(BR.click, clickListener)
                }

                override fun onBindItem(
                    binding: ItemAccountInfoBinding,
                    position: Int,
                    item: AccountInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.accountInfo = item
                    binding.notifyChange()
                }

            }
            adapter.setItemList(accountList)
            recyclerView.adapter = adapter
        }

        // User Permissions ....
        @JvmStatic
        @BindingAdapter(value = ["permissionInfoList", "vClick"], requireAll = true)
        fun setPermissionAdapter(recyclerView: RecyclerView, infoList:List<Permission>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<
                    Permission, ItemPermissionBinding>(recyclerView.context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_permission)
                        .addBindingParam(BR.click, viewClick)
                }

                override fun onBindItem(
                    binding: ItemPermissionBinding,
                    position: Int,
                    item: Permission,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.permissionInfo = item
                    binding.notifyChange()
                }
            }

            adapter.setItemList(infoList)
            recyclerView.adapter = adapter
        }

        // Setting Items...
        @JvmStatic
        @BindingAdapter(value = ["settingItemList", "vClick"], requireAll = true)
        fun setSettingAdapter(recyclerView: RecyclerView, settingItemList:List<SettingItem>, viewClick:ViewClickListener){
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm

            val adapter = object : BaseDataBindingAdapter<
                    SettingItem, ItemSettingBinding>(recyclerView.context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_setting)
                        .addBindingParam(BR.click, viewClick)
                }

                override fun onBindItem(
                    binding: ItemSettingBinding,
                    position: Int,
                    item: SettingItem,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.settingInfo = item
                    binding.notifyChange()
                }


            }
            adapter.setItemList(settingItemList)
            recyclerView.adapter = adapter
        }


    }
}