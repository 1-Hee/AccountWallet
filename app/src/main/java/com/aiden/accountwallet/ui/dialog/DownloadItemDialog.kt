package com.aiden.accountwallet.ui.dialog

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.View
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ItemClickListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseBottomSheetDialog
import com.aiden.accountwallet.data.vo.DownloadType
import com.aiden.accountwallet.databinding.DialogDownloadItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownloadItemDialog(
    private val callBack:DownloadItemCallBack? = null
) : BaseBottomSheetDialog<DialogDownloadItemBinding>(),
        ViewClickListener, ItemClickListener<DownloadType> {

    interface DownloadItemCallBack {
        fun onItemSelected(selectedItem: DownloadType, position: Int)
    }

    private var mDownloadTypeList:MutableList<DownloadType> = mutableListOf()
    override fun getDataBindingConfig(): DataBindingConfig {
        mDownloadTypeList.clear()

        val context = requireContext()
        val downTypeArray:Array<String> = context.resources
            .getStringArray(R.array.items_download_type); // 다운로드 유형
        val iconArray: TypedArray = context.resources
            .obtainTypedArray(R.array.icons_download_type) // 아이콘

        for(i in downTypeArray.indices){
            val downTypeStr:String = downTypeArray[i]
            val drawable: Drawable? = iconArray.getDrawable(i)
            val mDownloadType = DownloadType(i, drawable, downTypeStr)
            this.mDownloadTypeList.add(mDownloadType)
        }
        iconArray.recycle()

        return DataBindingConfig(R.layout.dialog_download_item)
            .addBindingParam(BR.downloadTypeList, mDownloadTypeList)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.itemClick, this)
    }

    override fun intiViewModel() {



    }

    override fun initView() {

    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.iv_download_close -> {
                dismiss()
            }
        }
    }

    override fun onItemClick(view: View, position: Int, item: DownloadType) {
        callBack?.onItemSelected(item, position)
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            dismiss()
        }
    }
}