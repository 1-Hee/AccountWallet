package com.aiden.accountwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiden.accountwallet.data.model.UserInfo
import java.util.Date

@Dao
interface UserInfoDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addUserInfo(userInfo: UserInfo):Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAllUserInfo(vararg userInfo: UserInfo)

    // Read
    @Query("SELECT * FROM user_info WHERE user_status = 0")
    fun readUserInfoList(): List<UserInfo>

    @Query("SELECT * FROM user_info WHERE date(created_at) = date(:mDate) AND user_status = 0")
    fun readUserInfoListByDate(mDate: Date): List<UserInfo>

    @Query("SELECT * FROM user_info WHERE user_id = :userId AND user_status = 0")
    fun readUserInfoListById(userId:Long): List<UserInfo>

    @Query("SELECT * FROM user_info WHERE user_status = 0 ORDER BY created_at DESC LIMIT 1")
    fun getLastUserInfo():UserInfo?

    // Update
    @Update
    fun modifyUserInfo(userInfo : UserInfo)

    // Delete
    @Delete
    fun removeUserInfo(userInfo : UserInfo)

    @Delete
    fun removeUserInfoList(vararg userInfo : UserInfo)

    // delete all
    //    @Query("DELETE FROM sqlite_sequence where name='table_task_registration'")
    //    fun clearAll()

    @Query("DELETE FROM user_info")
    fun deleteAll()

    @Query("UPDATE user_info SET user_status = 1")
    fun disableAll()
}