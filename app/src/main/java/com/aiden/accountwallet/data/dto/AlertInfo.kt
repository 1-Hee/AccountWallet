package com.aiden.accountwallet.data.dto

data class AlertInfo (
    val alertTitle:String,
    val alertContent:String = "",
    var txtCancel:String = "Cancel",
    var txtOk:String = "OK",
    var flag:Boolean = false
)
