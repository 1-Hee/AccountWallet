package com.aiden.accountwallet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_info")
data class UserInfo (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    var userId:Long = 0, // 고유 ID
    @ColumnInfo(name = "nickname")
    var nickName: String = "", // 닉네임
    @ColumnInfo(name = "created_at")
    var createAt: Date = Date(), // 첫 등록 일자
    @ColumnInfo(name = "user_status")
    var userStatus:Int = 0 // 사용자 상태
)