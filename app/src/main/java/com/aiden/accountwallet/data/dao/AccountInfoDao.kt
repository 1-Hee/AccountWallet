package com.aiden.accountwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo

@Dao
interface AccountInfoDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAccountInfo(accountInfo : AccountInfo) : Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAllAccountInfo(vararg accountInfo : AccountInfo)

    // Read
    @Query("""
        SELECT 
            ai.account_id, ai.fk_info_id,
            ai.user_account, ai.user_password,
            ai.ac_created_at, ai.official_url
        FROM identity_info ii
        JOIN account_info ai 
        ON ii.info_id = ai.fk_info_id
        WHERE ii.status = 0
        ORDER BY ii.created_at DESC;
    """)
    fun readAccountInfoList(): List<AccountInfo>


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
    fun readAllCountInfoList(): List<IdAccountInfo>

    // Update
    @Update
    fun modifyAccountInfo(accountInfo : AccountInfo)

    // Delete
    @Query(
        """
        UPDATE identity_info
        SET status = 1
        WHERE info_id IN (
            SELECT fk_info_id FROM account_info
            WHERE account_id = :accountId
        ); 
        """
    )
    fun removeAccountInfo(accountId: Long)

    @Delete
    fun removeAccountInfo(accountInfo : AccountInfo)

    @Delete
    fun removeAccountInfoList(vararg accountInfo : AccountInfo)

    // delete all
    //    @Query("DELETE FROM sqlite_sequence where name='table_task_registration'")
    //    fun clearAll()

    @Query("DELETE FROM account_info")
    fun deleteAll()
}