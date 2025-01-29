package com.aiden.accountwallet.data.model

import androidx.room.Embedded

data class IdAccountInfo(
    @Embedded
    val baseInfo: IdentityInfo,
    @Embedded
    val accountInfo: AccountInfo
)