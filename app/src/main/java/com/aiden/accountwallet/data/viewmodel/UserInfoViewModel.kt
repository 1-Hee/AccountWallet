package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.repository.BaseRoomRepository
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

    override suspend fun readEntity(): List<UserInfo> {
        val list = repository.readEntity()
        Timber.d("vm.. readUserInfoList : %s", list)
        this@UserInfoViewModel.addEntityList(list)
        return list
    }

    override suspend fun editEntity(entity: UserInfo) {

    }

    override suspend fun removeEntity(entityId: Long) {

    }

    override suspend fun removeEntity(entity: UserInfo) {

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


    override fun readAsyncEntity() {
        viewModelScope.launch(Dispatchers.IO) {
            val userInfoList = repository.readEntity()
            Timber.d("vm.. readUserInfoList : %s", userInfoList)

            userInfoList.forEach { info ->
                Timber.d("vm.. info : %s", info)
            }
            this@UserInfoViewModel.addEntityList(userInfoList)

        }
    }

    override fun editAsyncEntity(entity: UserInfo) {

    }

    override fun removeAsyncEntity(entityId: Long) {

    }

    override fun removeAsyncEntity(entity: UserInfo) {

    }

}
