package com.aiden.accountwallet.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.ui.adapter.IdentityAdapter
import com.aiden.accountwallet.util.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IdentityInfoRepository @Inject constructor(
    private val identityInfoDao: IdentityInfoDao
) : BaseRoomRepository<IdentityInfo>() {

    // Create
    override suspend fun addEntity(entity: IdentityInfo): Long {
        Logger.d("repo addEntity.. %s", entity)
        return this.identityInfoDao.addIdentityInfo(entity)
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
            pagingSourceFactory = { IdentityAdapter
                .QuerySortCheckPageSource(
                    query,
                    sortType,
                    isChecked,
                    identityInfoDao
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

    suspend fun getIdentityInfoCnt():Long {
        val cnt:Long = identityInfoDao.getIdentityInfoCnt()
        Logger.d("repo getIdentityInfoCnt : %d", cnt)
        return cnt
    }

    // Update
    override suspend fun modifyEntity(entity: IdentityInfo) {
        Logger.d("repo modifyEntity : %s", entity)
        this.identityInfoDao.modifyIdentityInfo(entity)
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