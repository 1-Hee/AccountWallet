package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.data.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class UserInfoViewModel(
    application: Application
) : BaseRoomViewModel<UserInfo>(application) {

    override val repository: BaseRoomRepository<UserInfo>
    // db init
    init {
        val userInfoDao = AppDataBase
            .getInstance(application.applicationContext)
            .getUserInfoDao()
        repository = UserInfoRepository(userInfoDao)
    }

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    override suspend fun addEntity(entity: UserInfo): Long {
        Timber.d("vm.. addUserInfo : %s", entity)
        val result = repository.addEntity(entity)
        this@UserInfoViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntityList(): List<UserInfo> {
        val list:List<UserInfo> = repository.readEntityList()
        Timber.d("vm.. readUserInfoList : %s", list)
        this@UserInfoViewModel.addEntityList(list)
        return list
    }

    override suspend fun readEntity(entityId: Long): UserInfo {

        return UserInfo()
    }

    override suspend fun editEntity(entity: UserInfo) {

    }

    override suspend fun removeEntity(entityId: Long) {

    }

    override suspend fun removeEntity(entity: UserInfo) {

    }

    override suspend fun removeAll() {
        Timber.d("vm removeAll")
        repository.deleteAll()
    }


    // * ----------------------------------------
    // *        ASync Task API
    // * ----------------------------------------

    override fun addAsyncEntity(entity: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm.. addUserInfo : %s", entity)
            val result =  repository.addEntity(entity)
            this@UserInfoViewModel.setAddEntityStatus(result)
        }
    }


    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfoList:List<UserInfo> = repository.readEntityList()
            Timber.d("vm.. readUserInfoList : %s", userInfoList)
            this@UserInfoViewModel.addEntityList(userInfoList)
        }
    }

    override fun readAsyncEntity(entityId: Long) {

    }

    override fun editAsyncEntity(entity: UserInfo) {

    }

    override fun removeAsyncEntity(entityId: Long) {

    }

    override fun removeAsyncEntity(entity: UserInfo) {

    }

    override fun removeAsyncAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeAll")
            repository.deleteAll()
        }
    }

}
