package com.aiden.accountwallet.data.model

import androidx.room.Embedded

data class IdProductKey(
    @Embedded
    val baseInfo: IdentityInfo,
    @Embedded
    val productKey: ProductKey
)
