package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.util.Logger
import javax.inject.Inject

class UserInfoRepository @Inject constructor(
    private val userInfoDao: UserInfoDao
) : BaseRoomRepository<UserInfo>() {

    // Create
    override suspend fun addEntity(entity: UserInfo): Long {
        Logger.d("repo addEntity.. %s", entity)
        return this.userInfoDao.addUserInfo(entity)
    }

    // Read
    override suspend fun readEntity(entityId: Long): UserInfo {
        return UserInfo()
    }

    override suspend fun readEntityList(): List<UserInfo> {
        val list:List<UserInfo> = userInfoDao.readUserInfoList()
        Logger.d("repo readEntityList.. %s", list)
        return list
    }

    suspend fun getLastUserInfo():UserInfo? {
        val userInfo:UserInfo? = userInfoDao.getLastUserInfo()
        Logger.d("repo getLastUserInfo : %s", userInfo)
        return userInfo
    }

    // Update
    override suspend fun modifyEntity(entity: UserInfo) {
        Logger.d("repo modifyEntity : %s", entity)
        this.userInfoDao.modifyUserInfo(entity)
    }

    // Delete
    override suspend fun deleteEntity(entityId: Long) {

    }

    override suspend fun deleteEntity(entity: UserInfo) {

    }

    override suspend fun deleteAll() {
        Logger.d("repo deleteAll")
        userInfoDao.disableAll()
    }

}