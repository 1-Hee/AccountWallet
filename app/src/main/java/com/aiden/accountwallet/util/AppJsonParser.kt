package com.aiden.accountwallet.util

import android.content.Context
import com.aiden.accountwallet.R
import com.aiden.accountwallet.data.dto.Info
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

object AppJsonParser {
    private fun gsonWithoutHtmlEscaping(): Gson {
        return GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
    }
    fun toInfoJsonArray(infoList:List<Info>): JsonArray {
        val jsonArray = JsonArray()
        infoList.forEach { info ->
            val jsonObj = JsonObject()
            jsonObj.addProperty("name", info.name)
            jsonObj.addProperty("value", info.value)
            jsonArray.add(jsonObj)
        }
        return jsonArray
    }
    fun toJsonArray(jsonObjList:List<JsonObject>): JsonArray {
        val jsonArray = JsonArray()
        jsonObjList.forEach { jsonObj ->
            jsonArray.add(jsonObj)
        }
        return jsonArray
    }

    fun toJsonObject(key:String, value:String): JsonObject {
        val backupJson = JsonObject()
        backupJson.add(
            key,
            gsonWithoutHtmlEscaping().fromJson(
                value,
                JsonObject::class.java
            )
        )
        return backupJson
    }

    fun getJsonObject(itemList : List<Info>) : JsonObject {
        val mJsonObject = JsonObject()
        itemList.forEach { item ->
            mJsonObject.addProperty(item.name, item.value)
        }
        return mJsonObject
    }

    fun getJsonObject(context: Context, item: IdAccountInfo) : JsonObject {
        val mProductInfoList:List<Info> = RoomTool.parseIdAccountInfo(context, item)
        return getJsonObject(mProductInfoList)
    }


    fun getJsonObject(context: Context, item: IdProductKey) : JsonObject {
        val mProductInfoList:List<Info> = RoomTool.parseIdProductKey(context, item)
        return getJsonObject(mProductInfoList)
    }

    // * ----------------------------------------------------
    // *    Import Json Data ...
    // * ----------------------------------------------------

    interface ParseListener {
        fun onUpdateStatus(status:String)
        fun onUpdateProgress(progress:Int)
        fun isStopTask():Boolean
        fun onParseFail()
        fun onParseFinish()
    }

    interface EntityImportHandler<T> {
        fun onRequest(entity:T)
    }

    fun parseJsonData(
        context: Context, readData: String,
        userAccountListener: EntityImportHandler<ImportUserAccount>,
        productKeyListener: EntityImportHandler<ImportProductKey>,
        callBack: ParseListener? = null,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            var currentProgress = 15;
            var statusMsg:String = context.getString(R.string.status_ready_load_data)
            callBack?.onUpdateProgress(currentProgress)
            callBack?.onUpdateStatus(statusMsg)

            val jsonString:String = readData
            val jsonObject:JsonObject = Gson()
                .fromJson(jsonString, JsonObject::class.java) // JSON 파싱

            val mAccountTag:String = context.getString(R.string.key_user_account)
            val mProductTag:String = context.getString(R.string.key_product)

            // "UserAccount" 배열 꺼내기
            val userAccounts: JsonArray = jsonObject
                .getAsJsonArray(mAccountTag)
            currentProgress += 5

            statusMsg = context.getString(R.string.status_load_user_account)
            callBack?.onUpdateProgress(currentProgress)
            callBack?.onUpdateStatus(statusMsg)

            // ProductKey
            val productKey: JsonArray = jsonObject
                .getAsJsonArray(mProductTag)

            currentProgress += 5
            statusMsg = context.getString(R.string.status_load_product_key)
            callBack?.onUpdateProgress(currentProgress)
            callBack?.onUpdateStatus(statusMsg)

            val progressUnit:Int = (100 - currentProgress)
            val totalSize:Int = productKey.size() + userAccounts.size()
            val progress:Int = (progressUnit / totalSize)

            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()) // 날짜 형식 지정

            val arrIdentity:Array<String> = context.resources
                .getStringArray(R.array.items_identity_header)

            val arrAccount:Array<String> = context.resources
                .getStringArray(R.array.items_account_header)

