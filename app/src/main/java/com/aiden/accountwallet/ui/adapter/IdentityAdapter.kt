package com.aiden.accountwallet.ui.adapter

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.adapter.BasePageAdapter
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
    private val itemClickListener: ItemClickListener<DisplayAccountInfo>,
) : BasePageAdapter<IdentityInfo, ItemAccountInfoBinding>(context, COMPARATOR) {

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

    // UI 설정과 데이터 바인딩을 위한 구현 함수들...
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.item_account_info)
    }

    override fun onBindItem(
        binding: ItemAccountInfoBinding,
        position: Int,
        item: IdentityInfo?,
        holder: RecyclerView.ViewHolder
    ) {
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

    // Page Source
    class BasePageSource (
        private val query: String?,
        private val sortType: String?,
        private val isChecked:Boolean = false,
        private val dao: IdentityInfoDao
    ) : PagingSource<Int, IdentityInfo>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IdentityInfo> {
            val page:Int = params.key ?: 0  // 첫 페이지는 0으로 설정
            val loadSize = params.loadSize  // 페이지 크기
            val offset = page * loadSize

            // Logging...
            Logger.d("Loading page: %d, loadSize: %d, offset: %d",
                page, params.loadSize, offset
            )

            return try {
                val items: List<IdentityInfo> = dao
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
                // e.printStackTrace()
                Logger.e("Error loading data : %s", e.message)
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
