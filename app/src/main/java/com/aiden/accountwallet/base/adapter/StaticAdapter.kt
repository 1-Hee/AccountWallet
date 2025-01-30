package com.aiden.accountwallet.base.adapter

import android.graphics.Color
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.data.dto.Permission
import com.aiden.accountwallet.data.dto.SettingItem
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.ItemAccountInfoBinding
import com.aiden.accountwallet.databinding.ItemPermissionBinding
import com.aiden.accountwallet.databinding.ItemSettingBinding

class StaticAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["displayAccountList", "itemClickListener"], requireAll = true)
        fun setStorageAdapter(
            recyclerView: RecyclerView,
            displayAccountList:List<DisplayAccountInfo>,
            itemClickListener: ItemClickListener<DisplayAccountInfo>
        ){
            val llm = LinearLayoutManager(
                recyclerView.context,
                RecyclerView.VERTICAL,
                false
            );

            recyclerView.layoutManager = llm
            val adapter = object : BaseDataBindingAdapter<
                    DisplayAccountInfo, ItemAccountInfoBinding>(recyclerView.context){
                override fun getDataBindingConfig(): DataBindingConfig {
                    return DataBindingConfig(R.layout.item_account_info)
                }

                override fun onBindItem(
                    binding: ItemAccountInfoBinding,
                    position: Int,
                    item: DisplayAccountInfo,
                    holder: RecyclerView.ViewHolder
                ) {
                    binding.displayAccountInfo = item
                    val colorHex = item.tagColor
                    val color:Int = colorHex.let {
                        try {
                            Color.parseColor(it)
                        } catch (e: IllegalArgumentException) {
                            try {
                                val defStr:String = binding.root.context.getString(R.string.def_tag_color)
                                Color.parseColor(defStr)
                            } catch (e: IllegalArgumentException) {
                                Color.GRAY
                            }
                        }
                    }
                    binding.mcvAccountInfo.setOnClickListener { v ->
                        itemClickListener?.onItemClick(v, item)
                    }
                    binding.mcvTypeTag.setBackgroundColor(color)
                    binding.notifyChange()
                }

            }
            adapter.setItemList(displayAccountList)
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