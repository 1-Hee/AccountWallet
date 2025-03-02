package com.aiden.accountwallet.data.viewmodel

import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.repository.UserInfoRepository
import com.aiden.accountwallet.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    override val repository: UserInfoRepository
) : BaseRoomViewModel<UserInfo>() {

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    override suspend fun addEntity(entity: UserInfo): Long {
        Logger.d("vm.. addUserInfo : %s", entity)
        val result = repository.addEntity(entity)
        this@UserInfoViewModel.setAddEntityStatus(result)
        return result
    }

    // Read
    override suspend fun readEntityList(): List<UserInfo> {
        val list:List<UserInfo> = repository.readEntityList()
        Logger.d("vm.. readUserInfoList : %s", list)
        this@UserInfoViewModel.addEntityList(list)
        return list
    }

    override suspend fun readEntity(entityId: Long): UserInfo {
        return UserInfo()
    }

    suspend fun getLastUserInfo():UserInfo? {
        val userInfo:UserInfo? = repository.getLastUserInfo()
        this.entity.set(userInfo)
        Logger.d("vm getLastUserInfo : %s", userInfo)
        return userInfo
    }

    // Update
    override suspend fun editEntity(entity: UserInfo) {
        Logger.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    // Delete
    override suspend fun removeEntity(entityId: Long) {

    }

    override suspend fun removeEntity(entity: UserInfo) {

    }

    override suspend fun removeAll() {
        Logger.d("vm removeAll")
        repository.deleteAll()
    }

    // * ----------------------------------------
    // *        ASync Task API
    // * ----------------------------------------

    // Create
    override fun addAsyncEntity(entity: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm.. addUserInfo : %s", entity)
            val result =  repository.addEntity(entity)
            this@UserInfoViewModel.setAddEntityStatus(result)
        }
    }

    // Read
    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfoList:List<UserInfo> = repository.readEntityList()
            Logger.d("vm.. readUserInfoList : %s", userInfoList)
            this@UserInfoViewModel.addEntityList(userInfoList)
        }
    }

    override fun readAsyncEntity(entityId: Long) {

    }

    fun getAsyncLastUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfo:UserInfo? = (repository as UserInfoRepository).getLastUserInfo()
            Logger.d("repo getLastUserInfo : %s", userInfo)
            this@UserInfoViewModel.entity.set(userInfo)
        }
    }

    // Update
    override fun editAsyncEntity(entity: UserInfo) {

    }

    // Delete
    override fun removeAsyncEntity(entityId: Long) {

    }

    override fun removeAsyncEntity(entity: UserInfo) {

    }

    override fun removeAsyncAll() {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeAll")
            repository.deleteAll()
        }
    }

}
