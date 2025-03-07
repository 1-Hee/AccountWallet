package com.aiden.accountwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import java.util.Date

@Dao
interface IdentityInfoDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addIdentityInfo(identityInfo : IdentityInfo):Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAllIdentityInfo(vararg identityInfo : IdentityInfo)

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAccountInfo(accountInfo: AccountInfo):Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addProductKey(productKey: ProductKey):Long

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

    // 전체 조회하기
    @Query("""
        SELECT 
            ii.info_id, ii.fk_user_id, ii.info_type,
            ii.provider_name, ii.created_at, ii.updated_at, ii.memo,
            ii.tag_color, ii.status,
            ai.account_id, ai.fk_info_id,
            ai.user_account, ai.user_password,
            ai.ac_created_at, ai.official_url
        FROM identity_info ii
        JOIN account_info ai 
        ON ii.info_id = ai.fk_info_id
        WHERE ii.status = 0
        ORDER BY ii.created_at DESC;
    """)
    fun readAllAccountList(): List<IdAccountInfo>

    @Query("""
        SELECT 
            ii.info_id, ii.fk_user_id, ii.info_type,
            ii.provider_name, ii.created_at, ii.updated_at, ii.memo,
            ii.tag_color, ii.status,
            pi.product_id, pi.fk_info_id, pi.product_key, pi.official_url
        FROM identity_info ii
        JOIN product_info pi 
        ON ii.info_id = pi.fk_info_id
        WHERE ii.status = 0
        ORDER BY ii.created_at DESC;
    """)
    fun readAllProductList(): List<IdProductKey>



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
