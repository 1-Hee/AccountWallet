package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.base.repository.ExtraEntityHandler
import com.aiden.accountwallet.data.dao.AccountInfoDao
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.util.Logger
import java.util.Date
import javax.inject.Inject

class AccountInfoRepository @Inject constructor(
    private val accountInfoDao: AccountInfoDao
) : BaseRoomRepository<AccountInfo>(), ExtraEntityHandler<IdAccountInfo> {

    override suspend fun addEntity(entity: AccountInfo): Long {
        Logger.d("repo addEntity : %s ", entity)
        val result = accountInfoDao.addAccountInfo(entity)
        return result
    }

    override suspend fun readEntity(entityId: Long): AccountInfo {
        return AccountInfo(fkInfoId = -1)
    }

    override suspend fun readEntityList(): List<AccountInfo> {
        val list:List<AccountInfo> = accountInfoDao.readAccountInfoList()
        Logger.d("repo eadEntityList : %s ", list)
        return list
    }

    override suspend fun readExtraEntity(entityId: Long): IdAccountInfo {
        Logger.d("repo readCombineEntity id : %d", entityId)
        val entity:IdAccountInfo = accountInfoDao.readIdAccountInfoById(entityId)
        Logger.d("repo readCombineEntity : %s", entity)
        return entity
    }

    override suspend fun readExtraEntityList(): List<IdAccountInfo> {
        return emptyList()
    }

    override suspend fun modifyEntity(entity: AccountInfo) {
        Logger.d("repo modifyEntity : %s", entity)
        val accountId:Long = entity.accountId
        val fkInfoId:Long? = entity.fkInfoId
        val userAccount:String = entity.userAccount
        val userPassword: String = entity.userPassword
        val acCreatedAt: Date = entity.acCreatedAt
        val officialUrl: String = entity.officialUrl

        // accountInfoDao.modifyAccountInfo(entity)
        this.accountInfoDao.modifyAccountInfo(
            accountId, fkInfoId,
            userAccount, userPassword, acCreatedAt, officialUrl
        )
    }

    override suspend fun deleteEntity(entityId: Long) {
        Logger.d("repo deleteEntity (id) : %s", entityId)
        accountInfoDao.removeAccountInfo(entityId)
    }

    override suspend fun deleteEntity(entity: AccountInfo) {
        Logger.d("repo deleteEntity : %s", entity)
        accountInfoDao.removeAccountInfo(entity.accountId)
    }

    override suspend fun deleteAll() {

    }


}