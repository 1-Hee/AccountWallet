package com.aiden.accountwallet.data.vo

data class AccountInfo(
    val keyIndex:Long = -1,
    val providerName:String,
    val typeIdx:Int = 0,
    val typeTag:String,
    val updateDate:String
)
