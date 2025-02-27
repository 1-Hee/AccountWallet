package com.aiden.accountwallet.util

import android.annotation.SuppressLint
import android.content.Context
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.dto.Info
import com.aiden.accountwallet.data.dto.TagInfo
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.vo.DisplayAccountInfo
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.aiden.accountwallet.util.TimeParser.DATE_FORMAT
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat
import timber.log.Timber
import java.text.SimpleDateFormat
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

    // 태그정보 변환을 위함
    private fun getTagInfo(context: Context,  typeIdx:Int, tagColor: String): TagInfo {
        return when(typeIdx) {
            0 -> TagInfo(context.getString(R.string.tag_str_account), tagColor)
            1 -> TagInfo(context.getString(R.string.tag_str_product_key), tagColor)
            else -> TagInfo("None", tagColor)
        }
    }

    fun getDisplayAccountInfo(context: Context, item:IdentityInfo): DisplayAccountInfo {
        val sFormat: SimpleDateFormat = getSimpleDateFormat(DATE_FORMAT)
        val tagInfo:TagInfo = getTagInfo(context, item.infoType, item.tagColor)
        val mAccountInfo = DisplayAccountInfo (
            item.infoId,
            item.providerName,
            item.infoType,
            tagInfo.tagName,
            tagInfo.tagColor,
            sFormat.format(item.createAt)
        )
        Timber.i("item info : %s", mAccountInfo)
        return mAccountInfo
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseIdentityList(
        context: Context, baseInfo : IdentityInfo
    ) : List<Info> {

        val arrInfoType:Array<String> = context.resources
            .getStringArray(R.array.spinner_info_type)
        val arrIdentity:Array<String> = context.resources
            .getStringArray(R.array.items_identity_header)
        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능

        var idx:Int = 0 ; // header 인덱스

        // Set Info Type
        val infoIdx:Int = baseInfo.infoType
        val mInfoList:MutableList<Info> = mutableListOf()
        val strInfoType:String = if(infoIdx > arrInfoType.size) {
            "None"
        } else {
            arrInfoType[infoIdx]
        }

        // set up Identity Info
        mInfoList.add(Info(arrIdentity[idx++], strInfoType))
        mInfoList.add(Info(arrIdentity[idx++], baseInfo.providerName))

        // set date...
        val currDate = Date()  //  기본 값
        var strCreateAt:String
        var strUpdateAt:String
        try {
            strCreateAt = dateFormat.format(baseInfo.createAt)
            strUpdateAt = dateFormat.format(baseInfo.updatedAt)
        } catch (e:Exception){
            strCreateAt = dateFormat.format(currDate)
            strUpdateAt = dateFormat.format(currDate)
        }

        mInfoList.add(Info(arrIdentity[idx++], strCreateAt))
        mInfoList.add(Info(arrIdentity[idx++], strUpdateAt))
        mInfoList.add(Info(arrIdentity[idx++], baseInfo.memo))
        mInfoList.add(Info(arrIdentity[idx], baseInfo.tagColor))

        return mInfoList
    }

    @SuppressLint("SimpleDateFormat")
    fun parseIdAccountInfo(
        context: Context,
        idAccountInfo : IdAccountInfo
    ) : List<Info> {

        val currDate = Date()  //  기본 값
        val arrAccount:Array<String> = context.resources
            .getStringArray(R.array.items_account_header)
        // 현재 날짜 형식 설정
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // 원하는 형식으로 변경 가능

        var idx:Int = 0 ; // header 인덱스
        val mInfoList:MutableList<Info> = mutableListOf()

        val mBaseInfoList:List<Info> = parseIdentityList(context, idAccountInfo.baseInfo)
        mInfoList.addAll(mBaseInfoList) // base info add

        // idAccountInfo.accountInfo.accountId
        // Account Info
        mInfoList.add(Info(arrAccount[idx++], idAccountInfo.accountInfo.userAccount))
        mInfoList.add(Info(arrAccount[idx++], idAccountInfo.accountInfo.userPassword))
        // set Date
        val strAccountCreateAt:String = try {
            dateFormat.format(idAccountInfo.accountInfo.acCreatedAt)
        } catch (e : Exception) {
            dateFormat.format(currDate)
        }
        mInfoList.add(Info(arrAccount[idx++], strAccountCreateAt))
        mInfoList.add(Info(arrAccount[idx], idAccountInfo.accountInfo.officialUrl))

        return mInfoList
    }

    fun parseIdProductKey(
        context: Context,
        idProductKey : IdProductKey
    ) : List<Info> {

        val arrProduct:Array<String> = context.resources
            .getStringArray(R.array.items_product_header)
        // 현재 날짜 형식 설정

        var idx:Int = 0 ; // header 인덱스
        val mInfoList:MutableList<Info> = mutableListOf()

        val mBaseInfoList:List<Info> = parseIdentityList(context, idProductKey.baseInfo)
        mInfoList.addAll(mBaseInfoList) // base info add

        // Product Info
        mInfoList.add(Info(arrProduct[idx++], idProductKey.productKey.productKey))
        mInfoList.add(Info(arrProduct[idx], idProductKey.productKey.officialUrl))


        return mInfoList
    }
}