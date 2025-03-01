package com.aiden.accountwallet.data.vo

import java.util.Date

data class ImportUserAccount(
    var infoType:Int = 0,
    var providerName:String = "",
    var createAt: Date = Date(),
    var lastUpdate: Date = Date(),
    var userMemo:String = "",
    var tagColor:String = "93534C",
    var usrAccount:String = "",
    var usrPwd:String = "",
    var acCreateAt: Date = Date(),
    var offUrl:String = ""
)