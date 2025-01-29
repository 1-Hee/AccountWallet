package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.data.dao.AccountInfoDao
import com.aiden.accountwallet.data.model.AccountInfo
import timber.log.Timber

class AccountInfoRepository(
    private val accountInfoDao: AccountInfoDao
) : BaseRoomRepository<AccountInfo>() {

    override suspend fun addEntity(entity: AccountInfo): Long {
        Timber.d("repo addEntity : %s ", entity)
        val result = accountInfoDao.addAccountInfo(entity)
        return result
    }

    override suspend fun readEntity(): List<AccountInfo> {
        val list = accountInfoDao.readAccountInfoList()
        Timber.d("repo eadEntity : %s ", list)
        return list
    }

    override suspend fun modifyEntity(entity: AccountInfo) {
        Timber.d("repo modifyEntity : %s", entity)
        accountInfoDao.modifyAccountInfo(entity)
    }

    override suspend fun deleteEntity(entityId: Long) {
        Timber.d("repo deleteEntity (id) : %s", entityId)
        accountInfoDao.removeAccountInfo(entityId)
    }

    override suspend fun deleteEntity(entity: AccountInfo) {
        Timber.d("repo deleteEntity : %s", entity)
        accountInfoDao.removeAccountInfo(entity.accountId)
    }

}