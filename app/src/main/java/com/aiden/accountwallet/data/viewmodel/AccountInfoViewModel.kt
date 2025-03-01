package com.aiden.accountwallet.data.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.base.repository.ExtraEntityHandler
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.base.viewmodel.SubEntityHandler
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.repository.AccountInfoRepository
import com.aiden.accountwallet.util.Logger
import com.aiden.accountwallet.util.RoomTool.checkInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    override val repository:AccountInfoRepository
) :  BaseRoomViewModel<AccountInfo>(), SubEntityHandler<IdAccountInfo> {


    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    override val extraEntity: ObservableField<IdAccountInfo>
        = ObservableField<IdAccountInfo>()
    override val extraEntityList:  ObservableArrayList<IdAccountInfo>
        = ObservableArrayList<IdAccountInfo>()

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    override suspend fun addEntity(entity: AccountInfo): Long {
        Logger.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@AccountInfoViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntityList(): List<AccountInfo> {
        val list:List<AccountInfo> = repository.readEntityList()
        Logger.d("vm readEntityList : %s", list)
        this@AccountInfoViewModel.addEntityList(list)
        return list
    }

    override suspend fun readEntity(entityId: Long): AccountInfo {
        return AccountInfo(fkInfoId = -1)
    }

    override suspend fun readExtraEntity(entityId: Long): IdAccountInfo {
        if(!checkInstance<ExtraEntityHandler<IdAccountInfo>>(repository)){
            throw IllegalArgumentException()
        }
        Logger.d("vm readExtraEntity id : %d", entityId)
        val entity:IdAccountInfo = (repository as ExtraEntityHandler<IdAccountInfo>)
            .readExtraEntity(entityId)
        Logger.d("vm readExtraEntity : %s", entity)
        this@AccountInfoViewModel.extraEntity.set(entity)
        return entity
    }

    override suspend fun readExtraEntityList(): List<IdAccountInfo> {
        return emptyList()
    }

    override suspend fun editEntity(entity: AccountInfo) {
        Logger.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    override suspend fun removeEntity(entityId: Long) {
        Logger.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeEntity(entity: AccountInfo) {
        Logger.d("vm removeEntity  : %s", entity)
        repository.deleteEntity(entity.accountId)
    }

    override suspend fun removeAll() {
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    override fun addAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm addEntity %s", entity)
            val result = repository.addEntity(entity)
            this@AccountInfoViewModel.setAddEntityStatus(result)
        }
    }

    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list:List<AccountInfo> = repository.readEntityList()
            Logger.d("vm readEntityList : %s", list)
            this@AccountInfoViewModel.addEntityList(list)
        }
    }

    override fun readAsyncEntity(entityId: Long) {

    }

    override fun readAsyncExtraEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!checkInstance<ExtraEntityHandler<IdAccountInfo>>(repository)){
                throw IllegalArgumentException()
            }
            Logger.d("vm readExtraEntity id : %d", entityId)
            val entity:IdAccountInfo = (repository as ExtraEntityHandler<IdAccountInfo>)
                .readExtraEntity(entityId)
            Logger.d("vm readExtraEntity : %s", entity)
            this@AccountInfoViewModel.extraEntity.set(entity)        }
    }

    override fun readAsyncExtraEntityList() {

    }

    override fun editAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm editEntity : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity  : %s", entity)
            repository.deleteEntity(entity.accountId)
        }
    }



    override fun removeAsyncAll() {

    }


}