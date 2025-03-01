package com.aiden.accountwallet.util

import android.content.Context
import androidx.room.Room
import com.aiden.accountwallet.data.dto.Info
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject

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


}