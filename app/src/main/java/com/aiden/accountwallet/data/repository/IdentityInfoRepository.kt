package com.aiden.accountwallet.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.aiden.accountwallet.ui.adapter.IdentityAdapter.BasePageSource
import com.aiden.accountwallet.util.Logger
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class IdentityInfoRepository @Inject constructor(
    private val identityInfoDao: IdentityInfoDao
) : BaseRoomRepository<IdentityInfo>() {

    // Create
    override suspend fun addEntity(entity: IdentityInfo): Long {
        Logger.d("repo addEntity.. %s", entity)
        return this.identityInfoDao.addIdentityInfo(entity)
    }

    suspend fun addImportUserAccount(item: ImportUserAccount) : Long  {
        Logger.d("repo addImportUserAccount.. %s", item.toString())
        val parentItem = IdentityInfo(
            fkUserId = null,
            infoType =  item.infoType,
            providerName = item.providerName,
            createAt = item.createAt,
            updatedAt = item.lastUpdate,
            memo = item.userMemo,
            tagColor = item.tagColor
        );
        val parentId:Long = identityInfoDao.addIdentityInfo(parentItem)

        val childItem:AccountInfo = AccountInfo(
            fkInfoId = parentId,
            userAccount = item.usrAccount,
            userPassword = item.usrPwd,
            acCreatedAt = item.acCreateAt,
            officialUrl = item.offUrl
        )

        identityInfoDao.addAccountInfo(childItem)
        // return this.identityInfoDao.addImportUserAccount(account)
        return parentId
    }


    suspend fun addImportProductKey(item : ImportProductKey) : Long {
        val parentItem = IdentityInfo(
            fkUserId = null,
            infoType =  item.infoType,
            providerName = item.providerName,
            createAt = item.createAt,
            updatedAt = item.lastUpdate,
            memo = item.userMemo,
            tagColor = item.tagColor
        );
        val parentId:Long = identityInfoDao.addIdentityInfo(parentItem)

        val childItem:ProductKey = ProductKey(
            fkInfoId = parentId,
            productKey = item.productKey,
            officialUrl = item.offUrl
        )

        identityInfoDao.addProductKey(childItem)
        // return this.identityInfoDao.addImportUserAccount(account)
        return parentId
    }


    // Read
    override suspend fun readEntityList(): List<IdentityInfo> {
        val list:List<IdentityInfo> = this.identityInfoDao.readIdentityInfoList()
        Logger.d("repo readEntityList.. %s", list)
        return list
    }

    // 검색어, 정렬, 적용 레포지토리
    fun readPageQuerySortCheckItems(
        query: String?,
        sortType: String?,
        isChecked:Boolean = false
    ): Flow<PagingData<IdentityInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = {
                BasePageSource(
                    query, sortType, isChecked, identityInfoDao
                )
            }
        ).flow
    }


    override suspend fun readEntity(entityId: Long): IdentityInfo {
        Logger.d("repo readEntity id : %d", entityId)
        val entity:IdentityInfo = this.identityInfoDao.readIdentityInfoById(entityId)
        Logger.d("repo readEntity : %s", entity)
        return entity
    }

    fun getIdentityInfoCnt():Long {
        val cnt:Long = identityInfoDao.getIdentityInfoCnt()
        Logger.d("repo getIdentityInfoCnt : %d", cnt)
        return cnt
    }

    // 백업용 메서드들
    fun readAllAccountList(): List<IdAccountInfo> {
        val accountList:List<IdAccountInfo> = identityInfoDao.readAllAccountList()
        Logger.i("[READ ALL] repo read size : %d", accountList.size)
        return accountList
    }

    fun readAllProductList(): List<IdProductKey> {
        val productKeyList:List<IdProductKey> = identityInfoDao.readAllProductList()
        Logger.i("[READ ALL] repo read size : %d", productKeyList.size)
        return productKeyList
    }


    // Update
    override suspend fun modifyEntity(entity: IdentityInfo) {
        Logger.d("repo modifyEntity : %s", entity)
        val infoId:Long = entity.infoId
        val fkUserId:Long? = entity.fkUserId
        val infoType:Int = entity.infoType
        val providerName:String = entity.providerName
        val updatedAt: Date = Date()
        val memo:String = entity.memo
        val tagColor:String = entity.tagColor
        // this.identityInfoDao.modifyIdentityInfo(entity)
        this.identityInfoDao.modifyIdentityInfo(
            infoId, fkUserId, infoType,
            providerName, updatedAt, memo, tagColor
        )
    }

    // Delete
    override suspend fun deleteEntity(entityId: Long) {
        Logger.d("repo deleteEntity (id) : %s", entityId)
        return this.identityInfoDao.removeIdentityInfo(entityId)
    }

    override suspend fun deleteEntity(entity: IdentityInfo) {
        Logger.d("repo deleteEntity : %s", entity)
        return identityInfoDao.removeIdentityInfo(entity.infoId)
    }

    override suspend fun deleteAll() {
        Logger.d("repo deleteAll")
        identityInfoDao.disableAll()
    }

}