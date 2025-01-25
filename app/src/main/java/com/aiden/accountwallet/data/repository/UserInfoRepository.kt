package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.model.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserInfoRepository(
    private val userInfoDao: UserInfoDao
) {

    private val LIST_SIZE = 10;

    // Create
    suspend fun addUserInfo(userInfo : UserInfo):Long
            = withContext(Dispatchers.IO) {
        Timber.d("addUserInfo.. %s", userInfo)
        return@withContext userInfoDao.addUserInfo(userInfo)
    }


    // Read
    suspend fun readUserInfoList():List<UserInfo>
            = withContext(Dispatchers.IO){
        return@withContext userInfoDao.readUserInfoList()
    }


}