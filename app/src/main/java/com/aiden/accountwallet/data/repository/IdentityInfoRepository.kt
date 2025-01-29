package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.model.IdentityInfo
import timber.log.Timber

class IdentityInfoRepository(
    private val identityInfoDao : IdentityInfoDao
) : BaseRoomRepository<IdentityInfo>() {

    override suspend fun addEntity(entity: IdentityInfo): Long {
        Timber.d("repo addEntity.. %s", entity)
        return this.identityInfoDao.addIdentityInfo(entity)
    }

    override suspend fun readEntity(): List<IdentityInfo>  {
        val list = this.identityInfoDao.readIdentityInfoList()
        Timber.d("repo readEntity.. %s", list)
        return list
    }

    override suspend fun modifyEntity(entity: IdentityInfo) {
        Timber.d("repo modifyEntity : %s", entity)
        this.identityInfoDao.modifyIdentityInfo(entity)
    }

    override suspend fun deleteEntity(entityId: Long) {
        Timber.d("repo deleteEntity (id) : %s", entityId)
        return this.identityInfoDao.removeIdentityInfo(entityId)
    }

    override suspend fun deleteEntity(entity: IdentityInfo) {
        Timber.d("repo deleteEntity : %s", entity)
        return identityInfoDao.removeIdentityInfo(entity.infoId)
    }
}