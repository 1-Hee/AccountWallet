package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.model.UserInfo
import timber.log.Timber

class UserInfoRepository(
    private val userInfoDao: UserInfoDao
) : BaseRoomRepository<UserInfo>() {

    // Create
    override suspend fun addEntity(entity: UserInfo): Long {
        Timber.d("repo addEntity.. %s", entity)
        return this.userInfoDao.addUserInfo(entity)
    }

    // Read
    override suspend fun readEntity(entityId: Long): UserInfo {
        return UserInfo()
    }

    override suspend fun readEntityList(): List<UserInfo> {
        val list:List<UserInfo> = userInfoDao.readUserInfoList()
        Timber.d("repo readEntityList.. %s", list)
        return list
    }

    suspend fun getLastUserInfo():UserInfo? {
        val userInfo:UserInfo? = userInfoDao.getLastUserInfo()
        Timber.d("repo getLastUserInfo : %s", userInfo)
        return userInfo
    }

    // Update
    override suspend fun modifyEntity(entity: UserInfo) {

    }

    // Delete
    override suspend fun deleteEntity(entityId: Long) {

    }

    override suspend fun deleteEntity(entity: UserInfo) {

    }

    override suspend fun deleteAll() {
        Timber.d("repo deleteAll")
        userInfoDao.disableAll()
    }

}