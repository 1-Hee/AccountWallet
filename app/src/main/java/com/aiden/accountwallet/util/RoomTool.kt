package com.aiden.accountwallet.util

import android.content.Context
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import java.util.Date

object RoomTool {

    inline fun <reified T> checkInstance(repository: Any):Boolean {
         return repository is T
    }

    fun getIdentifyInfo(
        context:Context,
        typeIdx:Int = -1,
        fkUserId:Long,
        accountVm:AccountFormViewModel,
        productVm:ProductFormViewModel
    ): IdentityInfo {
        var mInfoType:Int = 0
        var mProviderName:String = "None"
        var mMemo:String = ""
        var mTagColor:String = context.getString(R.string.def_tag_color)
        when(typeIdx){
            0 -> {
                mProviderName = accountVm.siteName.get()?:""
                mMemo = accountVm.memo.get()?:""
                mTagColor = accountVm.tagColor.get()?:""
                mInfoType = typeIdx
            }
            1 -> {
                mProviderName = productVm.providerName.get()?:""
                mMemo = productVm.memo.get()?:""
                mTagColor = productVm.tagColor.get()?:""
                mInfoType = typeIdx
            }
        }

        return IdentityInfo(
            fkUserId = fkUserId,
            infoType = mInfoType,
            providerName = mProviderName,
            memo = mMemo,
            tagColor = mTagColor
        )
    }

    fun getAccountInfo(
        infoId: Long?,
        accountVm: AccountFormViewModel
    ): AccountInfo {
        return AccountInfo(
            fkInfoId = infoId,
            userAccount = accountVm.personalAccount.get()?:"",
            userPassword = accountVm.password.get()?:"",
            acCreatedAt = accountVm.createDate.get()?: Date(),
            officialUrl = accountVm.siteUrl.get()?:""
        )
    }

    fun getProductKey(
        infoId: Long?,
        productVm: ProductFormViewModel
    ): ProductKey {
        return ProductKey(
            fkInfoId = infoId,
            productKey = productVm.productKey.get()?:"",
            officialUrl = productVm.siteUrl.get()?:""
        )
    }
}