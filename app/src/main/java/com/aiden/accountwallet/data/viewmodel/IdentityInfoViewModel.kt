package com.aiden.accountwallet.data.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.repository.IdentityInfoRepository
import com.aiden.accountwallet.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
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
        Timber.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@IdentityInfoViewModel.setAddEntityStatus(result)
        return result
    }

    // Read
    override suspend fun readEntityList(): List<IdentityInfo> {
        val list:List<IdentityInfo> = repository.readEntityList()
        Timber.d("vm readEntityList : %s", list)
        this@IdentityInfoViewModel.addEntityList(list)
        return list
    }

    fun readPageEntityList(): Flow<PagingData<IdentityInfo>> {
        return repository.readPageEntityItems()
            .cachedIn(viewModelScope)
    }


    override suspend fun readEntity(entityId: Long): IdentityInfo {
        return repository.readEntity(entityId)
    }

    suspend fun getIdentityInfoCnt():Long {
        val cnt:Long = (repository as IdentityInfoRepository).getIdentityInfoCnt()
        Timber.d("vm getIdentityInfoCnt : %d", cnt)
        return cnt
    }

    // Update
    override suspend fun editEntity(entity: IdentityInfo) {
        Timber.d("vm editEntity: %s", entity)
        repository.modifyEntity(entity)
    }

    // Delete
    override suspend fun removeEntity(entityId: Long) {
        Timber.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeEntity(entity: IdentityInfo) {
        Timber.d("vm removeEntity : %s", entity)
        repository.deleteEntity(entity)
    }

    override suspend fun removeAll() {
        Timber.d("vm removeAll")
        repository.deleteAll()
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    // Create
    override fun addAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm identityInfo %s", entity)
            val result = repository.addEntity(entity)
            this@IdentityInfoViewModel.setAddEntityStatus(result)
        }
    }

    // Read
    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list:List<IdentityInfo> = repository.readEntityList()
            Timber.d("vm IdentityInfoList : %s", list)
            this@IdentityInfoViewModel.addEntityList(list)
        }
    }

    override fun readAsyncEntity(entityId: Long) {
    }

    // Update
    override fun editAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm edit identityInfo : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    // Delete
    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: IdentityInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity : %s", entity)
            repository.deleteEntity(entity)
        }
    }

    override fun removeAsyncAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeAll")
            repository.deleteAll()
        }
    }
}