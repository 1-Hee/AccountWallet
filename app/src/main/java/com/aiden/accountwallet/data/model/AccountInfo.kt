package com.aiden.accountwallet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "account_info",
    foreignKeys = [
        ForeignKey(
            entity = IdentityInfo::class,
            parentColumns = arrayOf("info_id"),
            childColumns = arrayOf("fk_info_id"),
            onDelete = ForeignKey.SET_NULL  // 부모키가 삭제되면 자식 외래키를 NULL로 설정
        )
    ]
)
data class AccountInfo (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id")
    var accountId:Long = 0, // 고유 ID            // 기본키

    @ColumnInfo(name = "fk_info_id")
    val fkInfoId: Long?,                         // 외래키, IdentityInfo 테이블의 info_id (nullable로 설정)

    // site_name
    @ColumnInfo(name = "site_name")
    var siteName: String = "",                  // 사이트명

    // user_account
    @ColumnInfo(name = "user_account")
    var userAccount: String = "",               // 사용자 계정

    // user_password
    @ColumnInfo(name = "user_password")
    var userPassword: String = "",              // 비밀 번호

    // ac_created_at
    @ColumnInfo(name = "ac_created_at")
    var acCreatedAt: Date = Date(),            // 계정 생성 일자

    // official_url
    @ColumnInfo(name = "official_url")
    var officialUrl: String = "",              // 공식 사이트

)
