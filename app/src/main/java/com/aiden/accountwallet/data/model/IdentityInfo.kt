package com.aiden.accountwallet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "identity_info",
    foreignKeys = [
        ForeignKey(
            entity = UserInfo::class,
            parentColumns = arrayOf("user_id"),
            childColumns = arrayOf("fk_user_id"),
            onDelete = ForeignKey.SET_NULL  // 부모키가 삭제되면 자식 외래키를 NULL로 설정
        )
    ]
)
data class IdentityInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "info_id")
    var infoId:Long = 0, // 고유 ID          // 기본키

    @ColumnInfo(name = "fk_user_id")
    val fkUserId: Long?,                     // 외래키, UserInfo 테이블의 user_id (nullable로 설정)

    @ColumnInfo(name = "info_type")
    var infoType: Int = 0,                  // 정보 유형 ( 0 = Account , 1 = Product ))

    // provider_name
    @ColumnInfo(name = "provider_name")
    val providerName:String = "",               // 제품 공급사

    @ColumnInfo(name = "created_at")
    var createAt: Date = Date(),            // 첫 등록 일자

    @ColumnInfo(name = "updated_at")
    var updatedAt: Date = Date(),            // 수정 일자

    @ColumnInfo(name = "memo")
    var memo: String = "",

    @ColumnInfo(name = "tag_color")
    var tagColor: String = "93534C",        // 태그 색상

    @ColumnInfo(name = "status")
    var status: Int = 0                     // info status ( 0 = Create, 1 = Delete ... )
)
