package com.aiden.accountwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiden.accountwallet.data.model.IdentityInfo
import java.util.Date

@Dao
interface IdentityInfoDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addIdentityInfo(identityInfo : IdentityInfo):Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAllIdentityInfo(vararg identityInfo : IdentityInfo)

    // Read
    @Query("SELECT * FROM identity_info WHERE status = 0")
    fun readIdentityInfoList(): List<IdentityInfo>

    // 계정 only 검색
    // 검색어 필터링
    // 정렬 유형 적용 및 기본 정렬 설정
    @Query("""
        SELECT * FROM identity_info
        WHERE (status = 0 AND provider_name LIKE '%' || :query || '%') 
            AND (:isChecked = 0 OR info_type = 0) 
        ORDER BY 
            CASE WHEN :sortType = 'IDASC' THEN info_id END ASC,
            CASE WHEN :sortType = 'PROVNAME' THEN provider_name END ASC,
            CASE WHEN :sortType = 'DATEASC' THEN created_at END ASC,
            CASE WHEN :sortType = 'DATEDESC' THEN created_at END DESC,
            CASE WHEN :sortType = 'TAGCOLOR' THEN tag_color END ASC,
            CASE WHEN :sortType = 'INFTYPE' THEN info_type END ASC, 
            info_id DESC
            LIMIT :limit OFFSET :offset
    """)
    suspend fun readPageQuerySortList(
        query: String?,
        sortType: String?,
        isChecked:Boolean,
        limit: Int, offset: Int
    ): List<IdentityInfo>


    @Query("SELECT * FROM identity_info WHERE date(created_at) = date(:mDate)")
    fun readIdentityInfoListByDate(mDate: Date): List<IdentityInfo>

    @Query("SELECT * FROM identity_info WHERE info_id = :infoId")
    fun readIdentityInfoListById(infoId:Long): List<IdentityInfo>

    @Query("SELECT * FROM identity_info WHERE info_id = :infoId")
    fun readIdentityInfoById(infoId:Long):IdentityInfo

    @Query("""
        SELECT COUNT(info_id)
        FROM identity_info
        WHERE status = 0;
    """)
    fun getIdentityInfoCnt():Long

    // Update
    @Query("""
        UPDATE identity_info SET
        fk_user_id = :fkUserId,
        info_type = :infoType,
        provider_name = :providerName,
        updated_at = :updatedAt,
        memo = :memo,
        tag_color = :tagColor
        WHERE info_id = :infoId ; 
    """)
    fun modifyIdentityInfo(
        infoId:Long, fkUserId:Long?, infoType:Int, providerName:String,
        updatedAt:Date, memo:String, tagColor:String,
    )

    @Update
    fun modifyIdentityInfo(identityInfo : IdentityInfo)


    // Delete
    @Query("UPDATE identity_info SET status = 1 WHERE info_id = :mInfoId")
    fun removeIdentityInfo(mInfoId : Long)

    @Delete
    fun removeIdentityInfo(identityInfo : IdentityInfo)

    @Delete
    fun removeIdentityInfoList(vararg identityInfo : IdentityInfo)

    // delete all
    //    @Query("DELETE FROM sqlite_sequence where name='table_task_registration'")
    //    fun clearAll()

    @Query("DELETE FROM identity_info")
    fun deleteAll()

    @Query("UPDATE identity_info SET status = 1")
    fun disableAll()
}
