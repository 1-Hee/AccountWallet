package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.model.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserInfoRepository(
    private val userInfoDao: UserInfoDao
) : BaseRoomRepository<UserInfo>() {

    override suspend fun addEntity(entity: UserInfo): Long {
        Timber.d("repo addEntity.. %s", entity)
        return this.userInfoDao.addUserInfo(entity)
    }

    override suspend fun readEntity(): List<UserInfo> {
        val list = userInfoDao.readUserInfoList()
        Timber.d("repo readEntity.. %s", list)
        return list
    }

    override suspend fun modifyEntity(entity: UserInfo) {

    }

    override suspend fun deleteEntity(entityId: Long) {

    }

    override suspend fun deleteEntity(entity: UserInfo) {

    }
}