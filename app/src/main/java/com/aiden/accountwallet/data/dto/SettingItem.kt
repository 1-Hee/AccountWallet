package com.aiden.accountwallet.data.dto

data class SettingItem(
    val name:String,
    var value:String,
    var dangerFlag:Boolean = false
)
