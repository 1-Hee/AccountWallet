package com.aiden.accountwallet.data.vo

data class DisplayAccountInfo (
    val keyIndex:Long = -1,
    val providerName:String,
    val typeIdx:Int = 0,
    val tagName:String,
    val tagColor:String,
    val updateDate:String
)
