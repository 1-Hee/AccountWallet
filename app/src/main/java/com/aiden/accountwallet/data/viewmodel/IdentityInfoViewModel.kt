package com.aiden.accountwallet.data.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.repository.IdentityInfoRepository
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.aiden.accountwallet.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdentityInfoViewModel @Inject constructor(
    override val repository: IdentityInfoRepository
) : BaseRoomViewModel<IdentityInfo>() {


    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    override suspend fun addEntity(entity: IdentityInfo): Long {
        Logger.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@IdentityInfoViewModel.setAddEntityStatus(result)
        return result
    }

    suspend fun addImportUserAccount(item: ImportUserAccount) : Long {
        Logger.d("vm addImportUserAccount %s", item.toString())
        val result = repository.addImportUserAccount(item)
        this@IdentityInfoViewModel.setAddEntityStatus(result)
        return result
    }

    suspend fun addImportProductKey(item : ImportProductKey) : Long {
        Logger.d("vm addImportProductKey %s", item.toString())
        val result = repository.addImportProductKey(item)
        this@IdentityInfoViewModel.setAddEntityStatus(result)
        return result
    }


    // Read
    override suspend fun readEntityList(): List<IdentityInfo> {
        val list:List<IdentityInfo> = repository.readEntityList()
        Logger.d("vm readEntityList : %s", list)
        this@IdentityInfoViewModel.addEntityList(list)
        return list
    }

    // 쿼리 정렬 옵션 적용 레포지토리
    fun readPageQuerySortCheckList(
        query: String?,
        sortType: String?,
        isChecked:Boolean = false
    ): Flow<PagingData<IdentityInfo>> {
        return repository.readPageQuerySortCheckItems(
            query, sortType, isChecked
        ).cachedIn(viewModelScope)
    }


    override suspend fun readEntity(entityId: Long): IdentityInfo {
        return repository.readEntity(entityId)
    }

    suspend fun getIdentityInfoCnt():Long {
        val cnt:Long = repository.getIdentityInfoCnt()
        Logger.d("vm getIdentityInfoCnt : %d", cnt)
        return cnt
    }

    // 백업용 메서드들
    suspend fun readAllAccountList(): List<IdAccountInfo> {
        val accountList:List<IdAccountInfo> = repository.readAllAccountList()
        Logger.i("[READ ALL] repo read size : %d", accountList.size)
        return accountList
    }

    suspend fun readAllProductList(): List<IdProductKey> {
        val productKeyList:List<IdProductKey> = repository.readAllProductList()
        Logger.i("[READ ALL] repo read size : %d", productKeyList.size)
        return productKeyList
    }


    // Update
    override suspend fun editEntity(entity: IdentityInfo) {
        Logger.d("vm editEntity: %s", entity)
        repository.modifyEntity(entity)
    }

    // Delete
    override suspend fun removeEntity(entityId: Long) {
        Logger.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeEntity(entity: IdentityInfo) {
        Logger.d("vm removeEntity : %s", entity)
        repository.deleteEntity(entity)
    }

    override suspend fun removeAll() {
        Logger.d("vm removeAll")
        repository.deleteAll()
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    // Create
    override fun addAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm identityInfo %s", entity)
            val result = repository.addEntity(entity)
            this@IdentityInfoViewModel.setAddEntityStatus(result)
        }
    }

    // Read
    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list:List<IdentityInfo> = repository.readEntityList()
            Logger.d("vm IdentityInfoList : %s", list)
            this@IdentityInfoViewModel.addEntityList(list)
        }
    }

    override fun readAsyncEntity(entityId: Long) {
    }

    // Update
    override fun editAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm edit identityInfo : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    // Delete
    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity : %s", entity)
            repository.deleteEntity(entity)
        }
    }

    override fun removeAsyncAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeAll")
            repository.deleteAll()
        }
    }
}