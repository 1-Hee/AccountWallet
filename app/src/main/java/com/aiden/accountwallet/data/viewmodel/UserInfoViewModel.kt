package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class UserInfoViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    // db init
    private val repository:UserInfoRepository
    init {
        val userInfoDao = AppDataBase
            .getInstance(application.applicationContext)
            .getUserInfoDao()
        repository = UserInfoRepository(userInfoDao)
    }

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    val userAddStatus:MutableLiveData<Long> = MutableLiveData<Long>(0)
    var userInfoList:ObservableArrayList<UserInfo> = ObservableArrayList<UserInfo>()


    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    suspend fun addUserInfo(userInfo: UserInfo):Long{
        Timber.d("vm.. addUserInfo : %s", userInfo)
        return repository.addUserInfo(userInfo)
    }

    // Read
    suspend fun readUserInfoList():List<UserInfo> {
        Timber.d("vm.. readUserInfoList ...")
        return repository.readUserInfoList()
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    // Create
    fun addAsyncUserInfo(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm.. addUserInfo : %s", userInfo)
            val result =  repository.addUserInfo(userInfo)
            this@UserInfoViewModel.userAddStatus.postValue(result)
            Timber.d("vm.. addUserInfo result : %s", result)
        }
    }

    // Read
    fun readAsyncUserInfoList() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm.. readUserInfoList ...")
            val userInfoList = repository.readUserInfoList()

            userInfoList.forEach { info ->
                Timber.d("vm.. info : %s", info)
            }

            this@UserInfoViewModel.userInfoList.clear()
            this@UserInfoViewModel.userInfoList.addAll(userInfoList)
        }
    }


}