            val arrProduct:Array<String> = context.resources
                .getStringArray(R.array.items_account_header)

            val delayGap:Long = 0
            statusMsg = context.getString(R.string.status_import_user_account)
            callBack?.onUpdateProgress(currentProgress)
            callBack?.onUpdateStatus(statusMsg)

            var importAccount : ImportUserAccount
            userAccounts.forEach { element ->
                val userObject:JsonObject = element.asJsonObject
                currentProgress += progress
                callBack?.onUpdateProgress(currentProgress)

                if(callBack?.isStopTask() == true){
                    callBack.onParseFinish()
                    return@launch
                }

                importAccount = ImportUserAccount()
                userObject.entrySet().forEach { entry ->
                    val key = entry.key
                    val value = entry.value

                    when(key){
                        arrIdentity[0] -> {
                            importAccount.infoType = value.asInt
                        }
                        arrIdentity[1] -> {
                            importAccount.providerName = value.asString
                        }
                        arrIdentity[2] -> {
                            val stringValue:String = value.asString
                            if (stringValue.matches(Regex(
                                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                ))) { // 날짜 형식 변환
                                importAccount.createAt = dateFormat.parse(stringValue)
                            }
                        }
                        arrIdentity[3] -> {
                            val stringValue:String = value.asString
                            if (stringValue.matches(Regex(
                                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                ))) { // 날짜 형식 변환
                                importAccount.lastUpdate = dateFormat.parse(stringValue)
                            }
                        }
                        arrIdentity[4] -> {
                            importAccount.userMemo = value.asString
                        }
                        arrIdentity[5] -> {
                            importAccount.tagColor = value.asString
                        }
                        arrAccount[0] -> {
                            importAccount.usrAccount = value.asString
                        }
                        arrAccount[1] -> {
                            importAccount.usrPwd = value.asString
                        }
                        arrAccount[2] -> {
                            val stringValue:String = value.asString
                            if (stringValue.matches(Regex(
                                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                ))) { // 날짜 형식 변환
                                importAccount.acCreateAt = dateFormat.parse(stringValue)
                            }
                        }
                        arrAccount[3] -> {
                            importAccount.offUrl = value.asString
                        }
                    }
                }
                userAccountListener.onRequest(importAccount)
                delay(delayGap)
            }

            statusMsg = context.getString(R.string.status_import_product_key)
            callBack?.onUpdateStatus(statusMsg)

            var importProductKey : ImportProductKey

            productKey.forEach { element ->
                val productObject:JsonObject = element.asJsonObject
                currentProgress += progress
                callBack?.onUpdateProgress(currentProgress)
                if(callBack?.isStopTask() == true){
                    callBack.onParseFinish()
                    return@launch
                }

                importProductKey = ImportProductKey()
                productObject.entrySet().forEach { entry ->
                    val key = entry.key
                    val value = entry.value

                    // 데이터 타입 판별
                    when(key){
                        arrIdentity[0] -> {
                            importProductKey.infoType = value.asInt
                        }
                        arrIdentity[1] -> {
                            importProductKey.providerName = value.asString
                        }
                        arrIdentity[2] -> {
                            val stringValue:String = value.asString
                            if (stringValue.matches(Regex(
                                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                ))) { // 날짜 형식 변환
                                importProductKey.createAt = dateFormat.parse(stringValue)
                            }
                        }
                        arrIdentity[3] -> {
                            val stringValue:String = value.asString
                            if (stringValue.matches(Regex(
                                    "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                ))) { // 날짜 형식 변환
                                importProductKey.lastUpdate = dateFormat.parse(stringValue)
                            }
                        }
                        arrIdentity[4] -> {
                            importProductKey.userMemo = value.asString
                        }
                        arrIdentity[5] -> {
                            importProductKey.tagColor = value.asString
                        }
                        arrAccount[0] -> {
                            importProductKey.productKey = value.asString
                        }
                        arrProduct[1] -> {
                            importProductKey.offUrl = value.asString
                        }
                    }
                }
                productKeyListener.onRequest(importProductKey)
                delay(delayGap)
            }

            if(callBack?.isStopTask() == false){
                callBack.onParseFinish()
                return@launch
            }
        }
    }

}