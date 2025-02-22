package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.model.IdentityInfo
import timber.log.Timber
import javax.inject.Inject

class IdentityInfoRepository @Inject constructor(
    private val identityInfoDao: IdentityInfoDao
) : BaseRoomRepository<IdentityInfo>() {

    // Create
    override suspend fun addEntity(entity: IdentityInfo): Long {
        Timber.d("repo addEntity.. %s", entity)
        return this.identityInfoDao.addIdentityInfo(entity)
    }

    // Read
    override suspend fun readEntityList(): List<IdentityInfo> {
        val list:List<IdentityInfo> = this.identityInfoDao.readIdentityInfoList()
        Timber.d("repo readEntityList.. %s", list)
        return list
    }

    override suspend fun readEntity(entityId: Long): IdentityInfo {
        Timber.d("repo readEntity id : %d", entityId)
        val entity:IdentityInfo = this.identityInfoDao.readIdentityInfoById(entityId)
        Timber.d("repo readEntity : %s", entity)
        return entity
    }

    suspend fun getIdentityInfoCnt():Long {
        val cnt:Long = identityInfoDao.getIdentityInfoCnt()
        Timber.d("repo getIdentityInfoCnt : %d", cnt)
        return cnt
    }

    // Update
    override suspend fun modifyEntity(entity: IdentityInfo) {
        Timber.d("repo modifyEntity : %s", entity)
        this.identityInfoDao.modifyIdentityInfo(entity)
    }

    // Delete
    override suspend fun deleteEntity(entityId: Long) {
        Timber.d("repo deleteEntity (id) : %s", entityId)
        return this.identityInfoDao.removeIdentityInfo(entityId)
    }

    override suspend fun deleteEntity(entity: IdentityInfo) {
        Timber.d("repo deleteEntity : %s", entity)
        return identityInfoDao.removeIdentityInfo(entity.infoId)
    }

    override suspend fun deleteAll() {
        Timber.d("repo deleteAll")
        identityInfoDao.disableAll()
    }

}