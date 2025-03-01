package com.aiden.accountwallet.data.dto

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

data class Info(
    val name:String,
    val value:String
) : BaseJsonModel<Info>() {

    override fun fromJson(jsonString: String): Info {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.fromJson(jsonString, Info::class.java)
    }
}
