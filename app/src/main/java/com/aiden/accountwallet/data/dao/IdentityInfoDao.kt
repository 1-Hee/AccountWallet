package com.aiden.accountwallet.data.dao

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
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

    @Query("SELECT * FROM identity_info " +
            "WHERE status = 0 " +
            "ORDER BY info_id DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun readPageIdentityInfoList(limit: Int, offset: Int): List<IdentityInfo>

    @Query("SELECT * FROM identity_info " +
            "WHERE status = 0 AND provider_name LIKE '%' || :query || '%' " +
            "ORDER BY info_id DESC " +
            "LIMIT :limit OFFSET :offset")
    suspend fun readPageListByQuery(query:String, limit: Int, offset: Int): List<IdentityInfo>


    // 계정 only 적용한 페이징

    // 정렬 옵션 적용한 페이징


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
