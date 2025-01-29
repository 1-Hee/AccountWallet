package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.AccountInfo
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.repository.AccountInfoRepository
import com.aiden.accountwallet.data.repository.BaseRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AccountInfoViewModel (
    application: Application
) :  BaseRoomViewModel<AccountInfo>(application)  {


    override val repository: BaseRoomRepository<AccountInfo>
    init {
        val accountInfoDao = AppDataBase
            .getInstance(application.applicationContext)
            .getAccountInfoDao()
        repository = AccountInfoRepository(accountInfoDao)
    }

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    val idAccountInfoList: ObservableArrayList<IdAccountInfo> = ObservableArrayList<IdAccountInfo>()
    val isIdAccountEmpty: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)


    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    override suspend fun addEntity(entity: AccountInfo): Long {
        Timber.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@AccountInfoViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntity(): List<AccountInfo> {
        val list = repository.readEntity()
        Timber.d("vm readEntity : %s", list)
        this@AccountInfoViewModel.addEntityList(list)
        return list
    }

    /*
    suspend fun readIdAccountInfoList():List<IdAccountInfo> {
        val idList = repository.readEntity()
        Timber.d("vm readAccountInfoList : ${idList.toString()}")
        return idList
    }
     */

    override suspend fun editEntity(entity: AccountInfo) {
        Timber.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    override suspend fun removeEntity(entityId: Long) {
        Timber.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeEntity(entity: AccountInfo) {
        Timber.d("vm removeEntity  : %s", entity)
        repository.deleteEntity(entity.accountId)
    }

    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    override fun addAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm addEntity %s", entity)
            val result = repository.addEntity(entity)
            this@AccountInfoViewModel.setAddEntityStatus(result)
        }
    }

    override fun readAsyncEntity() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.readEntity()
            Timber.d("vm readEntity : %s", list)
            this@AccountInfoViewModel.addEntityList(list)
        }
    }

    /*
    fun readIdAccountInfoList():List<IdAccountInfo> {
        val idList = repository.readEntity()
        Timber.d("vm readAccountInfoList : ${idList.toString()}")
        return idList
    }
     */

    override fun editAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm editEntity : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: AccountInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity  : %s", entity)
            repository.deleteEntity(entity.accountId)
        }
    }

}