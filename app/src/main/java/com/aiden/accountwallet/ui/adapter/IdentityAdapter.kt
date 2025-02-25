package com.aiden.accountwallet.ui.adapter

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.databinding.ItemAccountInfoBinding
import com.aiden.accountwallet.util.Logger
import com.aiden.accountwallet.util.RoomTool.getDisplayAccountInfo
import com.aiden.accountwallet.util.UIManager

class IdentityAdapter(
    private val context: Context,
    private val itemClickListener: ItemClickListener<DisplayAccountInfo>
) : PagingDataAdapter<IdentityInfo, RecyclerView.ViewHolder>(COMPARATOR) {

    private fun onBindItem(
        binding: ItemAccountInfoBinding,
        position:Int,
        item: IdentityInfo?,
        holder: RecyclerView.ViewHolder) {

        if(item == null) return
        val displayItem:DisplayAccountInfo = getDisplayAccountInfo(context, item)

        binding.displayAccountInfo = displayItem
        val colorHex:String = item.tagColor
        val tagColor:Int = UIManager.getColor(context, colorHex)
        val txtColor:Int = UIManager.getContrastingTextColor(context, colorHex)
        binding.setVariable(BR.tagColor, tagColor)
        binding.setVariable(BR.txtColor, txtColor)
        binding.mcvAccountInfo.setOnClickListener { v ->
            itemClickListener.onItemClick(v, position, displayItem)
        }
        binding.notifyChange()

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding:ItemAccountInfoBinding? = DataBindingUtil.getBinding(holder.itemView)
        binding?.let { onBindItem(it, position, getItem(position), holder) }
        binding?.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //        Timber.d("onCreateViewHolder")
        Logger.d("onCreateViewHolder ... ")
        // config load
        val dbConfig = DataBindingConfig(R.layout.item_account_info)
        val binding: ItemAccountInfoBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this.context),
            dbConfig.layout,
            parent,
            false
        )
        // if has ViewModel..add ViewModel..!
        binding.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        // add other data binding params...
        val params: SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }

        return BasePageViewHolder(binding.root)
    }
    class BasePageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Page Adapter 를 위한 콜백
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<IdentityInfo>() {
            override fun areItemsTheSame(oldItem: IdentityInfo, newItem: IdentityInfo): Boolean {
                return oldItem.infoId == newItem.infoId
            }

            override fun areContentsTheSame(oldItem: IdentityInfo, newItem: IdentityInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

    // Page Source
    class QuerySortCheckPageSource (
        private val query: String?,
        private val sortType: String?,
        private val isChecked:Boolean = false,
        private val dao: IdentityInfoDao
    ) : PagingSource<Int, IdentityInfo>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IdentityInfo> {
            val page = params.key ?: 0  // 첫 페이지는 0으로 설정
            val loadSize = params.loadSize  // 페이지 크기
            val offset = page * loadSize

            Log.d("PagingSource", "Loading page: $page, loadSize: ${params.loadSize}, offset: $offset")

            return try {
                val items:List<IdentityInfo> = dao
                    .readPageQuerySortList(
                        query, sortType, isChecked, loadSize, offset
                    )
                val nextKey = if (items.isEmpty()) null else page + 1

                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            } catch (e: Exception) {
                Log.e("PagingSource", "Error loading data", e)
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, IdentityInfo>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

    }
}
