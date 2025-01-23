package com.aiden.accountwallet.data.dto

data class AlertInfo (
    val alertTitle:String,
    val alertContent:String = "",
    val txtCancel:String = "Cancel",
    val txtOk:String = "OK",
    var dangerFlag:Boolean = false
)
