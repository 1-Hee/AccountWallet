package com.aiden.accountwallet.data.vo

import java.util.Date

data class ImportProductKey (
    var infoType:Int = 1,
    var providerName:String = "",
    var createAt: Date = Date(),
    var lastUpdate: Date = Date(),
    var userMemo:String = "",
    var tagColor:String = "93534C",
    var productKey:String = "",
    var offUrl:String = ""
)