package com.aiden.accountwallet.base.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.data.dto.Permission
import com.aiden.accountwallet.data.dto.SettingItem
import com.aiden.accountwallet.data.vo.DownloadType
import com.aiden.accountwallet.databinding.ItemDownloadTypeBinding
import com.aiden.accountwallet.databinding.ItemPermissionBinding
import com.aiden.accountwallet.databinding.ItemSettingBinding

class StaticAdapter {

    companion object {

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

        // download Item
        @JvmStatic
        @BindingAdapter(value = ["downloadTypeList", "itemClick"], requireAll = true)
        fun setDownloadTypeAdapter(
            recyclerView: RecyclerView,
            downloadTypeList:List<DownloadType>,
            itemClick:ItemClickListener<DownloadType>
        ) {
            val llm = LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false);
            recyclerView.layoutManager = llm

            val context = recyclerView.context
            val adapter = object : BaseDataBindingAdapter<
                    DownloadType, ItemDownloadTypeBinding>(context) {
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_download_type)
                }

                override fun onBindItem(
                    binding: ItemDownloadTypeBinding,
                    position: Int,
                    item: DownloadType,
                    holder: RecyclerView.ViewHolder
                ) {
                    val item:DownloadType = downloadTypeList[position]
                    binding.setVariable(BR.downloadType, item)
                    binding.clDownloadItem.setOnClickListener { v ->
                        itemClick.onItemClick(v, position, item)
                        binding.rbDownloadItem.isChecked = true
                    }
                    binding.rbDownloadItem.setOnClickListener { v ->
                        itemClick.onItemClick(v, position, item)
                        binding.rbDownloadItem.isChecked = true
                    }
                    binding.notifyChange()
                }
            }

            adapter.setItemList(downloadTypeList)
            recyclerView.adapter = adapter
        }


    }
}