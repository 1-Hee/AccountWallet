package com.aiden.accountwallet.data.dto

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

sealed class BaseJsonModel<T> {
    fun toJsonString():String{
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.toJson(this)
    }

    fun toJsonObject(): JsonObject {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.toJsonTree(this).asJsonObject
    }

    abstract fun fromJson(jsonString: String): T
    /*
    fun fromJson(jsonString: String): Info {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .create()
        return gson.fromJson(jsonString, Info::class.java)
    }
     */
}