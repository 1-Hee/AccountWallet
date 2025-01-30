package com.aiden.accountwallet.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Note
// 데이터베이스에 새로운 테이블을 추가하거나 변동이 있을 경우
// Migration을 거쳐야 기존 데이터가 소실되지 않음.
// 또한, 아래와 같이 왜래키가 부모키의 기본키인 경우 왜래키에도 인덱스 처리가 되어야 함
// 이 경우에 테이블 생성 SQL과 별개로 인덱싱에 대한 쿼리

@Entity(
    tableName = "product_key",
    foreignKeys = [
        ForeignKey(
            entity = IdentityInfo::class,
            parentColumns = arrayOf("info_id"),
            childColumns = arrayOf("fk_info_id"),
            onDelete = ForeignKey.SET_NULL  // 부모키가 삭제되면 자식 외래키를 NULL로 설정
        )
    ],
    indices = [Index(value = ["fk_info_id"])]  // fk_info_id 컬럼에 인덱스 추가
)
data class ProductKey (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    var productId:Long = 0, // 고유 ID            // 기본키

    @ColumnInfo(name = "fk_info_id")
    val fkInfoId: Long?,                         // 외래키, IdentityInfo 테이블의 info_id (nullable로 설정)

    // product_key
    @ColumnInfo(name = "product_key")
    val productKey:String = "",                 // 제품키

    // official_url
    @ColumnInfo(name = "official_url")
    var officialUrl: String = "",              // 공식 사이트
)
