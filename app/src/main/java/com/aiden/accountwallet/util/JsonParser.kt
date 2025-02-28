package com.aiden.accountwallet.util

import com.aiden.accountwallet.data.dto.Info
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject

object JsonParser {
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
}